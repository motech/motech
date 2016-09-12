package org.motechproject.mds.annotations.internal.samples;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.EntityExtension;
import org.motechproject.mds.annotations.Field;

@Entity(recordHistory = true)
@EntityExtension
public class AnotherExtendedSample extends AnotherSample {

    @Field
    private String testField;

    public AnotherExtendedSample(int anotherInt, String testField) {
        super(anotherInt);
        this.testField = testField;
    }

    public String getTestField(){
        return testField;
    }

    public void setTestField(String testField){
        this.testField = testField;
    }
}
