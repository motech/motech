package org.motechproject.mds.annotations.internal.samples;

import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.EntityExtension;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;

@Entity(recordHistory = false)
@EntityExtension
@CrudEvents(CrudEventType.NONE)
public class ExtendedSample extends Sample {

    @Field
    private String extendedStringTest;

    public void setExtendedStringTest(String extendedStringTest) {
        this.extendedStringTest = extendedStringTest;
    }

    public String getExtendedStringTest(){
        return extendedStringTest;
    }
}
