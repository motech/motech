package org.motechproject.mds.reflections;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.motechproject.mds.annotations.internal.vfs.DoubleEncodedDirUrlType;
import org.motechproject.mds.annotations.internal.vfs.JndiUrlType;
import org.motechproject.mds.annotations.internal.vfs.MvnUrlType;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.MemberUtil;
import org.osgi.framework.Bundle;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
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
import java.util.Comparator;
import java.util.HashSet;
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

    static {
        // Add VFS types and make sure they get called at the beginning, so that we avoid
        // exceptions from the default handlers

        // add mvn type for OSGi tests
        Vfs.getDefaultUrlTypes().add(0, new MvnUrlType());
        // JNDI required for jars from the WAR
        Vfs.getDefaultUrlTypes().add(1, new JndiUrlType());
        // this is for a bug with spaces in Windows directory urls being encoded twice
        Vfs.getDefaultUrlTypes().add(2, new DoubleEncodedDirUrlType());
    }

    private ReflectionsUtil() {
    }

    /**
     * Finds all interfaces that extend the {@link MotechDataService} interface.
     *
     * @param bundle A bundle to look in.
     * @return a list of classes that extend the MotechDataService interface.
     */
    public static List<Class<? extends MotechDataService>> getMdsInterfaces(Bundle bundle) {
        LOGGER.debug("Looking for MDS interfaces in bundle: {}", bundle.getSymbolicName());

        Reflections reflections = configureReflection(bundle, new WrappedBundleClassLoader(bundle),
                new SubTypesScanner());
        Set<Class<? extends MotechDataService>> set = reflections.getSubTypesOf(MotechDataService.class);

        return new ArrayList<>(set);
    }

    /**
     * Looks for classes annotated with a given annotation.
     *
     * @param annotation an annotation to look for
     * @param bundle a bundle to look in.
     * @return A list of classes, annotated with the given annotation
     */
    public static Set<Class<?>> getClasses(Class<? extends Annotation> annotation, Bundle bundle) {
        LOGGER.debug("Scanning bundle: {}", bundle.getSymbolicName());
        LOGGER.debug("Searching for classes with annotations: {}", annotation.getName());

        Reflections reflections = configureReflection(bundle, new PristineBundleClassLoader(bundle),
                new TypeAnnotationsScanner(), new SubTypesScanner());

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(annotation);

        // in order to prevent processing of user defined or auto generated fields
        // we have to load the bytecode from the jar and define the class in a temporary
        // classLoader
        PristineBundleClassLoader pristineBundleClassLoader = new PristineBundleClassLoader(bundle);

        Set<Class<?>> result = new HashSet<>();
        for (Class clazz : classes) {
            try {
                result.add(pristineBundleClassLoader.loadClass(clazz.getName()));
            } catch (ClassNotFoundException e) {
                LOGGER.error("Could not find class", e);
            }
        }

        LOGGER.debug("Searched for classes with annotations: {}", annotation.getName());
        LOGGER.trace("Found {} classes with annotations: {}", result.size(), annotation.getName());

        return result;
    }

    /**
     * Looks for methods annotated with the given annotation.
     *
     * @param annotation an annotation to look for.
     * @param bundle a bundle to look in.
     * @return a list of moethods, annotated with the given annotation
     */
    public static Set<Method> getMethods(Class<? extends Annotation> annotation, Bundle bundle) {
        LOGGER.debug("Searching for methods with annotations: {}", annotation.getName());

        Reflections reflections = configureReflection(bundle, new WrappedBundleClassLoader(bundle),
                new MethodAnnotationsScanner());
        Set<Method> methods = reflections.getMethodsAnnotatedWith(annotation);

        LOGGER.debug("Searched for methods with annotations: {}", annotation.getName());
        LOGGER.trace("Found {} methods with annotations: {}", methods.size(), annotation.getName());

        return methods;
    }

    /**
     * Looks for class members, that match the given predicate.
     *
     * @param clazz a class to look for members in
     * @param memberPredicate predicated that must be fulfilled by the class members
     * @return a list of class members that match the predicate
     */
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

    /**
     * Retrieves a value of the specified property from the given annotation.
     *
     * @param annotation an annotation to look for the property in
     * @param property a property to retrieve value for
     * @param defaultValues default values to return, in case the property is not set
     * @return value of the property from the annotation, or default values
     */
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

    /**
     * Checks whether the annotated element is annotated with the given annotation.
     *
     * @param element annotated element to verify
     * @param annotation an annotation to look for
     * @return true if element is annotated with the given annotation; false otherwise
     */
    public static boolean hasAnnotation(AnnotatedElement element,
                                        Class<? extends Annotation> annotation) {
        return getAnnotation(element, annotation) != null;
    }

    /**
     * Gets the specified annotation from the annotated element or its accessor. If neither of them
     * has got this annotation, it returns null.
     *
     * @param object an object to get annotation from
     * @param annotation an annotation to look for
     * @param <T> an annotation
     * @return annotation from the class member or its accessor, or null, if not found
     */
    public static <T extends Annotation> T getAnnotationSelfOrAccessor(AnnotatedElement object, Class<T> annotation) {
        for (AccessibleObject accessibleObject : MemberUtil.getFieldAndAccessorsForElement((AccessibleObject) object)) {
            Member asMember = (Member) accessibleObject;
            T result = ReflectionsUtil.getAnnotationClassLoaderSafe(accessibleObject, asMember.getDeclaringClass(),
                    annotation);

            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Checks whether the specified object or its accessor is annotated with the given annotation.
     *
     * @param object an object to verify annotation presence on
     * @param annotation an annotation to look for
     * @return true, if given member or its accessor are annotated with the given annotation; false otherwise
     */
    public static boolean hasAnnotationSelfOrAccessor(AnnotatedElement object,
                                                      Class<? extends Annotation> annotation) {
        return getAnnotationSelfOrAccessor(object, annotation) != null;
    }

    /**
     * Checks whether the given annotation has got the specified property.
     *
     * @param annotation an annotation to look for the property in
     * @param property a property to look for
     * @return true if the specified property is present in the annotation; false otherwise
     */
    public static boolean hasProperty(Annotation annotation, String property) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        Method method;

        try {
            method = annotationType.getDeclaredMethod(property);
        } catch (NoSuchMethodException e) {
            method = null;
        }

        return method != null;
    }

    /**
     * Checks whether the given element has got given annotation, loaded using class loader of the specified class.
     *
     * @param ae annotated element to verify annotation presence on
     * @param clazz a class, which classloader will be used
     * @param annotation annotation class to verify
     * @param <T> annotation parameter
     * @return true if the annotated element has got given annotation; false otherwise
     */
    public static <T extends Annotation> boolean hasAnnotationClassLoaderSafe(AnnotatedElement ae,
                                                                          Class<?> clazz, Class<T> annotation) {
        Class<T> annotationClass = getAnnotationClass(clazz, annotation);
        return hasAnnotation(ae, annotationClass);
    }

    /**
     * Gets annotation from the given annotated element, using class loader of the specified class.
     *
     * @param ae annotated element to get annotation from
     * @param clazz a class, which classloader will be used
     * @param annotation annotation class to get
     * @param <T> annotation parameter
     * @return annotation from the given element, loaded using class loader of the given class
     */
    public static <T extends Annotation> T getAnnotationClassLoaderSafe(AnnotatedElement ae,
                                                                        Class<?> clazz, Class<T> annotation) {
        Class<T> annotationClass = getAnnotationClass(clazz, annotation);
        return getAnnotation(ae, annotationClass);
    }

    /**
     * Loads annotation class, using class loader of the specified class.
     *
     * @param clazz a class, which classloader will be used
     * @param annotation an annotation to load
     * @param <T> annotation parameter
     * @return annotation class, loaded using class loader of the given class
     */
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

    private static Reflections configureReflection(Bundle bundle, ClassLoader classLoader, Scanner... scanners) {
        ConfigurationBuilder configuration = new ConfigurationBuilder();
        configuration.addUrls(resolveLocation(bundle));
        configuration.setScanners(scanners);

        // we add the ability to load classes from the bundle
        configuration.addClassLoader(classLoader);

        LOGGER.debug("Initialized Reflections configuration");

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
}
