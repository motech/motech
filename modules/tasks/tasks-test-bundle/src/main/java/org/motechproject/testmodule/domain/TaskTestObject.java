package org.motechproject.testmodule.domain;


import java.util.Objects;

public class TaskTestObject {
    private String testName;
    private String testNameWithPrefix;

    public TaskTestObject(String name) {
        this(name, null);
    }

    public TaskTestObject(String testName, String testNameWithPrefix) {
        this.testName = testName;
        this.testNameWithPrefix = testNameWithPrefix;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestNameWithPrefix(String testNameWithPrefix) {
        this.testNameWithPrefix = testNameWithPrefix;
    }

    public String getTestNameWithPrefix() {
        return testNameWithPrefix;
    }

    @Override
    public int hashCode() {
        return Objects.hash(testName, testNameWithPrefix);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        TaskTestObject other = (TaskTestObject) obj;

        return Objects.equals(this.testName, other.testName) &&
                Objects.equals(this.testNameWithPrefix, other.testNameWithPrefix);
    }
}
