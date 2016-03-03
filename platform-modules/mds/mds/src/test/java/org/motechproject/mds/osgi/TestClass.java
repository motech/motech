package org.motechproject.mds.osgi;

import org.apache.commons.beanutils.PropertyUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class TestClass implements Serializable {
    private static final long serialVersionUID = -2023939468477873901L;
    private int someInt;
    private String someString;

    public TestClass(int someInt, String someString) {
        this.someInt = someInt;
        this.someString = someString;
    }

    public int getSomeInt() {
        return someInt;
    }

    public void setSomeInt(int someInt) {
        this.someInt = someInt;
    }

    public String getSomeString() {
        return someString;
    }

    public void setSomeString(String someString) {
        this.someString = someString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        try {
            return Objects.equals(someInt, PropertyUtils.getProperty(o, "someInt")) &&
                   Objects.equals(someString, PropertyUtils.getProperty(o, "someString"));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(someInt, someString);
    }
}