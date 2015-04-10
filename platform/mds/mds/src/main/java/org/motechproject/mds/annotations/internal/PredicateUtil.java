package org.motechproject.mds.annotations.internal;

import org.apache.commons.collections.Predicate;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.annotations.RestIgnore;
import org.motechproject.mds.annotations.UIDisplayable;
import org.motechproject.mds.annotations.UIFilterable;
import org.motechproject.mds.annotations.NonEditable;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.util.MemberUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

public final class PredicateUtil {
    private static final Predicate ENTITY_FIELD = and(not(annotation(Ignore.class)), or(field(), accessorMethod()));
    private static final Predicate UI_DISPLAYABLE = and(annotation(UIDisplayable.class), ENTITY_FIELD);
    private static final Predicate UI_FILTERABLE = and(annotation(UIFilterable.class), ENTITY_FIELD);
    private static final Predicate REST_IGNORE = and(annotation(RestIgnore.class), ENTITY_FIELD);
    private static final Predicate NON_EDITABLE = and(annotation(NonEditable.class), ENTITY_FIELD);

    private PredicateUtil() {
    }

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

    public static Predicate not(final Predicate predicate) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return !predicate.evaluate(object);
            }
        };
    }

    public static Predicate entityField() {
        return ENTITY_FIELD;
    }

    public static Predicate uiDisplayable() {
        return UI_DISPLAYABLE;
    }

    public static Predicate uiFilterable() {
        return UI_FILTERABLE;
    }

    public static Predicate restIgnore() {
        return REST_IGNORE;
    }

    /**
     * Builds a predicate that evaluates to true, if the tested object is non-editable. For an object to be
     * non-editable, it must be an entity field and be annotated with the
     * {@link org.motechproject.mds.annotations.NonEditable} annotation.
     *
     * @return A predicate that evaluates to true for MDS entity fields that are non-editable.
     */
    public static Predicate nonEditable() {
        return NON_EDITABLE;
    }
}
