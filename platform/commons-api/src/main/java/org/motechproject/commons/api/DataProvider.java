package org.motechproject.commons.api;

import java.util.Map;

/**
 * Interface for classes that act as data providers for Tasks.
 */
public interface DataProvider {

    /**
     * Returns data provider name.
     *
     * @return the data provider name
     */
    String getName();

    /**
     * Converts data provider to json.
     *
     * @return json stored as {@code String}
     */
    String toJSON();

    /**
     * Returns single object matching given conditions.
     *
     * @param type  the type of searched object
     * @param lookupName  the name of used lookup
     * @param lookupFields  the map of fields names and expected values
     * @return single object matching conditions
     */
    Object lookup(String type, String lookupName, Map<String, String> lookupFields);

    /**
     * Checks if given type is supported by the {@code DataProvider}.
     *
     * @param type  the type to be checked
     * @return true if type is supported, false otherwise
     */
    boolean supports(String type);

}
