package org.motechproject.mds.reflections;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.motechproject.mds.annotations.internal.vfs.MvnUrlType;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.util.MemberUtil;
import org.osgi.framework.Bundle;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.vfs.Vfs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;

/**
 * The <code>ReflectionsUtil</code> class is a helper class, providing handy
 * methods, allowing to search given bundle for all kind of classes, methods,
 * parameters or fields. ReflectionsUtil allows to find only classes and members
 * that have got a certain annotation. Additionally, a whole configuration for
 * the Reflections library is provided.
 */
public final class ReflectionsUtil extends AnnotationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionsUtil.class);

    // we hold on to the default classloaders, and always add a bundle classlaoder
    private static final ClassLoader[] DEFAULT_REFLECTION_CLASS_LOADERS =
            Arrays.copyOf(ClasspathHelper.defaultClassLoaders, ClasspathHelper.defaultClassLoaders.length);

    private ReflectionsUtil() {
    }

    /**
     * Finds all interfaces that extend the {@link MotechDataService} interface.
     *
     * @param bundle A bundle to look in.
     * @return A list of classes that extend the MotechDataService interface.
     */
    public static List<Class<? extends MotechDataService>> getMdsInterfaces(Bundle bundle) {
        LOGGER.debug("Looking for MDS interfaces in bundle: {}", bundle.getSymbolicName());

        Reflections reflections = configureReflection(bundle, new SubTypesScanner());
        Set<Class<? extends MotechDataService>> set = reflections.getSubTypesOf(MotechDataService.class);

        return new ArrayList<>(set);
    }

    public static List<Class> getClasses(Class<? extends Annotation> annotation, Bundle bundle) {
        LOGGER.debug("Scanning bundle: {}", bundle.getSymbolicName());
        LOGGER.debug("Searching for classes with annotations: {}", annotation.getName());

        Reflections reflections = configureReflection(bundle, new TypeAnnotationsScanner());
        Set<String> set = reflections.getStore().getTypesAnnotatedWith(annotation.getName());
        List<Class> classes = new ArrayList<>(set.size());

        // in order to prevent processing of user defined or auto generated fields
        // we have to load the bytecode from the jar and define the class in a temporary
        // classLoader
        BundleLoader bundleLoader = new BundleLoader(bundle);

        for (String className : set) {
            Class<?> clazz = bundleLoader.loadClass(className);
            classes.add(clazz);
        }

        for (Class<?> clazz : classes) {
            bundleLoader.loadFieldsAndMethodsOfClass(clazz);
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

    public static List<Member> getFilteredMembers(Class<?> clazz, Predicate memberPredicate) {
        List<Member> members = MemberUtil.getMembers(clazz, memberPredicate);
        Set<Member> membersSet = new TreeSet<>(new Comparator<Member>() {
            @Override
            public int compare(Member member1, Member member2) {
                String member1Name = MemberUtil.getFieldName(member1);
                String member2Name = MemberUtil.getFieldName(member2);
                return ObjectUtils.compare(member1Name, member2Name);
            }
        });
        membersSet.addAll(members);
        return new ArrayList<>(membersSet);
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

    public static boolean hasAnnotationSelfOrAccessor(AnnotatedElement object,
                                                      Class<? extends Annotation> annotation) {
        for (AccessibleObject accessibleObject : MemberUtil.getFieldAndAccessorsForElement((AccessibleObject) object)) {
            Member asMember = (Member) accessibleObject;
            if (ReflectionsUtil.hasAnnotationClassLoaderSafe(accessibleObject, asMember.getDeclaringClass(), annotation)) {
                return true;
            }
        }
        return false;
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

    public static <T extends Annotation> boolean hasAnnotationClassLoaderSafe(AnnotatedElement ae,
                                                                          Class<?> clazz, Class<T> annotation) {
        Class<T> annotationClass = ReflectionsUtil.getAnnotationClass(clazz, annotation);
        return ReflectionsUtil.hasAnnotation(ae, annotationClass);
    }

    public static <T extends Annotation> T getAnnotationClassLoaderSafe(AnnotatedElement ae,
                                                                        Class<?> clazz, Class<T> annotation) {
        Class<T> annotationClass = ReflectionsUtil.getAnnotationClass(clazz, annotation);
        return ReflectionsUtil.getAnnotation(ae, annotationClass);
    }

    public static <T extends Annotation> Class<T> getAnnotationClass(Class<?> clazz, Class<T> annotation) {
        ClassLoader clazzClassLoader = clazz.getClassLoader();
        ClassLoader annotationClassLoader = annotation.getClassLoader();

        if (null != clazzClassLoader && !clazzClassLoader.equals(annotationClassLoader)) {
            try {
                return (Class<T>) clazzClassLoader.loadClass(annotation.getName());
            } catch (ClassNotFoundException e) {
                // if the classloader for the given class cannot find the annotation class
                // then the given annotation class will be returned
            }
        }

        return annotation;
    }

    private static Reflections configureReflection(Bundle bundle, Scanner... scanners) {
        ConfigurationBuilder configuration = new ConfigurationBuilder();
        configuration.addUrls(resolveLocation(bundle));
        configuration.setScanners(scanners);

        // we add the ability to load classes from the bundle
        // we are synchronized so this is fairly ok, moving to a new version of reflections
        // would be better though
        ClasspathHelper.defaultClassLoaders = (ClassLoader[]) ArrayUtils.add(DEFAULT_REFLECTION_CLASS_LOADERS,
                new BundleClassLoaderImplWrapper(bundle));

        // add mvn type for OSGi tests
        Vfs.addDefaultURLTypes(new MvnUrlType());

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
    private static class BundleClassLoaderImplWrapper extends MDSClassLoader {

        private Bundle bundle;

        public BundleClassLoaderImplWrapper(Bundle bundle) {
            this.bundle = bundle;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            // first check if we have this class
            Class<?> clazz = findLoadedClass(name);

            if (clazz == null) {
                // if not, load from bundle, classes with enhanced data are loaded directly from the bundle
                if (MotechClassPool.getEnhancedClassData(name) == null) {
                    clazz = bundle.loadClass(name);
                } else {
                    clazz = new BundleLoader(bundle).loadClass(name);
                }
            }

            return clazz;
        }
    }
}
