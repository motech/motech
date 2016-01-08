package org.motechproject.mds.domain;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class EntityExtension extends Entity {

    @Persistent
    private Entity extendedEntity;

    public Entity getExtendedEntity() {
        return extendedEntity;
    }

    public void setExtendedEntity(Entity extendedEntity) {
        this.extendedEntity = extendedEntity;
    }
}
