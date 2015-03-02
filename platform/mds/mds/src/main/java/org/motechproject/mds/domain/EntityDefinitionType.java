package org.motechproject.mds.domain;

/**
 * Represents entity origin.
 */
public enum EntityDefinitionType {
    /**
     * Developer Defined Entity. Entity, that has been created by developer, using MDS annotations.
     */
    DDE,
    /**
     * End User Defined Entity. Entity, that has been created by user, using MDS User Interface.
     */
    EUDE
}
