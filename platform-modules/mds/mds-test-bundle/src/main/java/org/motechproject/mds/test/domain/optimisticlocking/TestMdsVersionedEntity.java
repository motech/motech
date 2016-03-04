package org.motechproject.mds.test.domain.optimisticlocking;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsVersionedEntity;

@Entity
public class TestMdsVersionedEntity extends MdsVersionedEntity {

    public TestMdsVersionedEntity(String stringField) {
        this.stringField = stringField;
    }

    @Field
    private String stringField;

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }
}

