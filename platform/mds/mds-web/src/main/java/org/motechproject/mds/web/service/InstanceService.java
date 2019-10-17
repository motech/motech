package org.motechproject.mds.web.service;

import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.exception.entity.EntityInstancesNonEditableException;
import org.motechproject.mds.filter.Filters;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.web.domain.BasicEntityRecord;
import org.motechproject.mds.web.domain.BasicHistoryRecord;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.commons.api.Records;
import org.motechproject.mds.web.domain.RelationshipsUpdate;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * The <code>InstanceService</code> interface, defines methods responsible for executing actions on the
 * instances of the entity.
 */
public interface InstanceService {

    /**
     * Gets a total number of instances, for the given entity.
     *
     * @param entityId id of the entity
     * @return number of instances
     */
    long countRecords(Long entityId);

    /**
     * Saves the given instance representation in MDS. If the representation has the id field assigned,
     * an update will be performed. Otherwise, it will attempt to create a new instance.
     *
     * @param entityRecord representation of the instance
     * @return Created instance
     * @throws org.motechproject.mds.exception.object.ObjectNotFoundException if the id is specified, but
     *         the instance of such id does not exist
     * @throws org.motechproject.mds.exception.object.ObjectUpdateException if any problem arises while saving the instance
     */
    Object saveInstance(EntityRecord entityRecord);

    /**
     * Saves the given instance representation in MDS. If the representation has the id field assigned,
     * an update will be performed. Otherwise, it will attempt to create an instance. If the id is specified,
     * but the instance of such id does not exist, it throws {@link org.motechproject.mds.exception.object.ObjectNotFoundException}.
     * If any problem arises while saving the the instance, it throws {@link org.motechproject.mds.exception.object.ObjectUpdateException}.
     * This method is capable of removing the blob field values.
     *
     * @param entityRecord representation of the instance
     * @param deleteValueFieldId blob field ID, to clear the value for; it will not be effective for any other field types
     * @return Created instance
     */
    Object saveInstance(EntityRecord entityRecord, Long deleteValueFieldId);

    /**
     * Returns all instances of the entity, that the current user has access to. Additionally, allows to
     * tamper the results, using query parameters (eg. to limit the number of retrieves records).
     * Throws {@link org.motechproject.mds.exception.entity.EntityNotFoundException}, if entity of
     * the given id does not exist.
     *
     * @param entityId id of the entity
     * @param queryParams query parameters to use, retrieving instances
     * @return a list of instances
     */
    List<BasicEntityRecord> getEntityRecords(Long entityId, QueryParams queryParams);

    /**
     * Returns all instances of the entity, that the current user has access to.
     * Throws {@link org.motechproject.mds.exception.entity.EntityNotFoundException}, if entity of
     * the given id does not exist.
     *
     * @param entityId id of the entity
     * @return a list of instances
     */
    List<BasicEntityRecord> getEntityRecords(Long entityId);

    /**
     * Retrieves all fields of the entity. This will not include draft fields, created, modified
     * or deleted by the current user. Throws {@link org.motechproject.mds.exception.entity.EntityNotFoundException},
     * if entity of the given id does not exist.
     *
     * @param entityId id of the entity
     * @return all fields of the entity
     */
    List<FieldDto> getEntityFields(Long entityId);

    /**
     * Retrieves and executes a lookup of the given name, from the given entity. Additionally, allows to
     * tamper the results, using query parameters (eg. to limit the number of retrieves records). It will throw
     * {@link org.motechproject.mds.exception.lookup.LookupExecutionException} in case the provided lookup
     * parameters do not match the lookup definition or if the execution could not be performed for any
     * other reason. It will also throw {@link org.motechproject.mds.exception.entity.EntityNotFoundException} and
     * {@link org.motechproject.mds.exception.lookup.LookupNotFoundException} if entity of the given id or lookup
     * of the given name does not exist.
     *
     * @param entityId id of the entity
     * @param lookupName name of the lookup
     * @param lookupMap map, containing lookup parameters with their respective values
     * @param queryParams query parameters to use, retrieving instances
     * @return a list of instances, retrieved using the given lookup
     */
    List<BasicEntityRecord> getEntityRecordsFromLookup(Long entityId, String lookupName, Map<String, Object> lookupMap,
                                                  QueryParams queryParams);

    /**
     * Retrieves all instances of the given entity, that match criteria, specified in the provided
     * filters. Additionally, allows to tamper the results, using query parameters
     * (eg. to limit the number of retrieves records). It will throw {@link org.motechproject.mds.exception.entity.EntityNotFoundException}
     * when entity of given id does not exist.
     *
     * @param entityId id of the entity
     * @param filters filters to use, retrieving instances
     * @param queryParams query parameters to use, retrieving instances
     * @return a list of instances, matching given filters
     */
    List<BasicEntityRecord> getEntityRecordsWithFilter(Long entityId, Filters filters, QueryParams queryParams);

    /**
     * Returns field definitions of the given entity, assigned to the given instance.
     * Throws {@link org.motechproject.mds.exception.entity.EntityNotFoundException} if entity of the
     * given id does not exist.
     *
     * @param entityId id of the entity
     * @param instanceId id of the instance
     * @return a list of field definitions
     */
    List<FieldInstanceDto> getInstanceFields(Long entityId, Long instanceId);

    /**
     * Retrieves a list of historical revisions for the given instance. Current revision
     * will not be a part of the result. Additionally, allows to tamper the results, using query parameters
     * (eg. to limit the number of retrieves records).
     *
     * @param entityId id of the entity
     * @param instanceId id of the instance
     * @param queryParams query parameters to use, retrieving instances
     * @return a list of historical revisions for the instance
     */
    List<BasicHistoryRecord> getInstanceHistory(Long entityId, Long instanceId, QueryParams queryParams);

    /**
     * Retrieves a single, historical revision of an instance, by its id.
     *
     * @param entityId id of the entity
     * @param instanceId id of the instance
     * @param historyId id of the historical revision
     * @return the historical revision of the instance
     */
    HistoryRecord getHistoryRecord(Long entityId, Long instanceId, Long historyId);

    /**
     * Returns total count of historical revisions, for the given instance. Current revision is not
     * included in the count.
     *
     * @param entityId id of the entity
     * @param instanceId id of the instance
     * @return total count of historical revisions
     */
    long countHistoryRecords(Long entityId, Long instanceId);

    /**
     * Prepares a new instance representation. Builds available fields and populates
     * some of the auto-generated fields.
     *
     * @param entityId id of the entity
     * @return the new instance representation
     */
    EntityRecord newInstance(Long entityId);

    /**
     * Retrieves an instance of the given entity, by its id. If there's no instance of such id,
     * for the given entity, it throws {@link org.motechproject.mds.exception.object.ObjectNotFoundException}.
     *
     * @param entityId id of the entity
     * @param instanceId id of the instance
     * @return an instance representation
     */
    EntityRecord getEntityInstance(Long entityId, Long instanceId);

    /**
     * Retrieves MOTECH Data service for the class, related with the field of the given id from an entity.
     * The service is then queried for the instance of the given id. If such instance is not found, it throws
     * {@link org.motechproject.mds.exception.object.ObjectNotFoundException}. If such instance exists, its representation
     * is prepared and returned.
     *
     * @param entityId id of the entity
     * @param fieldId id of the field from entity
     * @param instanceId id of the instance, from the related entity
     * @return the related field representation
     */
    FieldRecord getInstanceValueAsRelatedField(Long entityId, Long fieldId, Long instanceId);

    /**
     * Retrieves total count of the instances, that match given lookup criteria. Throws
     * {@link org.motechproject.mds.exception.lookup.LookupNotFoundException} if lookup of the given name
     * does not exist for the given entity. It will also throw {@link org.motechproject.mds.exception.lookup.LookupExecutionException}
     * if lookup parameters do not match their definition.
     *
     * @param entityId id of the entity
     * @param lookupName name of the lookup
     * @param lookupMap map, containing lookup parameters with their respective values
     * @return total count, of the instances that match lookup criteria
     */
    long countRecordsByLookup(Long entityId, String lookupName, Map<String, Object> lookupMap);

    /**
     * Retrieves total count of the instances, that match given filter criteria.
     *
     * @param entityId id of the entity
     * @param filters filters to use
     * @return total count, of the instances that match filter criteria
     */
    long countRecordsWithFilters(Long entityId, Filters filters);

    /**
     * Removes an instance from MDS. Depending on the settings, it will either be moved to trash or
     * deleted permanently.
     *
     * @param entityId id of the entity
     * @param instanceId id of the instance
     */
    void deleteInstance(Long entityId, Long instanceId);

    /**
     * Removes all instances from MDS. Depending on the settings, it will either be moved to trash or
     * deleted permanently.
     *
     * @param entityId id of the entity
     */
    void deleteAllInstances(Long entityId);

    /**
     * Removes selected instances from MDS. Depending on the settings, it will either be moved to trash or
     * deleted permanently.
     *
     * @param entityId id of the entity
     * @param instanceIds list of ids of the instances
     */
    void deleteSelectedInstances(Long entityId, List<Long> instanceIds);

    /**
     * Brings back an instance from trash. This, in fact, creates a new instance in MDS and assigns identical
     * field values as the deleted instance, after which the deleted instance is removed permanently. Old id
     * of the instance will not be persisted.
     *
     * @param entityId id of the entity
     * @param instanceId id of the instance in trash
     */
    void revertInstanceFromTrash(Long entityId, Long instanceId);

    /**
     * Retrieves a historical revision of an instance and attempts to revert specified instance
     * to its previous state. It will throw {@link org.motechproject.mds.exception.entity.EntitySchemaMismatchException} if
     * two instances are from two different schemas (meaning, that the entity has been changed at least once).
     *
     * @param entityId id of the entity
     * @param instanceId id of the instance
     * @param historyId id of the historical revision
     */
    void revertPreviousVersion(Long entityId, Long instanceId, Long historyId);

    /**
     * Retrieves instances of the given entity, placed in trash. This will only return instances,
     * that have been moved to trash on the given entity schema. Additionally, allows to
     * tamper the results, using query parameters (eg. to limit the number of retrieves records).
     *
     * @param entityId id of the entity
     * @param queryParams query parameters to use, retrieving instances from trash
     * @return list of instances, placed in trash
     */
    List<BasicEntityRecord> getTrashRecords(Long entityId, QueryParams queryParams);

    /**
     * Returns total count of the instances of the given entity, placed in trash. This will only consider instances,
     * that have been moved to trash on the given entity schema.
     *
     * @param entityId id of the entity
     * @return count of instances, placed in trash
     */
    long countTrashRecords(Long entityId);

    /**
     * Allows to retrieve a single instance, that has been moved to trash.
     *
     * @param entityId id of the entity
     * @param instanceId id of the instance in trash
     * @return instance representation
     */
    EntityRecord getSingleTrashRecord(Long entityId, Long instanceId);

    /**
     * Retrieves value of a single field of an instance. Throws {@link org.motechproject.mds.exception.entity.EntityNotFoundException}
     * if entity of given id does not exist.
     *
     * @param entityId id of the entity
     * @param instanceId id of the instance
     * @param fieldName name of the field to retrieve
     * @throws InstanceNotFoundException if instance with the given id does not exist
     * @return value of the field
     */
    Object getInstanceField(Long entityId, Long instanceId, String fieldName) throws InstanceNotFoundException;

    /**
     * Checks whether the logged in user has access to the entity with the given ID.
     *
     * @param entityId the id of the entity
     */
    void verifyEntityAccess(Long entityId);

    /** Checks whether the entity with the given ID is non editable.
     *
     * @param entityId the id of the entity
     * @throws EntityInstancesNonEditableException if the entity is non editable
     */
    void validateNonEditableProperty(Long entityId);

    /**
     * Returns the related field as collection, applying filtering. Allows retrieval of related fields for
     * grids, etc.
     * @param entityId the id of entity (the entity with the related field)
     * @param instanceId the id of the instance we want to retrieve the field for
     * @param fieldName the name of the related field
     * @param filter contains related fields that have been removed or added on the UI
     * @param queryParams the query params which will be used for retrieval
     * @return the records object containing the values for the related field
     */
    Records<BasicEntityRecord> getRelatedFieldValue(Long entityId, Long instanceId, String fieldName, RelationshipsUpdate filter, QueryParams queryParams);
}
