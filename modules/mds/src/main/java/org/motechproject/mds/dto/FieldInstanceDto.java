package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * The <code>FieldInstanceDto</code> class contains information about an existing field in an instance.
 */
public class FieldInstanceDto {

    private Long id;
    private Long instanceId;
    private FieldBasicDto basic;

    public FieldInstanceDto() {
        this(null, null, null);
    }

    public FieldInstanceDto(Long id, Long instanceId, FieldBasicDto basic) {
        this.id = id;
        this.instanceId = instanceId;
        this.basic = basic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public FieldBasicDto getBasic() {
        return basic;
    }

    public void setBasic(FieldBasicDto basic) {
        this.basic = basic;
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
