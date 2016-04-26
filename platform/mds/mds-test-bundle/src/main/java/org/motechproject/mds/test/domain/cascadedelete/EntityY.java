package org.motechproject.mds.test.domain.cascadedelete;

import java.util.HashSet;
import java.util.Set;

import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsEntity;

@Entity
public class EntityY extends MdsEntity{
    @Field
    private Set<EntityX> x;
     
    @Field
    @Cascade(delete = true)
    private Set<EntityZ> z;
    
    public EntityY() {
        this.x = new HashSet<>();
        this.z = new HashSet<>();
    }

    public Set<EntityZ> getZ() {
        return z;
    }

    public void setZ(Set<EntityZ> z) {
        this.z = z;
    }

    public Set<EntityX> getX() {
        return x;
    }

    public void setX(Set<EntityX> x) {
        this.x = x;
    }
}
