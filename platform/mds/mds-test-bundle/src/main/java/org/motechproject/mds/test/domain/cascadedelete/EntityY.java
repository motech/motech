package org.motechproject.mds.test.domain.cascadedelete;

import java.util.ArrayList;
import java.util.List;

import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsEntity;

@Entity
public class EntityY extends MdsEntity{
    @Field
    private EntityX x;
    
    @Field
    @Cascade(delete = true)
    private List<EntityZ> z;

    public EntityY() {
        this.z = new ArrayList<>();
    }

    public List<EntityZ> getZ() {
        return z;
    }

    public void setZ(List<EntityZ> z) {
        this.z = z;
    }

    public EntityX getX() {
        return x;
    }

    public void setX(EntityX x) {
        this.x = x;
    }
}
