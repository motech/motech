package org.motechproject.mds.service;

import org.motechproject.mds.dto.JsonLookupDto;

/**
 * Service for managing lookups coming from JSON files.
 */
public interface JsonLookupService {

    /**
     * Stores the given {@code jsonLookup} in the database.
     *
     * @param jsonLookup  the lookup to be stored.
     */
    void createJsonLookup(JsonLookupDto jsonLookup);

    /**
     * Checks if a lookup with the given {@code originLookupName} was already added for the entity with the given
     * {@code entityClassName}.
     *
     * @param entityClassName  the class name of the entity
     * @param originLookupName  the origin name of the lookup
     * @return true if the lookup was added, false otherwise
     */
    boolean exists(String entityClassName, String originLookupName);

}
