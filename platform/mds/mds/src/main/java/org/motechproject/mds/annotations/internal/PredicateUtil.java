package org.motechproject.mds.annotations.internal;

import org.apache.commons.collections.Predicate;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.annotations.RestIgnore;
import org.motechproject.mds.annotations.UIDisplayable;
import org.motechproject.mds.annotations.UIFilterable;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.util.MemberUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

/**
 * This is a util class for {@link org.apache.commons.collections.Predicate} for MDS annotations.
 * It contains several, ready to use predicates, that can be used for filtering collections. It also
 * contains helper methods for basic logic operations on predicates.
 */
public final class PredicateUtil {
    private static final Predicate ENTITY_FIELD = and(not(annotation(Ignore.class)), or(field(), accessorMethod()));
    private static final Predicate UI_DISPLAYABLE = and(annotation(UIDisplayable.class), ENTITY_FIELD);
    private static final Predicate UI_FILTERABLE = and(annotation(UIFilterable.class), ENTITY_FIELD);
    private static final Predicate REST_IGNORE = and(annotation(RestIgnore.class), ENTITY_FIELD);

    private PredicateUtil() {
    }

    /**
     * This constructs predicate, that evaluates if tested object has got a specific annotation. It will
     * additionally perform checks on getters and setters, if invoked on a field.
     *
     * @param annotation an annotation to test against.
     * @return Predicate evaluating the presence of an annotation on field or its accessor.
     */
    public static Predicate annotation(final Class<? extends Annotation> annotation) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                boolean match = false;
                if ((object instanceof java.lang.reflect.Field) || (object instanceof Method &&
                        (MemberUtil.isGetter((Member) object) || MemberUtil.isSetter((Member) object)))) {
                    match = ReflectionsUtil.hasAnnotationSelfOrAccessor((AnnotatedElement) object, annotation);
                } else if (object instanceof AnnotatedElement) {
                    match = ReflectionsUtil.hasAnnotation((AnnotatedElement) object, annotation);
                }
                return match;
            }
        };
    }

    /**
     * This constructs predicate, that evaluates whether the given object can be an MDS field. It will return false if
     * not invoked on a field. If invoked on a field, it will return true if the field is annotated with
     * the {@link org.motechproject.mds.annotations.Field} annotation or if the field is public. It will return false
     * for any static fields.
     *
     * @return Predicate evaluating whether the passed field can be an MDS field.
     */
    public static Predicate field() {
        return new Predicate() {

            @Override
            public boolean evaluate(Object object) {
                boolean match = false;
                if (object instanceof java.lang.reflect.Field) {
                    java.lang.reflect.Field field = (java.lang.reflect.Field) object;
                    boolean hasFieldAnnotation = ReflectionsUtil.hasAnnotationClassLoaderSafe(
                            field, field.getDeclaringClass(), Field.class);
                    boolean isPublic = isPublic(field.getModifiers());
                    boolean isStatic = isStatic(field.getModifiers());

                    match = (hasFieldAnnotation || isPublic) && !isStatic;
                }
                return match;
            }
        };
    }

    /**
     * This constructs predicate, that evaluates whether the tested object is either a getter or a setter.
     * If the tested object is not a method, it will return false at once. For boolean getters, the "is" prefix
     * will be taken into consideration as well.
     *
     * @return Predicate evaluating whether the given method is either a getter or a setter.
     */
    public static Predicate accessorMethod() {
        return new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                boolean match = false;
                if (object instanceof Method) {
                    Method method = (Method) object;
                    boolean isNotFromObject = method.getDeclaringClass() != Object.class;
                    boolean isGetter = MemberUtil.isGetter(method);
                    boolean isSetter = MemberUtil.isSetter(method);

                    match = (isNotFromObject && (isGetter || isSetter));
                }
                return match;
            }
        };
    }

    /**
     * This constructs a predicate, that performs logical AND operation on a given set of predicates.
     * The evaluation returns true if all predicates evaluate to true. Otherwise, it returns false.
     *
     * @param predicates predicates to perform an operation on.
     * @return A predicate that evaluates to true, only if all passed predicates evaluate to true.
     */
    public static Predicate and(final Predicate... predicates) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                for (Predicate predicate : predicates) {
                    if (!predicate.evaluate(object)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    /**
     * This constructs a predicate, that performs logical OR operation on a given set of predicates.
     * The evaluation returns false if all predicates evaluate to false. If at least one predicate
     * evaluates to true, it will return true.
     *
     * @param predicates predicates to perform an operation on.
     * @return A predicate that evaluates to true, if at least one passed predicate evaluates to true.
     */
    public static Predicate or(final Predicate... predicates) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                for (Predicate predicate : predicates) {
                    if (predicate.evaluate(object)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * This constructs a predicate, that performs logical NOT operation on a given predicate. The positive evaluation
     * turns to negative evaluation and negative evaluation turns to positive evaluation.
     *
     * @param predicate a predicate to perform logical NOT on.
     * @return A predicate, that evaluates to the opposite value than the passed predicate.
     */
    public static Predicate not(final Predicate predicate) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return !predicate.evaluate(object);
            }
        };
    }

    /**
     * Builds a predicate that evaluates to true, if the tested object is a MDS field. For an object to be an MDS
     * entity, it must either be a field annotated with the {@link org.motechproject.mds.annotations.Field} annotation
     * or have either a public getter or setter. Additionally, it must not be annotated with the
     * {@link org.motechproject.mds.annotations.Ignore} annotation.
     *
     * @return A predicate that evaluates to true for MDS entity fields.
     */
    public static Predicate entityField() {
        return ENTITY_FIELD;
    }

    /**
     * Builds a predicate that evaluates to true, if the tested object is UI Displayable. For an object to be
     * UI Displayable, it must be an entity field and be annotated with the
     * {@link org.motechproject.mds.annotations.UIDisplayable} annotation.
     *
     * @return A predicate that evaluates to true for MDS entity fields that are UI displayable.
     */
    public static Predicate uiDisplayable() {
        return UI_DISPLAYABLE;
    }

    /**
     * Builds a predicate that evaluates to true, if the tested object is UI Filterable. For an object to be
     * UI Filterable, it must be an entity field and be annotated with the
     * {@link org.motechproject.mds.annotations.UIFilterable} annotation.
     *
     * @return A predicate that evaluates to true for MDS entity fields that are UI filterable.
     */
    public static Predicate uiFilterable() {
        return UI_FILTERABLE;
    }

    /**
     * Builds a predicate that evaluates to true, if the tested object is not included in the REST operations. An object
     * is not exposed via REST operations, if it is an entity field and is annotated with the
     * {@link org.motechproject.mds.annotations.RestIgnore} annotation.
     *
     * @return A predicate that evaluates to true for MDS entity fields that is not exposed via REST.
     */
    public static Predicate restIgnore() {
        return REST_IGNORE;
    }
}
