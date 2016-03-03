package org.motechproject.mds.testutil;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Unique;

/**
 * Class used in the entity builder test
 */
@Entity(recordHistory = true)
@PersistenceCapable
public class EntBuilderTestClass {

    @Field
    @Unique
    private String testStr = "defValForTestStr";
    @Field
    private boolean testBool;

    public String getTestStr() {
        return testStr;
    }

    public void setTestStr(String testStr) {
        this.testStr = testStr;
    }

    public boolean isTestBool() {
        return testBool;
    }

    public void setTestBool(boolean testBool) {
        this.testBool = testBool;
    }
}
