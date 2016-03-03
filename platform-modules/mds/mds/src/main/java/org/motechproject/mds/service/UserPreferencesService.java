package org.motechproject.mds.service;

import org.motechproject.mds.dto.UserPreferencesDto;

import java.util.List;


/**
 * The <code>UserPreferencesService</code> provides API for managing the entity user preferences (grid size, visible fields).
 *
 * @see org.motechproject.mds.domain.UserPreferences
 */
public interface UserPreferencesService {

    /**
     * Returns preferences for entity with the given id.
     *
     * @param entityId the id of  the entity for the preferences
     * @return list of user preferences for the entity
     */
    List<UserPreferencesDto> getEntityPreferences(Long entityId);

    /**
     * Returns preferences for given user and entity. If preferences don't exist then default ones will be created.
     *
     * @param entityId the id of  the entity for the preferences
     * @param username the owner of the preferences
     * @return user preferences for the entity and username
     */
    UserPreferencesDto getUserPreferences(Long entityId, String username);

    /**
     * Updates grid size preferences for given user and entity. If preferences don't exist then default ones will be created.

     * @param entityId the id of the entity for the preferences
     * @param username the owner of the preferences
     * @param newSize the new size og the grid
     */
    void updateGridSize(Long entityId, String username, Integer newSize);

    /**
     * Adds field with the given name to the user visible fields. If preferences don't exist then default ones will be
     * created.
     *
     * @param entityId the id of  the entity for the preferences
     * @param username the owner of the preferences
     * @param fieldName the name of the field to add
     */
    void selectField(Long entityId, String username, String fieldName);

    /**
     * Removes field with the given name from the user visible fields. If preferences don't exist then default ones will be
     * created.
     *
     * @param entityId the id of  the entity for the preferences
     * @param username the owner of the preferences
     * @param fieldName the name of the field to remove
     */
    void unselectField(Long entityId, String username, String fieldName);

    /**
     * Adds all entity fields to the user visible fields. If preferences don't exist then default ones will be created.
     *
     * @param entityId the id of  the entity for the preferences
     * @param username the owner of the preferences
     */
    void selectFields(Long entityId, String username);

    /**
     * Removes all entity fields from the user visible fields. If preferences don't exist then default ones will be created.
     *
     * @param entityId the id of  the entity for the preferences
     * @param username the owner of the preferences
     */
    void unselectFields(Long entityId, String username);

    /**
     * Removes user preferences for entity with the given id.
     *
     * @param entityId he id of entity
     * @param username the owner of the preferences
     */
    void removeUserPreferences(Long entityId, String username);
}
