package org.motechproject.mds.dto;

/**
 * The lookup type represents whether the lookup will be done by comparing to a single field,
 * matching values to a range, or matching to a set of values.
 */
public enum LookupFieldType {
    VALUE, RANGE, SET
}
