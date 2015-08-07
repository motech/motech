package org.motechproject.mds.dto;

/**
 * Contains information about single lookup added via JSON file.
 */
public class JsonLookupDto {

    private String originLookupName;
    private String entityClassName;

    public JsonLookupDto(String entityClassName, String originLookupName) {
        this.entityClassName = entityClassName;
        this.originLookupName = originLookupName;
    }

    public String getOriginLookupName() {
        return originLookupName;
    }

    public void setOriginLookupName(String originLookupName) {
        this.originLookupName = originLookupName;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }
}
