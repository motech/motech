package org.motechproject.mds.dto;

/**
 * The <code>SettingOptions</code> contains available options that can be added to field setting.
 */
public enum SettingOptions {
    /**
     * Force setting a value for a given setting.
     */
    REQUIRE,
    /**
     * Ensure that a value in a given setting is a number and it has a positive value.
     */
    POSITIVE
}
