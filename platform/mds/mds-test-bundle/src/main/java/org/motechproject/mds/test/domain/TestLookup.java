package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.Objects;

@Entity(recordHistory = true)
public class TestLookup extends SuperClass {

    @Field
    private String someString;

    public TestLookup(String someString, String testString) {
        super(testString);
        this.someString = someString;
    }

    public String getSomeString() {
        return someString;
    }

    public void setSomeString(String someString) {
        this.someString = someString;
    }

    @Override
    public int hashCode() {
        return Objects.hash(someString, getSuperClassString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        TestLookup other = (TestLookup) obj;

        return Objects.equals(this.someString, other.someString) &&
               Objects.equals(this.getSuperClassString(), other.getSuperClassString());
    }

    @Override
    public String toString() {
        return String.format("TestLookup{someString = '%s', superClassString = '%s'}",
                someString, getSuperClassString());
    }
}
