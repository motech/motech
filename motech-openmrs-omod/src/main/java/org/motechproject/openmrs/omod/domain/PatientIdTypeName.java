package org.motechproject.openmrs.omod.domain;

public enum PatientIdTypeName {
    PATIENT_IDENTIFIER_FACILITY_ID("MoTeCH Facility Id"),
    PATIENT_IDENTIFIER_MOTECH_ID("MoTeCH Id"),
    PATIENT_IDENTIFIER_STAFF_ID("MoTeCH Staff Id");

    private String key;

    PatientIdTypeName(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
