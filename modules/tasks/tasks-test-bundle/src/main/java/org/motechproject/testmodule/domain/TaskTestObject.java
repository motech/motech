package org.motechproject.testmodule.domain;


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
}