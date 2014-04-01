package org.motechproject.mds.osgi;

import java.io.Serializable;

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
}