package org.motechproject.mds.builder.impl;

/**
 * Represents the three lookup methods generated.
 */
enum LookupType {
    /**
     * Simple retrieving lookup.
     */
    SIMPLE,
    /**
     * Paged/ordered retrieving lookup.
     */
    WITH_QUERY_PARAMS,
    /**
     * result count lookup.
     */
    COUNT
}
