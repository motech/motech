package org.motechproject.mds.service;

import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.DraftData;
import org.motechproject.mds.dto.DraftResult;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.dto.TrackingDto;
import org.motechproject.mds.util.SecurityMode;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This interface provides methods related with executing actions on an entity.
 *
 * @see org.motechproject.mds.domain.Entity
 */
public interface EntityService {

    /**
     * Creates an entity and adds default fields, provided the entity does not contain them
     * already (from inheritance). It will throw {@link org.motechproject.mds.exception.entity.EntityAlreadyExistException}
     * if an entity of identical class name already exists.
     *
     * @param entityDto representation of an entity to construct from.
     * @return representation of a created entity.
     */
    EntityDto createEntity(EntityDto entityDto);

    /**
     * Returns all entities, that are currently stored in the database. This will not return
     * Entity drafts and Entity audit. It will also not perform any security checks on the entities.
     *
     * @return A list of all entities.
     */
    List<EntityDto> listEntities();

    /**
     * Returns all entities, that are currently stored in the database. Allows to filter out entities, that
     * the current user does not have access to. This will not return Entity drafts and Entity audit.
     *
     * @param withSecurityCheck set to true, if you wish to filter out entities, that current user does not have access to
     * @return A list of all entities, that currently logged user has got access to.
     */
    List<EntityDto> listEntities(boolean withSecurityCheck);

    /**
     * Returns all entities of the bundle with the given name. This will not return Entity drafts and Entity audit.
     * It will also not perform any security checks on the entities.
     *
     * @param bundleSymbolicName the symbolic name of the bundle
     * @return A list of the bundle entities.
     */
    List<EntityDto> listEntitiesByBundle(String bundleSymbolicName);

    /**
     * Returns an entity of the given id. If an entity with given id does not exist, it will return null.
     *
     * @param entityId The id of an entity.
     * @return Entity with given id or null.
     */
    EntityDto getEntity(Long entityId);

    /**
     * Returns a map which contains lookup fields. Fields may come from related entities. Map keys represents lookup
     * fields name which can contains a dot operator(for example relationshipField.id).
     *
     * @param entityId The id of an entity
     * @param lookupName name of a lookup
     * @return a map of lookup fields.
     */
    Map<String, FieldDto> getLookupFieldsMapping(Long entityId, String lookupName);

    /**
     * Removes the entity with the given id. This will also remove all drafts associated with the
     * deleted entity.
     *
     * @param entityId The id of an entity
     */
    void deleteEntity(Long entityId);

    /**
     * Retrieves a list of all entities, that currently authenticated user has applied
     * changes to.
     *
     * @return A list of entities, modified by the user
     */
    List<EntityDto> listWorkInProgress();

    /**
     * Retrieves a draft entity representation, connected to the currently logged in user.
     *
     * @param entityId id of an entity to retrieve
     * @return draft entity representation
     */
    EntityDto getEntityForEdit(Long entityId);

    /**
     * Retrieves entity by the className parameter
     *
     * @param className the className of an entity
     * @return Entity with the given className
     */
    EntityDto getEntityByClassName(String className);


    /**
     * Retrieves entities for the given package
     *
     * @param packageName the package name
     * @return A list of entities
     */
    List<EntityDto> findEntitiesByPackage(String packageName);

    /**
     * Adds or updates lookups for the given entity.
     *
     * @param entityDto entity representation
     * @param lookups lookups to add or update
     */
    void addLookups(EntityDto entityDto, LookupDto... lookups);

    /**
     * Adds or updates lookups for the given entity.
     *
     * @param entityDto entity representation
     * @param lookups lookups to add or update
     */
    void addLookups(EntityDto entityDto, Collection<LookupDto> lookups);

    /**
     * Adds or updates lookups for the given entity.
     *
     * @param entityId id of an entity
     * @param lookups lookups to add or update
     */
    void addLookups(Long entityId, LookupDto... lookups);

    /**
     * Adds or updates lookups for the given entity.
     *
     * @param entityId id of an entity
     * @param lookups lookups to add or update
     */
    void addLookups(Long entityId, Collection<LookupDto> lookups);

    /**
     * Retrieves a list of all lookups for the given entity. This will not include
     * draft lookups.
     *
     * @param entityId id of an entity
     * @return a list of lookups for an entity
     */
    List<LookupDto> getEntityLookups(Long entityId);

    /**
     * Retrieves a list of all fields for the given entity. This will include draft fields,
     * that the current user has added, deleted or modified in any way.
     *
     * @param entityId id of an entity
     * @return a list of fields for an entity
     */
    List<FieldDto> getFields(Long entityId);

    /**
     * Retrieves a list of all fields for the given entity. This will not include any draft fields.
     *
     * @param entityId id of an entity
     * @return a list of fields for an entity
     */
    List<FieldDto> getEntityFields(Long entityId);

    /**
     * Retrieves a list of all fields for the given entity class name. This will not include any draft fields.
     *
     * @param className the entity class name
     * @return a list of fields for the entity
     */
    List<FieldDto> getEntityFieldsByClassName(String className);

    /**
     * Retrieves a list of all fields for the given entity class name. This will not include any draft fields.
     * Since this for the UI, additional display options such as all combobox values will be added to the resultant
     * fields.
     *
     * @param className the entity class name
     * @return a list of fields for the entity
     */
    List<FieldDto> getEntityFieldsByClassNameForUI(String className);

    /**
     * Retrieves a field by name. This will be able to find any draft fields,
     * that the current user has added, deleted or modified in any way.
     *
     * @param entityId id of an entity
     * @param name name of the field
     * @return Actual or draft field of the given name for given entity id
     */
    FieldDto findFieldByName(Long entityId, String name);

    /**
     * Retrieves a field by name. This will not include draft fields.
     *
     * @param entityId id of an entity
     * @param name name of the field
     * @return Field of the given name for given entity id
     */
    FieldDto findEntityFieldByName(Long entityId, String name);

    /**
     * Retrieves a field by id. This will not include draft fields.
     *
     * @param entityId id of an entity
     * @param fieldId id of the field
     * @return Field of the given name for given entity id
     */
    FieldDto getEntityFieldById(Long entityId, Long fieldId);

    /**
     * Creates, updates or removes draft data for the user. If there's no draft for given user,
     * it will be created. If a draft already exists, the existing draft will get updated.
     *
     * @param entityId id of an actual entity
     * @param draftData data representing changes to the entity
     * @param username the username to whom draft will be assigned
     * @return The result, indicating whether changes have been made and whether a draft is outdated
     */
    DraftResult saveDraftEntityChanges(Long entityId, DraftData draftData, String username);

    /**
     * Creates, updates or removes draft data. The username will be retrieved from the existing security context.
     * If there's no draft for given user, it will be created. If a draft already exists, the existing  draft
     * will get updated.
     *
     * @param entityId id of an actual entity
     * @param draftData data representing changes to the entity
     * @return The result, indicating whether changes have been made and whether a draft is outdated
     */
    DraftResult saveDraftEntityChanges(Long entityId, DraftData draftData);

    /**
     * Removes the draft data permanently.
     *
     * @param entityId id of the draft entity
     */
    void abandonChanges(Long entityId);

    /**
     * Retrieves a draft and attempts to update actual entity, according to the changes present in the draft. The
     * username, for which the draft should be retrieved, will be determined on the current security context.
     * If the draft is outdated, which means that somebody else has already updated the entity, the
     * {@link org.motechproject.mds.exception.entity.EntityChangedException} will be thrown. If the draft is not outdated,
     * a validation will be performed to determine whether the changes are valid and can be applied, and if so
     * the changes will be made and the draft will get deleted.
     *
     * @param entityId id of the draft or actual entity
     * @return a list of modules, affected by the commit
     */
    List<String> commitChanges(Long entityId);

    /**
     * Retrieves a draft for a given user and attempts to update actual entity, according to the changes present in the draft.
     * If the draft is outdated, which means that somebody else has already updated the entity, the
     * {@link org.motechproject.mds.exception.entity.EntityChangedException} will be thrown. If the draft is not outdated,
     * a validation will be performed to determine whether the changes are valid and can be applied, and if so
     * the changes will be made and the draft will get deleted.
     *
     * @param entityId id of the draft or actual entity
     * @return a list of modules, affected by the commit
     */
    List<String> commitChanges(Long entityId, String changesOwner);

    /**
     * Retrieves advanced settings for an entity. This will include any draft changes that the current user has
     * made to the entity.
     *
     * @param entityId id of an entity
     * @return advanced settings for the entity
     */
    AdvancedSettingsDto getAdvancedSettings(Long entityId);

    /**
     * Retrieves advanced settings for an entity.
     *
     * @param entityId id of an entity
     * @param committed a flag indicating whether the settings should come from actual entity or a draft
     * @return advanced settings for the entity
     */
    AdvancedSettingsDto getAdvancedSettings(Long entityId, boolean committed);

    /**
     * Returns the advanced settings for the entity with the given class name. This method
     * is safe, meaning that it will return null for non-existent entities.
     *
     * @param entityClassName the class name of the entity
     * @return the advanced settings of the entity, or null if the entity does not exist
     */
    AdvancedSettingsDto safeGetAdvancedSettingsCommitted(String entityClassName);

    /**
     * Updates rest options for the given entity. If entity of the given id does not exist, it
     * throws {@link org.motechproject.mds.exception.entity.EntityNotFoundException}
     *
     * @param entityId id of an entity
     * @param restOptionsDto new rest options
     */
    void updateRestOptions(Long entityId, RestOptionsDto restOptionsDto);

    /**
     * Updates audit settings for the given entity. If entity of the given id does not exist, it
     * throws {@link org.motechproject.mds.exception.entity.EntityNotFoundException}
     *
     * @param entityId id of an entity
     * @param trackingDto new audit settings
     */
    void updateTracking(Long entityId, TrackingDto trackingDto);

    /**
     * Retrieves the entity draft. The user, for which the draft should be obtained, will be determined
     * on the current security context.
     *
     * @param entityId id of the draft or actual entity
     * @return Entity draft for the user
     */
    EntityDraft getEntityDraft(Long entityId);

    /**
     * Retrieves the entity draft for the given user.
     *
     * @param entityId id of the draft or actual entity
     * @return Entity draft for the user
     */
    EntityDraft getEntityDraft(Long entityId, String username);

    /**
     * Adds fields to the given entity. If the field of identical name already exists in the
     * entity definition, it will be updated. If the entity does not exist, it throws
     * {@link org.motechproject.mds.exception.entity.EntityNotFoundException}
     *
     * @param entity the entity to add fields to
     * @param fields fields to add or update
     */
    void addFields(EntityDto entity, FieldDto... fields);

    /**
     * Adds fields to the given entity. If the field of identical name already exists in the
     * entity definition, it will be updated. If the entity does not exist, it throws
     * {@link org.motechproject.mds.exception.entity.EntityNotFoundException}
     *
     * @param entity the entity to add fields to
     * @param fields fields to add or update
     */
    void addFields(EntityDto entity, Collection<FieldDto> fields);

    /**
     * Adds fields to the given entity. If the field of identical name already exists in the
     * entity definition, it will be updated. If the entity does not exist, it throws
     * {@link org.motechproject.mds.exception.entity.EntityNotFoundException}
     *
     * @param entityId id of the entity to add fields to
     * @param fields fields to add or update
     */
    void addFields(Long entityId, FieldDto... fields);

    /**
     * Adds fields to the given entity. If the field of identical name already exists in the
     * entity definition, it will be updated. If the entity does not exist, it throws
     * {@link org.motechproject.mds.exception.entity.EntityNotFoundException}
     *
     * @param entityId id of the entity to add fields to
     * @param fields fields to add or update
     */
    void addFields(Long entityId, Collection<FieldDto> fields);

    /**
     * Provides ability to point fields, for which UI should provide the ability to filter through.
     * Note, that only several field types support filtering via UI. If a field of not supported type
     * is marked as filterable, this will have no effect.
     *
     * @param entityDto entity representation
     * @param fieldNames the names of the fields, that should be marked filterable
     */
    void addFilterableFields(EntityDto entityDto, Collection<String> fieldNames);

    /**
     * Adds ability to point fields that should be displayed on the data browser by default and allows
     * to set their position on the UI. If not invoked on any field and no field has the
     * {@link org.motechproject.mds.annotations.UIDisplayable} annotation, all the fields, except auto-generated ones
     * will be displayed. If invoked on at least one field, all other fields will get hidden by default.
     *
     * @param entityDto entity representation
     * @param positions a map of field names and their positions. If position is irrelevant, place -1 as entry value
     */
    void addDisplayedFields(EntityDto entityDto, Map<String, Long> positions);

    /**
     * Updates security options for the given entity. If entity of the given id does not exist, it
     * throws {@link org.motechproject.mds.exception.entity.EntityNotFoundException}
     *
     * @param entityId id of an entity
     * @param securityMode new security mode
     * @param securityMembers set of user or role names
     * @param readOnlySecurityMode new read only security mode
     * @param readOnlySecurityMembers set of user or role names for read only security mode
     */
    void updateSecurityOptions(Long entityId, SecurityMode securityMode, Set<String> securityMembers, SecurityMode readOnlySecurityMode, Set<String> readOnlySecurityMembers);

    /**
     * Updated the max fetch depth for a given entity. That fetch depth will be passed to the fetch plan
     * of the persistence manager for that entity.
     *
     * @param entityId the id of the entity to update
     * @param maxFetchDepth the new maximum fetch depth
     */
    void updateMaxFetchDepth(Long entityId, Integer maxFetchDepth);

    /**
     * Provides ability to point fields that should be non-editable via UI.
     *
     * @param entityDto entity representation
     * @param nonEditableFields a map of the non-editable field names and their display values.
     */
    void addNonEditableFields(EntityDto entityDto, Map<String, Boolean> nonEditableFields);

    /**
     * Updates draft entity for the user, determined on the current security context. The update changes
     * the parent entity of the draft to the latest version, which may happen if another user commits changes
     * to the entity.
     *
     * @param entityId id of an entity
     * @return updated draft entity
     */
    EntityDto updateDraft(Long entityId);

    /**
     * Retrieves lookup representation by entity id and lookup name. If entity of given id does not
     * exists, it throws {@link org.motechproject.mds.exception.entity.EntityNotFoundException}. If there is no
     * lookup of such name in the entity, it returns {@code null}.
     *
     * @param entityId id of an entity
     * @param lookupName name of a lookup to retrieve
     * @return Lookup representation, or null if a lookup of given name does not exist
     */
    LookupDto getLookupByName(Long entityId, String lookupName);

    /**
     * Retrieves all fields of an entity, that are marked as displayable. By default, these are all the fields
     * that aren't auto-generated by the MDS. The displayable fields can be adjusted using annotations or
     * {@link #addDisplayedFields(org.motechproject.mds.dto.EntityDto, java.util.Map)} method. If entity of given
     * id does not exist, it throws {@link org.motechproject.mds.exception.entity.EntityNotFoundException}.
     *
     * @param entityId id of an entity
     * @return All fields of the entity, that are marked as displayable
     */
    List<FieldDto> getDisplayFields(Long entityId);

    /**
     * Retrieves current version of the entity schema. The version gets incremented each time the entity gets updated.
     * It throws {@link org.motechproject.mds.exception.entity.EntityNotFoundException} if entity of given class name
     * does not exist.
     *
     * @param entityClassName fully qualified class name of the entity
     * @return schema version for the entity
     */
    Long getCurrentSchemaVersion(String entityClassName);

    /**
     * Increments the version of the entity.
     *
     * @param entityId id of an entity
     * @throws org.motechproject.mds.exception.entity.EntityNotFoundException when entity of the given id does not exist
     */
    void incrementVersion(Long entityId);

    /**
     * Returns the list of fields for the entity, ready to use for the UI. Combobox fields will contain all
     * available options.
     * @param entityId the id of the entity
     * @return the list of fields for the UI
     */
    List<FieldDto> getEntityFieldsForUI(Long entityId);

    /**
     * Retrieves the current MDS schema - entities, fields, lookups, advanced settings etc. This schema can be
     * processed outside of a transacton.
     * @return the current MDS schema
     */
    SchemaHolder getSchema();
}
