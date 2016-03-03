package org.motechproject.mds.test.secondary.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.test.domain.differentbundles.EntityB;

@Entity
public class EntityA {

    @Field
    private Long id;

    @Field
    private String name;

    @Field
    private EntityB entityB;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EntityB getEntityB() {
        return entityB;
    }

    public void setEntityB(EntityB entityB) {
        this.entityB = entityB;
    }
}
