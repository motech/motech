package org.motechproject.openmrs.model;

public enum RegistrationMode {
    USE_PREPRINTED_ID("Pre-printed MoTECH Id"), AUTO_GENERATE_ID("Auto-generated MoTECH Id");

    private String userFriendlyName;

    RegistrationMode(String userFriendlyName) {
        this.userFriendlyName = userFriendlyName;
    }

    public String getUserFriendlyName() {
        return userFriendlyName;
    }
}
