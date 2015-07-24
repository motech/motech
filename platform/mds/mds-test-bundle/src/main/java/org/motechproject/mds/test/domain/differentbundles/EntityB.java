package org.motechproject.mds.test.domain.differentbundles;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class EntityB {

    @Field
    private Long id;

    @Field
    private String name;

    @Field
    private Priority priority;

    @Field
    private EntityC entityC;

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

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public EntityC getEntityC() {
        return entityC;
    }

    public void setEntityC(EntityC entityC) {
        this.entityC = entityC;
    }
}
