package org.motechproject.mds.util;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.osgi.framework.Bundle;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.springframework.util.ReflectionUtils.FieldCallback;
import static org.springframework.util.ReflectionUtils.FieldFilter;
import static org.springframework.util.ReflectionUtils.MethodCallback;
import static org.springframework.util.ReflectionUtils.MethodFilter;

public final class AnnotationsUtil extends AnnotationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationsUtil.class);

    private AnnotationsUtil() {
    }

    public static List<Class> getClasses(Class<? extends Annotation> annotation, Bundle bundle) {
        LOGGER.debug("Scanning bundle: {}", bundle.getSymbolicName());
        LOGGER.debug("Searching for classes with annotations: {}", annotation.getName());

        Reflections reflections = configureReflection(bundle, new TypeAnnotationsScanner());
        Set<String> set = reflections.getStore().getTypesAnnotatedWith(annotation.getName());
        List<Class> classes = new ArrayList<>(set.size());

        for (String className : set) {
            try {
                classes.add(bundle.loadClass(className));
            } catch (ClassNotFoundException e) {
                LOGGER.error(
                        "Failed to load class {} from bundle {}",
                        className, bundle.getSymbolicName()
                );
            }
        }

        LOGGER.debug("Searched for classes with annotations: {}", annotation.getName());
        LOGGER.trace("Found {} classes with annotations: {}", classes.size(), annotation.getName());

        return classes;
    }

    public static List<Method> getMethods(Class<? extends Annotation> annotation, Bundle bundle) {
        LOGGER.debug("Searching for methods with annotations: {}", annotation.getName());

        Reflections reflections = configureReflection(bundle, new MethodAnnotationsScanner());
        List<Method> methods = new ArrayList<>(reflections.getMethodsAnnotatedWith(annotation));

        LOGGER.debug("Searched for methods with annotations: {}", annotation.getName());
        LOGGER.trace("Found {} methods with annotations: {}", methods.size(), annotation.getName());

        return methods;
    }

    public static List<AnnotatedElement> getMembers(Class<? extends Annotation> annotation,
                                                    Class<?> clazz, Predicate methodPredicate,
                                                    Predicate fieldPredicate) {
        List<AnnotatedElement> list = new ArrayList<>();

        Callback callback = new Callback(list, annotation);
        Filter filter = new Filter(methodPredicate, fieldPredicate);

        ReflectionUtils.doWithFields(clazz, callback, filter);
        ReflectionUtils.doWithMethods(clazz, callback, filter);

        return list;
    }

    public static String getAnnotationValue(Annotation annotation, String property,
                                            String... defaultValues) {
        Object value = getValue(annotation, property);
        String valueAsString = null;

        if (null != value) {
            valueAsString = new ToStringBuilder(value, ToStringStyle.SIMPLE_STYLE)
                    .append(value).toString();
        }

        for (String defaultValue : defaultValues) {
            valueAsString = defaultIfBlank(valueAsString, defaultValue);
        }

        return valueAsString;
    }

    public static boolean hasAnnotation(AnnotatedElement element,
                                        Class<? extends Annotation> annotation) {
        return getAnnotation(element, annotation) != null;
    }

    public static boolean hasProperty(Annotation annotation, String property) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        Method method;

        try {
            method = annotationType.getDeclaredMethod(property, new Class[0]);
        } catch (NoSuchMethodException e) {
            method = null;
        }

        return method != null;
    }

    private static Reflections configureReflection(Bundle bundle, Scanner... scanners) {
        ConfigurationBuilder configuration = new ConfigurationBuilder();
        configuration.addUrls(resolveLocation(bundle));
        configuration.setScanners(scanners);

        LOGGER.debug("Initialized Reflections for resolved file location.");
        return new Reflections(configuration);
    }

    private static URL resolveLocation(Bundle bundle) {
        LOGGER.debug(
                "Resolving the following file location for bundle: {}",
                bundle.getSymbolicName()
        );
        URL resolved;

        try {
            resolved = new URL(bundle.getLocation());
        } catch (MalformedURLException e) {
            LOGGER.error("Failed to resolve URL from file: {}", bundle.getLocation());
            resolved = null;
        }

        LOGGER.debug(
                "Resolved the following file location for bundle: {}",
                bundle.getSymbolicName()
        );

        return resolved;
    }

    private static final class Callback implements MethodCallback, FieldCallback {
        private Class<? extends Annotation> annotation;
        private List<AnnotatedElement> elements;

        protected Callback(List<AnnotatedElement> elements,
                           Class<? extends Annotation> annotation) {
            this.annotation = annotation;
            this.elements = elements;
        }

        @Override
        public void doWith(Method method) {
            add(method);
        }

        @Override
        public void doWith(java.lang.reflect.Field field) {
            add(field);
        }

        private void add(AnnotatedElement candidate) {
            Iterator<AnnotatedElement> iterator = elements.iterator();
            boolean found = false;

            while (iterator.hasNext()) {
                AnnotatedElement element = iterator.next();
                String candidateName = MemberUtil.getFieldName(candidate);
                String elementName = MemberUtil.getFieldName(element);

                if (equalsIgnoreCase(candidateName, elementName)) {
                    Annotation candidateAnnotation = getAnnotation(candidate, annotation);
                    Annotation elementAnnotation = getAnnotation(element, annotation);

                    found = !(candidateAnnotation != null && elementAnnotation == null);

                    if (!found) {
                        iterator.remove();
                    }
                }
            }

            if (!found) {
                elements.add(candidate);
            }
        }

    }

    private static final class Filter implements MethodFilter, FieldFilter {
        private Predicate methodPredicate;
        private Predicate fieldPredicate;

        private Filter(Predicate methodPredicate, Predicate fieldPredicate) {
            this.methodPredicate = methodPredicate == null ? new TruePredicate() : methodPredicate;
            this.fieldPredicate = fieldPredicate == null ? new TruePredicate() : fieldPredicate;
        }

        @Override
        public boolean matches(Method method) {
            return methodPredicate.evaluate(method);
        }

        @Override
        public boolean matches(java.lang.reflect.Field field) {
            return fieldPredicate.evaluate(field);
        }

    }

    private static class TruePredicate implements Predicate {

        @Override
        public boolean evaluate(Object object) {
            return true;
        }

    }

}
