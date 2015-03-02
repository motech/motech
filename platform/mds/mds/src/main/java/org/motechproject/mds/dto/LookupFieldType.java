package org.motechproject.mds.dto;

/**
 * The lookup type represents whether the lookup will be done by comparing to a single field,
 * matching values to a range, or matching to a set of values.
 */
public enum LookupFieldType {
    /**
     * Single value lookup field.
     */
    VALUE,

    /**
     * Lookup field that accepts a range of values, specified by a minimum and maximum values.
     */
    RANGE,

    /**
     * Lookup field that accepts a collection of values.
     */
    SET
}
