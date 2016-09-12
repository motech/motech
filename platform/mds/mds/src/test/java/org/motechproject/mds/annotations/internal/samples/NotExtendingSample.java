package org.motechproject.mds.annotations.internal.samples;

import org.motechproject.mds.annotations.EntityExtension;
import org.motechproject.mds.annotations.Field;

@EntityExtension
public class NotExtendingSample {

    @Field
    private String testField;

    public String getTestField(){
        return testField;
    }

    public void setTestField(String testField){
        this.testField = testField;
    }
}
