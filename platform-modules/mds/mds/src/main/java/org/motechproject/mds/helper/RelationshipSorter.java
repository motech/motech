package org.motechproject.mds.helper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.domain.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The <code>RelationshipSorter</code> class provides method that sorts given entities list using dependency
 * ordering. It means, that if entity A depends on entity B, B will be before A in sorted list. In case of
 * any circular dependencies, entities contained in the cycle are considered equal, thus their mutual positions
 * are unspecified.
 */
public class RelationshipSorter {

    public void sort(List<Entity> entities) {
        final Map<Entity, Integer> entitiesOrder = new HashMap<>();
        EntitiesRetriever entitiesRetriever = new EntitiesRetriever(entities);
        for (Entity entity : entities) {
            traverse(entity, new ArrayList<Entity>(), entitiesOrder, entitiesRetriever);
        }
        Collections.sort(entities, new Comparator<Entity>() {
            @Override
            public int compare(Entity entity1, Entity entity2) {
                return Integer.compare(entitiesOrder.get(entity2), entitiesOrder.get(entity1)); // reverse
            }
        });
    }

    private void traverse(Entity entity, List<Entity> visitedEntities, Map<Entity, Integer> entitiesOrder, EntitiesRetriever entitiesRetriever) {
        if (!visitedEntities.contains(entity)) {
            visitedEntities.add(entity);
            updateEntityOrder(entitiesOrder, entity);
            for (String relatedEntityClass : EntityHelper.getRelatedEntityClasses(entity)) {
                Entity relatedEntity = entitiesRetriever.retrieveEntity(relatedEntityClass);
                if (null != relatedEntity) {
                    traverse(relatedEntity, visitedEntities, entitiesOrder, entitiesRetriever);
                }
            }
        }
    }

    private void updateEntityOrder(Map<Entity, Integer> entitiesOrder, Entity entity) {
        Integer order = entitiesOrder.get(entity);
        if (null == order) {
            order = 0;
        }
        entitiesOrder.put(entity, order + 1);
    }

    private class EntitiesRetriever {
        private List<Entity> entities;

        public EntitiesRetriever(List<Entity> entities) {
            this.entities = entities;
        }

        public Entity retrieveEntity(String entityClass) {
            for (Entity entity : entities) {
                if (StringUtils.equals(entity.getClassName(), entityClass)) {
                    return entity;
                }
            }
            return null;
        }
    }
}
