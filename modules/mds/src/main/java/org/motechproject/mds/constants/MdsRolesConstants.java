package org.motechproject.mds.constants;

/**
 * Provides all the mds roles constants.
 */
public final class MdsRolesConstants {
    public static final String SCHEMA_ACCESS = "seussSchemaAccess";
    public static final String SETTINGS_ACCESS = "seussSettingsAccess";
    public static final String DATA_ACCESS = "seussDataAccess";

    public static final String HAS_SCHEMA_ACCESS = "hasRole('" + SCHEMA_ACCESS + "')";
    public static final String HAS_SETTINGS_ACCESS = "hasRole('" + SETTINGS_ACCESS + "')";
    public static final String HAS_DATA_ACCESS = "hasRole('" + DATA_ACCESS + "')";
    public static final String HAS_DATA_OR_SCHEMA_ACCESS = "hasAnyRole('" + SCHEMA_ACCESS + "', '" + DATA_ACCESS + "')";
    public static final String HAS_ANY_SEUSS_ROLE = "hasAnyRole('" + SCHEMA_ACCESS + "', '" + DATA_ACCESS + "', '" + SETTINGS_ACCESS + "')";

    private MdsRolesConstants() {
    }
}
