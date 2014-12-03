package org.motechproject.mds.domain;

/**
 * The <code>FieldInfo</code> class contains base information about the given entity field like its
 * name or type.
 *
 * @see org.motechproject.mds.service.JarGeneratorService
 */
public class FieldInfo {
    private String name;
    private String displayName;
    private String type;
    private boolean required;

    public FieldInfo(String name, String displayName, String type, boolean required) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
