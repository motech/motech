package org.motechproject.mds.helper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.repository.AllEntities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * The <code>RelationshipResolver</code> class provides a method that removes unresolved entities from a set.
 * Entity is considered unresolved if at least one of its dependencies is not contained in provided set or
 * does not exist in database.
 *
 * @see org.motechproject.mds.domain.Entity
 */
@Component
public class RelationshipResolver {

    private AllEntities allEntities;

    public List<Entity> removeUnresolvedEntities(Set<Entity> entities) {

        List<Entity> resolvedEntities = new ArrayList<>();
        EntitiesRetriever entitiesRetriever = new EntitiesRetriever(entities);

        for (Entity entity : entities) {
            if (isEntityResolved(entity, new ArrayList<Entity>(), new ArrayList<Entity>(), entitiesRetriever)) {
                resolvedEntities.add(entity);
            }
        }

        return resolvedEntities;
    }

    private boolean isEntityResolved(Entity entity, List<Entity> visitedEntities, List<Entity> resolvedEntities, EntitiesRetriever entitiesRetriever) {
        if (resolvedEntities.contains(entity)) {
            return true;
        } else {
            visitedEntities.add(entity);
            for (String relatedEntityClass : EntityHelper.getRelatedEntityClasses(entity)) {
                Entity relatedEntity = entitiesRetriever.retrieveEntity(relatedEntityClass);
                if (null == relatedEntity) {
                    return false;
                } else if (!visitedEntities.contains(relatedEntity) &&
                        !isEntityResolved(relatedEntity, visitedEntities, resolvedEntities, entitiesRetriever)) {
                    return false;
                }
            }
            resolvedEntities.add(entity);
            return true;
        }
    }

    private class EntitiesRetriever {
        private Set<Entity> entities;
        private List<Entity> databaseEntities;

        public EntitiesRetriever(Set<Entity> entities) {
            this.entities = entities;
            this.databaseEntities = allEntities.retrieveAll();
        }

        public Entity retrieveEntity(String entityClass) {
            Entity entity = retrieveEntityByClassName(entities, entityClass);
            if (null == entity) {
                entity = retrieveEntityByClassName(databaseEntities, entityClass);
            }
            return entity;
        }

        private Entity retrieveEntityByClassName(Collection<Entity> entities, String entityClass) {
            for (Entity entity : entities) {
                if (StringUtils.equals(entity.getClassName(), entityClass)) {
                    return entity;
                }
            }
            return null;
        }
    }

    @Autowired
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }
}
