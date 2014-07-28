package org.motechproject.mds.util;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.Introspector;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ReflectionUtils.FieldCallback;
import static org.springframework.util.ReflectionUtils.FieldFilter;
import static org.springframework.util.ReflectionUtils.MethodCallback;
import static org.springframework.util.ReflectionUtils.MethodFilter;

public final class MemberUtil {

    public static final String GETTER_PREFIX = "get";
    public static final String BOOLEAN_GETTER_PREFIX = "is";
    public static final String SETTER_PREFIX = "set";

    public static final int GET_OR_SET_END_INDEX = 3;
    public static final int IS_END_INDEX = 2;

    private MemberUtil() {
    }

    public static List<Member> getMembers(Class<?> clazz, Predicate methodPredicate,
                                          Predicate fieldPredicate) {
        List<Member> list = new ArrayList<>();

        MemberCallback memberCallback = new MemberCallback(list);
        MemberFilter memberFilter = new MemberFilter(methodPredicate, fieldPredicate);

        ReflectionUtils.doWithFields(clazz, memberCallback, memberFilter);
        ReflectionUtils.doWithMethods(clazz, memberCallback, memberFilter);

        return list;
    }

    public static String getFieldName(AnnotatedElement object) {
        return object instanceof Member
                ? getFieldName((Member) object)
                : null;
    }

    public static String getFieldName(Member object) {
        String name = null;

        if (object instanceof Method) {
            Method method = (Method) object;

            if (isGetter(method) || isSetter(method)) {
                name = getFieldNameFromGetterSetterName(method.getName());
            }
        } else if (object instanceof Field) {
            Field field = (Field) object;

            name = field.getName();
        }

        return name;
    }

    public static Class<?> getCorrectType(AnnotatedElement object) {
        return object instanceof Member
                ? getCorrectType((Member) object)
                : null;
    }

    public static Class<?> getCorrectType(Member object) {
        Class<?> classType = null;

        if (object instanceof Method) {
            Method method = (Method) object;

            if (isGetter(method)) {
                classType = method.getReturnType();
            } else if (isSetter(method)) {
                Class<?>[] parameterTypes = method.getParameterTypes();

                if (ArrayUtils.isNotEmpty(parameterTypes)) {
                    classType = parameterTypes[0];
                }
            }
        } else if (object instanceof Field) {
            Field field = (Field) object;

            classType = field.getType();
        }

        return classType;
    }

    public static Class<?> getGenericType(AnnotatedElement object) {
        return getGenericType(object, 0);
    }

    public static Class<?> getGenericType(AnnotatedElement object, int typeNumber) {
        return object instanceof Member
                ? getGenericType((Member) object, typeNumber)
                : null;
    }

    public static Class<?> getGenericType(Member object, int typeNumber) {
        Type generic = null;

        if (object instanceof Method) {
            Method method = (Method) object;

            if (isGetter(method)) {
                generic = method.getGenericReturnType();
            } else if (isSetter(method)) {
                Type[] genericParameterTypes = method.getGenericParameterTypes();

                if (ArrayUtils.isNotEmpty(genericParameterTypes)) {
                    generic = genericParameterTypes[0];
                }
            }
        } else if (object instanceof Field) {
            Field field = (Field) object;
            generic = field.getGenericType();
        }

        if (generic instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) generic;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            if (ArrayUtils.isNotEmpty(actualTypeArguments)) {
                generic = actualTypeArguments[typeNumber];
            }
        }

        return (Class<?>) generic;
    }

    public static boolean isGetter(Member member) {
        if (member instanceof Method) {
            Method method = (Method) member;

            // check regular getter
            boolean isGetter = !method.getReturnType().equals(Void.TYPE) &&
                                 method.getName().startsWith(GETTER_PREFIX) &&
                                 ArrayUtils.isEmpty(method.getParameterTypes());
            if (!isGetter) {
                // check for boolean getter
                isGetter = (Boolean.class.equals(method.getReturnType()) || boolean.class.equals(method.getReturnType()))
                        && method.getName().startsWith(BOOLEAN_GETTER_PREFIX)
                        && ArrayUtils.isEmpty(method.getParameterTypes());
            }

            return isGetter;
        } else {
            return false;
        }
    }

    public static boolean isSetter(Member member) {
        if (member instanceof Method) {
            Method method = (Method) member;
            return method.getName().startsWith(SETTER_PREFIX) && method.getReturnType().equals(Void.TYPE)
                    && ArrayUtils.getLength(method.getParameterTypes()) == 1;
        } else {
            return false;
        }
    }

    public static String getFieldNameFromGetterSetterName(String getterSetterName) {
        if (StringUtils.isBlank(getterSetterName)) {
            throw new IllegalArgumentException("Provided getter or setter name cannot be null or empty");
        } else if (getterSetterName.startsWith(SETTER_PREFIX) || getterSetterName.startsWith(GETTER_PREFIX)) {
            return Introspector.decapitalize(getterSetterName.substring(GET_OR_SET_END_INDEX));
        } else if (getterSetterName.startsWith(BOOLEAN_GETTER_PREFIX)) {
            return Introspector.decapitalize(getterSetterName.substring(IS_END_INDEX));
        } else {
            throw new IllegalArgumentException(getterSetterName + " does not start with get/set/is");
        }
    }

    private static final class MemberCallback implements MethodCallback, FieldCallback {
        private List<Member> elements;

        protected MemberCallback(List<Member> elements) {
            this.elements = elements;
        }

        @Override
        public void doWith(Method method) {
            elements.add(method);
        }

        @Override
        public void doWith(Field field) {
            elements.add(field);
        }

    }

    private static final class MemberFilter implements MethodFilter, FieldFilter {
        private Predicate methodPredicate;
        private Predicate fieldPredicate;

        private MemberFilter(Predicate methodPredicate, Predicate fieldPredicate) {
            this.methodPredicate = methodPredicate == null ? new TruePredicate() : methodPredicate;
            this.fieldPredicate = fieldPredicate == null ? new TruePredicate() : fieldPredicate;
        }

        @Override
        public boolean matches(Method method) {
            return methodPredicate.evaluate(method);
        }

        @Override
        public boolean matches(Field field) {
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
