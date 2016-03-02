package org.motechproject.mds.helper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.domain.RelationshipHolder;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.exception.entity.InvalidEntitySettingsException;
import org.motechproject.mds.exception.entity.InvalidRelationshipException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_FIELD;

/**
 * The <code>EntitySorter</code> is a helper class that allows to sort and validate entities.
 */
public final class EntitySorter {

    /**
     * Takes a list of entities and sorts them, according to relationships they have. The entities
     * that have uni-directional relationship with another entity, will be moved to the position
     * behind the entity they are related with. The bi-directional relationships are not sorted,
     * moreover if invalid bi-directional relationship is found, an exception is thrown.
     *
     * @param schemaHolder the holder of the current MDS schema
     * @return List of entities, sorted by relationship
     */
    public static List<EntityDto> sortByHasARelation(List<EntityDto> allEntities, SchemaHolder schemaHolder) {
        List<EntityDto> sorted = new ArrayList<>(allEntities);
        MultiValueMap<String, String> unresolvedRelations = new LinkedMultiValueMap<>();

        // we need to check if classes have 'has-a' relation
        // these classes should be later in list
        // we do that after all entities will be added to sorted list
        for (int i = 0; i < sorted.size(); ++i) {
            EntityDto entity = sorted.get(i);
            List<FieldDto> fields = (List<FieldDto>) CollectionUtils.select(
                    schemaHolder.getFields(entity),
                    new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return object instanceof FieldDto && ((FieldDto) object).getType().isRelationship();
                }
            });

            if (CollectionUtils.isNotEmpty(fields)) {
                int max = i;

                for (FieldDto field : fields) {
                    final RelationshipHolder holder = new RelationshipHolder(field);

                    // For each field we perform a validation to spot circular, unresolvable relations,
                    // which means the data model is incorrect
                    unresolvedRelations = validateRelationship(unresolvedRelations, entity, holder);
                    assertRelationshipIsHistoryCompatible(entity, holder, allEntities);

                    EntityDto relation = (EntityDto) CollectionUtils.find(sorted, new Predicate() {
                        @Override
                        public boolean evaluate(Object object) {
                            return object instanceof EntityDto
                                    && ((EntityDto) object).getClassName().equalsIgnoreCase(holder.getRelatedClass());
                        }
                    });

                    // In case the relation is bidirectional, we shouldn't move the class,
                    // in order to avoid infinite loop
                    boolean biDirectional = field.getMetadata(RELATED_FIELD) != null;
                    max = Math.max(max, biDirectional ? -1 : sorted.indexOf(relation));
                }

                if (max != i) {
                    sorted.remove(i);
                    --i;

                    if (max < sorted.size()) {
                        sorted.add(max, entity);
                    } else {
                        sorted.add(entity);
                    }

                }
            }
        }

        return sorted;
    }

    /**
     * Takes a list of entities and sorts them by the inheritance tree. The entities that extend
     * the Object class or MdsEntity class will be moved to the beggining of the list. After that,
     * the entites that are already present on the list will be added, up the inheritance tree.
     *
     * @param list Initial list of entities to sort
     * @return List of entities, sorted by inheritance tree
     */
    public static List<EntityDto> sortByInheritance(List<EntityDto> list) {
        List<EntityDto> sorted = new ArrayList<>(list.size());

        // firstly we add entities with base class equal to Object class or MdsEntity class
        for (Iterator<EntityDto> iterator = list.iterator(); iterator.hasNext(); ) {
            EntityDto entity = iterator.next();

            if (entity.isBaseEntity()) {
                sorted.add(entity);
                iterator.remove();
            }
        }

        // then we add entities which base classes are in sorted list
        // we do that after all entities will be added to sorted list
        while (!list.isEmpty()) {
            for (Iterator<EntityDto> iterator = list.iterator(); iterator.hasNext(); ) {
                final EntityDto entity = iterator.next();
                EntityDto superClass = (EntityDto) CollectionUtils.find(sorted, new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        return object instanceof EntityDto
                                && ((EntityDto) object).getClassName().equals(entity.getSuperClass());
                    }
                });

                if (null != superClass) {
                    sorted.add(entity);
                    iterator.remove();
                }
            }
        }

        return sorted;
    }

    private static MultiValueMap<String, String> validateRelationship(MultiValueMap<String, String> unresolvedRelations,
                                                               EntityDto entity, RelationshipHolder holder) {
        List<String> relatedClasses = unresolvedRelations.get(holder.getRelatedClass());

        if (relatedClasses != null && relatedClasses.contains(entity.getClassName()) &&
                StringUtils.isEmpty(holder.getRelatedField())) {
            throw new InvalidRelationshipException(holder.getRelatedClass(), entity.getClassName());
        }

        if (holder.hasUnresolvedRelation() && !entity.getClassName().equals(holder.getRelatedClass())) {
            unresolvedRelations.add(entity.getClassName(), holder.getRelatedClass());
        }

        return unresolvedRelations;
    }

    private static void assertRelationshipIsHistoryCompatible(EntityDto entity, RelationshipHolder holder,
                                                              List<EntityDto> allEntities) {
        EntityDto relatedEntity = findEntityByName(holder.getRelatedClass(), allEntities);

        if (!hasCorrectTrackingSettings(entity, relatedEntity, holder)) {
            String relatedClassName = relatedEntity == null ? "null" : relatedEntity.getClassName();
            throw new InvalidEntitySettingsException(entity.getClassName(), relatedClassName);
        }
    }

    private static boolean hasCorrectTrackingSettings(EntityDto entity, EntityDto relatedEntity,
                                                      RelationshipHolder holder) {
        boolean recordsHistory = entity.isRecordHistory();
        boolean relatedRecordsHistory = relatedEntity.isRecordHistory();

        if (holder.isBiDirectional()) {
            // Both sides of bi-directional relationship must have the same history tracking settings
            return !(recordsHistory ^ relatedRecordsHistory);
        } else {
            // For uni-directional relationship, the related side must not have more strict options than
            // the entity that defines the relationship
            return recordsHistory ? relatedRecordsHistory : true;
        }
    }

    private static EntityDto findEntityByName(String name, List<EntityDto> allEntities) {
        for (EntityDto entity : allEntities) {
            if (entity.getClassName().equals(name)) {
                return entity;
            }
        }

        return null;
    }

    private EntitySorter() {
    }
}
