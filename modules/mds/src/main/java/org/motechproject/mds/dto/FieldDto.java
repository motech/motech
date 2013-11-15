package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Map;

/**
 * The <code>FieldDto</code> class contains information about an existing field in an entity.
 */
public class FieldDto extends SettingsDto {
    private String id;
    private String entityId;
    private TypeDto type;
    private FieldBasicDto basic;
    private Map<String, String> metadata;

    public FieldDto() {
        this(null, null, null, null, null);
    }

    public FieldDto(String id, String entityId, TypeDto type, FieldBasicDto basic, Map<String, String> metadata,
                    SettingDto... settings) {
        super(settings);
        this.id = id;
        this.entityId = entityId;
        this.type = type;
        this.basic = basic;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public TypeDto getType() {
        return type;
    }

    public void setType(TypeDto type) {
        this.type = type;
    }

    public FieldBasicDto getBasic() {
        return basic;
    }

    public void setBasic(FieldBasicDto basic) {
        this.basic = basic;
    }

    public Map getMetadata() {
        return metadata;
    }

    public void setMetadata(Map metadata) {
        this.metadata = metadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
