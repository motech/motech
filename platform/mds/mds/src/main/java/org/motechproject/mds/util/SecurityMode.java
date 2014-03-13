package org.motechproject.mds.util;

/**
 * This enum describes security mode for an entity
 */
public enum SecurityMode {
    EVERYONE,
    OWNER,
    CREATOR,
    USERS,
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

    public boolean isIntanceRestriction() {
        return this == CREATOR || this == OWNER;
    }
}
