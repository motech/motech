package org.motechproject.mds.dto;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * Represents a field added to a lookup. The lookup using a given field can be done using multiple lookup types.
 */
public class LookupFieldDto {

    private Long id;
    private String name;
    private String relatedName;
    private LookupFieldType type;
    private String customOperator;
    private boolean useGenericParam;
    private String className;
    private String displayName;
    private String relatedFieldDisplayName;
    private List<SettingDto> settings;

    public LookupFieldDto() {
    }

    public LookupFieldDto(String name, LookupFieldType type) {
        this(null, name, type);
    }

    public LookupFieldDto(Long id, String name, LookupFieldType type) {
        this(id, name, type, null);
    }

    public LookupFieldDto(String name, LookupFieldType type, String customOperator) {
        this(null, name, type, customOperator, false, null);
    }

    public LookupFieldDto(Long id, String name, LookupFieldType type, String customOperator) {
        this(id, name, type, customOperator, false, null);
    }

    public LookupFieldDto(Long id, String name, LookupFieldType type, String customOperator, boolean useGenericParam, String relatedName) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.customOperator = customOperator;
        this.useGenericParam = useGenericParam;
        this.relatedName = relatedName;
    }

    public String getLookupFieldName() {
        if (StringUtils.isNotBlank(getRelatedName())) {
            return getName() + "." + getRelatedName();
        }
        return getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LookupFieldType getType() {
        return type;
    }

    public void setType(LookupFieldType type) {
        this.type = type;
    }

    public String getCustomOperator() {
        return customOperator;
    }

    public void setCustomOperator(String customOperator) {
        this.customOperator = customOperator;
    }

    public boolean isUseGenericParam() {
        return useGenericParam;
    }

    public void setUseGenericParam(boolean useGenericParam) {
        this.useGenericParam = useGenericParam;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<SettingDto> getSettings() {
        return settings;
    }

    public void setSettings(List<SettingDto> settings) {
        this.settings = settings;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRelatedName() {
        return relatedName;
    }

    public void setRelatedName(String relatedName) {
        this.relatedName = relatedName;
    }

    public String getRelatedFieldDisplayName() {
        return relatedFieldDisplayName;
    }

    public void setRelatedFieldDisplayName(String relatedFieldDisplayName) {
        this.relatedFieldDisplayName = relatedFieldDisplayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LookupFieldDto)) {
            return false;
        }

        LookupFieldDto that = (LookupFieldDto) o;

        return Objects.equals(that.getId(), id) && Objects.equals(that.getName(), name) &&
                Objects.equals(that.getCustomOperator(), customOperator) &&
                Objects.equals(that.getType(), type) &&
                Objects.equals(that.isUseGenericParam(), useGenericParam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
