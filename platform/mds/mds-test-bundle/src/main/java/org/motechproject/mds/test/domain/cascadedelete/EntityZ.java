package org.motechproject.mds.test.domain.cascadedelete;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.domain.MdsEntity;

@Entity
public class EntityZ extends MdsEntity{
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
