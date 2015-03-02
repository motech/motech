package org.motechproject.mds.util;

/**
 * This enum describes security mode for an entity
 */
public enum SecurityMode {
    /**
     * Everyone has got an access to the instances of an entity
     */
    EVERYONE,
    /**
     * Only user marked as an owner can access the instance of an entity
     */
    OWNER,
    /**
     * Only user that created an instance can access it.
     */
    CREATOR,
    /**
     * Only specified users can access the instances of an entity.
     */
    USERS,
    /**
     * Only users with specified roles can access the instances of an entity.
     */
    ROLES;

    public static SecurityMode getEnumByName(String value) {
        switch (value.toUpperCase()) {
            case "EVERYONE":
                return EVERYONE;
            case "OWNER":
                return OWNER;
            case "CREATOR":
                return CREATOR;
            case "USERS":
                return USERS;
            case "ROLES":
                return ROLES;
            default:
                return null;
        }
    }

    /**
     * @return true, if this security mode is instance-specific (meaning that only some of
     *         the instances may be affected by it)
     */
    public boolean isIntanceRestriction() {
        return this == CREATOR || this == OWNER;
    }
}
