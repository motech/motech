package org.motechproject.mds.annotations.internal.samples;

import org.motechproject.mds.annotations.EntityExtension;
import org.motechproject.mds.annotations.Field;

@EntityExtension
public class FieldOrverridingSample extends Sample {

    @Field
    private int primitiveInt;

    public int getPrimitiveInt() {
        return primitiveInt;
    }

    public void setPrimitiveInt(int primitiveInt) {
        this.primitiveInt = primitiveInt;
    }
}
