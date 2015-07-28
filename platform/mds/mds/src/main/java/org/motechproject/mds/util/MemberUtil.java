package org.motechproject.mds.util;

import javassist.CtClass;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.capitalize;
import static org.springframework.util.ReflectionUtils.FieldCallback;
import static org.springframework.util.ReflectionUtils.FieldFilter;
import static org.springframework.util.ReflectionUtils.MethodCallback;
import static org.springframework.util.ReflectionUtils.MethodFilter;

/**
 * Util class that provides convenient methods connected with the
 * class members.
 */
public final class MemberUtil {

    public static final String GETTER_PREFIX = "get";
    public static final String BOOLEAN_GETTER_PREFIX = "is";
    public static final String SETTER_PREFIX = "set";

    public static final int GET_OR_SET_END_INDEX = 3;
    public static final int IS_END_INDEX = 2;

    private MemberUtil() {
    }

    /**
     * Gets all members of a class, that match the specified predicate.
     *
     * @param clazz class to retrieve members from
     * @param memberPredicate predicate that must be fulfilled by class members
     * @return list of class members
     */
    public static List<Member> getMembers(Class<?> clazz, Predicate memberPredicate) {
        List<Member> list = new ArrayList<>();

        MemberCallback memberCallback = new MemberCallback(list);
        MemberFilter memberFilter = new MemberFilter(memberPredicate);

        ReflectionUtils.doWithFields(clazz, memberCallback, memberFilter);
        ReflectionUtils.doWithMethods(clazz, memberCallback, memberFilter);

        return list;
    }

    /**
     * Gets field name, from the specified annotated element. It will return null, if annotated element is
     * not a class member. Otherwise, it will try to resolve the field name, by either reading it directly from the
     * member, or by determining the name, based on the getter/setter method. It will return null if member is neither
     * a field or getter/setter.
     *
     * @param object annotated element to retrieve field name from
     * @return field name, if possible; null otherwise
     */
    public static String getFieldName(AnnotatedElement object) {
        return object instanceof Member
                ? getFieldName((Member) object)
                : null;
    }

    /**
     * Gets field name, from the specified member. It will try to resolve the field name, by either reading it directly
     * from the member, or by determining the name, based on the getter/setter method. It will return null if member is neither
     * a field or getter/setter.
     *
     * @param object class member to retrieve field name from
     * @return field name, if possible; null otherwise
     */
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

    /**
     * Builds an enum class name for given entity class name and field name.
     *
     * @param entityClassName the class name of the entity
     * @param fieldName the field name
     * @return enum class name
     */
    public static String getDefaultEnumName(String entityClassName, String fieldName) {
        return String.format("%s.%s%s", ClassName.getEnumPackage(entityClassName),
                ClassName.getSimpleName(entityClassName), capitalize(fieldName));
    }

    /**
     * Gets annotated element type. If this element is not a member of the class, it returns null. Otherwise, it
     * will try to resolve the type by checking it directly, or via getter/setter methods. If member is neither
     * a field or getter/setter method, it returns null.
     *
     * @param object annotated element to retrieve type from
     * @return type of the element, or null if not applicable
     */
    public static Class<?> getCorrectType(AnnotatedElement object) {
        return object instanceof Member
                ? getCorrectType((Member) object)
                : null;
    }

    /**
     * Gets member type. It will try to resolve the type by checking it directly, or via getter/setter methods.
     * If member is neither a field or getter/setter method, it returns null.
     *
     * @param object annotated element to retrieve type from
     * @return type of the element, or null if not applicable
     */
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

    /**
     * Retrieves an actual type from the parameterized class member. If annotated element is not a class member,
     * it returns null. It always checks for the first parameter. If you want to specify which parameter to
     * retrieve, use {@link #getGenericType(java.lang.reflect.AnnotatedElement, int)}. This will work on
     * fields and getter/setter methods. It will return null for other class members or if there is no parameterized
     * type on them.
     *
     * @param object annotated element to retrieve actual type from
     * @return Actual type of the parameterized class member
     */
    public static Class<?> getGenericType(AnnotatedElement object) {
        return getGenericType(object, 0);
    }

    /**
     * Retrieves an actual type from the parameterized class member. If annotated element is not a class member,
     * it returns null. It will check the parameter on position {@code typeNumber}. This will work on
     * fields and getter/setter methods. It will return null for other class members or if there is no parameterized
     * type on them.
     *
     * @param object annotated element to retrieve actual type from
     * @param typeNumber position of the parameterized type
     * @return Actual type of the parameterized class member
     */
    public static Class<?> getGenericType(AnnotatedElement object, int typeNumber) {
        return object instanceof Member
                ? getGenericType((Member) object, typeNumber)
                : null;
    }

    /**
     * Retrieves an actual type from the parameterized class member. It will check the parameter on position {@code typeNumber}
     * This will work on fields and getter/setter methods. It will return null for other class members or if there is no
     * parameterized type on them.
     *
     * @param object class member to retrieve actual type from
     * @param typeNumber position of the parameterized type
     * @return Actual type of the parameterized class member
     */
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

    /**
     * Returns getter method name for the given field name and the class declaration.
     *
     * @param fieldName field name
     * @param declaring the class declaration that contains the field
     * @return getter name
     */
     public static String getGetterName(String fieldName, CtClass declaring) {
        String capitalized = capitalize(fieldName);

        String standardGetter = GETTER_PREFIX + capitalized;
        String booleanGetter = BOOLEAN_GETTER_PREFIX + capitalized;
        // we have to check what kind of getter is defined in the given class definition
        // and create the new one with the same name
        boolean containsBooleanGetter = JavassistUtil.containsDeclaredMethod(declaring, booleanGetter) ||
            JavassistUtil.containsMethod(declaring, booleanGetter);
        return containsBooleanGetter ? booleanGetter : standardGetter;
    }

    /**
     * Returns setter method name for the given field name.
     *
     * @param fieldName field name
     * @return setter name
     */
    public static String getSetterName(String fieldName) {
        return SETTER_PREFIX + capitalize(fieldName);
    }

    /**
     * Checks if given class member is a getter method. This includes boolean-specific getters, starting with
     * "is" prefix.
     *
     * @param member class member to verify
     * @return true if given class member is a getter method; false otherwise
     */
    public static boolean isGetter(Member member) {
        if (member instanceof Method && !Modifier.isStatic(member.getModifiers())) {
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

    /**
     * Checks if given class member is a setter method.
     *
     * @param member class member to verify
     * @return true if given class member is a setter method; false otherwise
     */
    public static boolean isSetter(Member member) {
        if (member instanceof Method && !Modifier.isStatic(member.getModifiers())) {
            Method method = (Method) member;
            return method.getName().startsWith(SETTER_PREFIX) && method.getReturnType().equals(Void.TYPE)
                    && ArrayUtils.getLength(method.getParameterTypes()) == 1;
        } else {
            return false;
        }
    }

    /**
     * Attempts to retrieve field name from the getter/setter method name. It will throw
     * {@link java.lang.IllegalArgumentException} if provided value is empty or does not match the setter/getter naming
     * convention.
     *
     * @param getterSetterName getter/setter method name
     * @return field name
     */
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

    /**
     * Retrieves a declaring class for the given object. Returns null if the given object
     * is not a member of a class.
     *
     * @param ac object to verify
     * @return A class, to which this object belongs
     */
    public static Class<?> getDeclaringClass(AccessibleObject ac) {
        return (ac instanceof Member) ? ((Member) ac).getDeclaringClass() : null;
    }

    /**
     * Returns a list of objects, that are either field or getter/setter methods of this field, based
     * on single accessible object of a class. If it fails to find anything, it returns the passed
     * object.
     *
     * @param ao an object to find field, getter/setter method for
     * @return a list of field and getter/setter methods or {@code ao} if nothing has been found
     */
    public static List<AccessibleObject> getFieldAndAccessorsForElement(AccessibleObject ao) {
        String fieldName = getFieldName(ao);

        if (fieldName == null) {
            return Arrays.asList(ao);
        }

        Class declaringClass = ((Member) ao).getDeclaringClass();

        try {
            PropertyDescriptor descriptor = new PropertyDescriptor(fieldName, declaringClass);

            Field field = ReflectionUtils.findField(declaringClass, fieldName);
            Method getter = descriptor.getReadMethod();
            Method setter = descriptor.getWriteMethod();

            List<AccessibleObject> result = new ArrayList<>();

            if (field != null) {
                result.add(field);
            }
            if (getter != null) {
                result.add(getter);
            }
            if (setter != null) {
                result.add(setter);
            }

            if (result.isEmpty()) {
                result.add(ao);
            }

            return result;
        } catch (IntrospectionException e) {
            return Arrays.asList(ao);
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
        private Predicate memberPredicate;

        private MemberFilter(Predicate memberPredicate) {
            this.memberPredicate = memberPredicate == null ? PredicateUtils.truePredicate() : memberPredicate;
        }

        @Override
        public boolean matches(Method method) {
            return memberPredicate.evaluate(method);
        }

        @Override
        public boolean matches(Field field) {
            return memberPredicate.evaluate(field);
        }

    }
}
