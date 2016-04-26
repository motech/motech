package org.motechproject.mds.test.domain.cascadedelete;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.Persistent;

import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsEntity;

@Entity
public class EntityX extends MdsEntity{   
    @Field
    @Cascade(delete = true)
    @Persistent(mappedBy = "x")
    private Set<EntityY> y;

    public EntityX() {
        this.y = new HashSet<>();
    }
    
    public Set<EntityY> getY() {
        return y;
    }

    public void setY(Set<EntityY> y) {
        this.y = y;
    }
}
