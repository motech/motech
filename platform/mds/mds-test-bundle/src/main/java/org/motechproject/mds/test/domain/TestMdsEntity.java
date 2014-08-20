package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsEntity;

import java.util.Objects;

@Entity
public class TestMdsEntity extends MdsEntity {

    @Field
    private String someString;

    /*
        Do NOT insert default constructor in this class, since MdsDdeBundleIT tests
        if MDS will insert default one
     */

    public TestMdsEntity(String someString) {
        this.someString = someString;
    }

    public String getSomeString() {
        return someString;
    }

    public void setSomeString(String someString) {
        this.someString = someString;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        TestMdsEntity other = (TestMdsEntity) obj;

        return Objects.equals(this.someString, other.someString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(someString);
    }

    @Override
    public String toString() {
        return String.format("TestMdsEntity{someString= '%s'}",someString);
    }
}
