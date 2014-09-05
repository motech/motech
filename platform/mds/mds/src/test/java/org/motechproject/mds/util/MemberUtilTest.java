package org.motechproject.mds.util;

import org.junit.Test;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MemberUtilTest {

    @Test
    public void shouldRecognizeGetters() throws NoSuchMethodException {
        for (Method method : legitimateGetters()) {
            assertTrue("Method " + method.getName() + " was not recognized as a getter",
                        MemberUtil.isGetter(method));
        }

        for (Member[] memberColl : Arrays.asList(legitimateSetters(), fakeGetters(), fakeSetters(), fields())) {
            for (Member member : memberColl) {
                assertFalse("Member " + member.getName() + " was incorrectly recognized as a getter",
                        MemberUtil.isGetter(member));
            }
        }
    }

    @Test
    public void shouldRecognizeSetters() throws NoSuchMethodException {
        for (Method method : legitimateSetters()) {
            assertTrue("Method " + method.getName() + " was not recognized as a setter",
                        MemberUtil.isSetter(method));
        }

        for (Member[] memberColl : Arrays.asList(legitimateGetters(), fakeGetters(), fakeSetters(), fields())) {
            for (Member member : memberColl) {
                assertFalse("Member " + member.getName() + " was incorrectly recognized as a setter",
                        MemberUtil.isSetter(member));
            }
        }
    }

    @Test
    public void shouldRecognizeFieldNamesFromGettersSetters() {
        assertEquals("boolField", MemberUtil.getFieldNameFromGetterSetterName("isBoolField"));
        assertEquals("something", MemberUtil.getFieldNameFromGetterSetterName("getSomething"));
        assertEquals("dateOfBirth", MemberUtil.getFieldNameFromGetterSetterName("getDateOfBirth"));
        assertEquals("oneTwoThree", MemberUtil.getFieldNameFromGetterSetterName("isOneTwoThree"));
    }

    @Test
    public void shouldGetFieldNamesAndTypesFromGettersSetters() throws NoSuchMethodException {
        String[] names = {"boolField", "bigBoolField", "bigIntField", "strField", "boolField2", "bigBoolField2"};
        Class[] types = {boolean.class, Boolean.class, Integer.class, String.class, boolean.class, Boolean.class};
        Method[] getters = legitimateGetters();

        for (int i = 0; i < names.length; i++) {
            String expectedName = names[i];
            Class expectedType = types[i];
            Member element = getters[i];

            assertEquals(expectedName + " was not correctly recognized from getter " + element.getName(),
                    expectedName, MemberUtil.getFieldName(element));
            assertEquals(expectedType + " type was not correctly recognized from getter " + element.getName(),
                    expectedType, MemberUtil.getCorrectType(element));
        }

        names = new String[] {"boolField", "intField", "bigBoolField", "bigBoolField2"};
        types = new Class[] {boolean.class, int.class, Boolean.class, Boolean.class};
        Method[] setters = legitimateSetters();

        for (int i = 0; i < names.length; i++) {
            String expectedName = names[i];
            Class expectedType = types[i];
            Member element = setters[i];

            assertEquals(expectedName + " was not correctly recognized from setter " + element.getName(),
                    expectedName, MemberUtil.getFieldName(element));
            assertEquals(expectedType + " type was not correctly recognized from setter " + element.getName(),
                    expectedType, MemberUtil.getCorrectType(element));
        }
    }

    @Test
    public void shouldRecognizeCorrectTypesFromFields() throws NoSuchFieldException {
        assertEquals(boolean.class, MemberUtil.getCorrectType(getDeclaredField("boolField")));
        assertEquals(boolean.class, MemberUtil.getCorrectType(getDeclaredField("boolField2")));
        assertEquals(Boolean.class, MemberUtil.getCorrectType(getDeclaredField("bigBoolField")));
        assertEquals(Boolean.class, MemberUtil.getCorrectType(getDeclaredField("bigBoolField2")));
        assertEquals(int.class, MemberUtil.getCorrectType(getDeclaredField("intField")));
        assertEquals(Integer.class, MemberUtil.getCorrectType(getDeclaredField("bigIntField")));
        assertEquals(String.class, MemberUtil.getCorrectType(getDeclaredField("strField")));
    }

    @Test
    public void shouldRetrieveSettersAndField() throws NoSuchMethodException, NoSuchFieldException {
        AccessibleObject field = TestClass.class.getDeclaredField("boolField");
        AccessibleObject getter = TestClass.class.getMethod("isBoolField");
        AccessibleObject setter = TestClass.class.getMethod("setBoolField", boolean.class);
        List<AccessibleObject> expected = Arrays.asList(field, getter, setter);

        assertEquals(expected, MemberUtil.getFieldAndAccessorsForElement(field));
        assertEquals(expected, MemberUtil.getFieldAndAccessorsForElement(getter));
        assertEquals(expected, MemberUtil.getFieldAndAccessorsForElement(setter));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForIncorrectGetterSetterNames() {
        MemberUtil.getFieldNameFromGetterSetterName("nothingGood");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForNullGetterSetterNames() {
        MemberUtil.getFieldNameFromGetterSetterName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForBlankGetterSetterNames() {
        MemberUtil.getFieldNameFromGetterSetterName("");
    }

    private AnnotatedElement getDeclaredField(String name) throws NoSuchFieldException {
        return TestClass.class.getDeclaredField(name);
    }

    private Method[] legitimateGetters() throws NoSuchMethodException {
        return new Method[] {
                TestClass.class.getMethod("isBoolField"),
                TestClass.class.getMethod("getBigBoolField"),
                TestClass.class.getMethod("getBigIntField"),
                TestClass.class.getMethod("getStrField"),
                TestClass.class.getMethod("getBoolField2"),
                TestClass.class.getMethod("isBigBoolField2")
        };
    }

    private Method[] legitimateSetters() throws NoSuchMethodException {
        return new Method[] {
                TestClass.class.getMethod("setBoolField", boolean.class),
                TestClass.class.getMethod("setIntField", int.class),
                TestClass.class.getMethod("setBigBoolField", Boolean.class),
                TestClass.class.getMethod("setBigBoolField2", Boolean.class)
        };
    }

    private Method[] fakeGetters() throws NoSuchMethodException {
        return new Method[] {
            TestClass.class.getMethod("getIntField"),
            TestClass.class.getMethod("something"),
            TestClass.class.getMethod("getIntField", int.class)
        };
    }

    private Method[] fakeSetters() throws NoSuchMethodException {
        return new Method[] {
                TestClass.class.getMethod("setStrField", String.class),
                TestClass.class.getMethod("setBigIntField"),
        };
    }

    private Field[] fields() {
        return TestClass.class.getDeclaredFields();
    }

    private class TestClass {

        private boolean boolField;
        private boolean boolField2;
        private int intField;
        private Boolean bigBoolField;
        private Boolean bigBoolField2;
        private Integer bigIntField;
        private String strField;

        public boolean isBoolField() {
            return boolField;
        }

        public void setBoolField(boolean boolField) {
            this.boolField = boolField;
        }

        public void setIntField(int intField) {
            this.intField = intField;
        }

        public Boolean getBigBoolField() {
            return bigBoolField;
        }

        public void setBigBoolField(Boolean bigBoolField) {
            this.bigBoolField = bigBoolField;
        }

        public Integer getBigIntField() {
            return bigIntField;
        }

        public String getStrField() {
            return strField;
        }

        public boolean getBoolField2() {
            return boolField2;
        }

        public void setBoolField2(boolean boolField2) {
            this.boolField2 = boolField2;
        }

        public Boolean isBigBoolField2() {
            return bigBoolField2;
        }

        public void setBigBoolField2(Boolean bigBoolField2) {
            this.bigBoolField2 = bigBoolField2;
        }

        // fake setters/getters

        public String setStrField(String strField) {
            this.strField = strField;
            return strField;
        }

        public void setBigIntField() {
        }

        public void getIntField() {
        }

        public int getIntField(int param) {
            return param;
        }

        public Boolean something() {
            return true;
        }
    }
}
