package org.motechproject.mds.test.domain.cascadedelete;

import java.util.ArrayList;
import java.util.List;

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
    private List<EntityY> y;
    
    public EntityX() {
        this.y = new ArrayList<>();
    }

    public List<EntityY> getY() {
        return y;
    }

    public void setY(List<EntityY> y) {
        this.y = y;
    }
}
