package org.motechproject.mds.repository;

import org.motechproject.mds.domain.Entity;
import org.springframework.stereotype.Repository;

@Repository
public class EntityRepository extends GenericRepository<Entity> {
    public EntityRepository() {
    }

    public Entity findByName(String name) {
        return null;
    }

    public void create(Entity capture) {

    }
}
