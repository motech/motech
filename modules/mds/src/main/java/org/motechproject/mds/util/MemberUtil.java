package org.motechproject.mds.util;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.Introspector;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;
import static org.springframework.util.ReflectionUtils.FieldCallback;
import static org.springframework.util.ReflectionUtils.FieldFilter;
import static org.springframework.util.ReflectionUtils.MethodCallback;
import static org.springframework.util.ReflectionUtils.MethodFilter;

public final class MemberUtil {
    public static final String GETTER_PREFIX = "get";
    public static final String SETTER_PREFIX = "set";
    public static final Integer FIELD_NAME_START_IDX = 3;

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

            if (startsWithIgnoreCase(method.getName(), GETTER_PREFIX)
                    || startsWithIgnoreCase(method.getName(), SETTER_PREFIX)) {
                name = method.getName().substring(FIELD_NAME_START_IDX);
                name = Introspector.decapitalize(name);
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

            if (startsWithIgnoreCase(method.getName(), GETTER_PREFIX)) {
                classType = method.getReturnType();
            } else if (startsWithIgnoreCase(method.getName(), SETTER_PREFIX)) {
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
