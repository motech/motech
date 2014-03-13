package org.motechproject.mds.util;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.osgi.framework.Bundle;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

public final class AnnotationsUtil extends AnnotationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationsUtil.class);

    // we hold on to the default classloaders, and always add a bundle classlaoder
    private static final ClassLoader[] DEFAULT_REFLECTION_CLASS_LOADERS =
            Arrays.copyOf(ClasspathHelper.defaultClassLoaders, ClasspathHelper.defaultClassLoaders.length);

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

    public static List<AnnotatedElement> getAnnotatedMembers(Class<? extends Annotation> aClass,
                                                             Class<?> clazz, Predicate method,
                                                             Predicate field) {
        List<Member> members = MemberUtil.getMembers(clazz, method, field);
        return getAnnotatedMembers(aClass, members);
    }

    public static List<AnnotatedElement> getAnnotatedMembers(Class<? extends Annotation> aClass,
                                                             List<Member> members) {
        List<AnnotatedElement> list = new ArrayList<>();

        for (Member m : members) {
            Iterator<AnnotatedElement> iterator = list.iterator();
            boolean found = false;

            while (iterator.hasNext()) {
                AnnotatedElement ae = iterator.next();
                String candidateName = MemberUtil.getFieldName(m);
                String elementName = MemberUtil.getFieldName((Member) ae);

                if (equalsIgnoreCase(candidateName, elementName)) {
                    Annotation candidateAnnotation = getAnnotation((AnnotatedElement) m, aClass);
                    Annotation elementAnnotation = getAnnotation(ae, aClass);

                    found = !(candidateAnnotation != null && elementAnnotation == null);

                    if (!found) {
                        iterator.remove();
                    }
                }
            }

            if (!found) {
                list.add((AnnotatedElement) m);
            }
        }

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

        // we add the ability to load classes from the bundle
        // we are synchronized so this is fairly ok, moving to a new version of reflections
        // would be better though
        ClasspathHelper.defaultClassLoaders = (ClassLoader[]) ArrayUtils.add(DEFAULT_REFLECTION_CLASS_LOADERS,
                new BundleClassLoaderWrapper(bundle));

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

    /**
     * A hack classLoader for loading classes from the processed bundle.
     */
    private static class BundleClassLoaderWrapper extends ClassLoader {

        private Bundle bundle;

        public BundleClassLoaderWrapper(Bundle bundle) {
            this.bundle = bundle;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            return bundle.loadClass(name);
        }
    }
}
