package org.motechproject.mds.dto;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The <code>SettingDto</code> contains information about a single setting inside a field.
 */
public class SettingDto {
    private String name;
    private Object value;
    private List<SettingOptions> options = new LinkedList<>();
    private TypeDto type;

    public SettingDto() {
        this(null, null, null);
    }

    public SettingDto(final String name, final Object value, final TypeDto type,
                      final SettingOptions... options) {
        this.name = name;
        this.value = value;
        this.type = type;

        if (!ArrayUtils.isEmpty(options)) {
            Collections.addAll(this.options, options);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getValueAsString() {
        return value == null ? "" : value.toString();
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List<SettingOptions> getOptions() {
        return options;
    }

    public void setOptions(List<SettingOptions> options) {
        this.options = CollectionUtils.isEmpty(options)
                ? new LinkedList<SettingOptions>()
                : options;
    }

    public TypeDto getType() {
        return type;
    }

    public void setType(TypeDto type) {
        this.type = type;
    }

    public SettingDto copy() {
        return new SettingDto(name, value, type, options.toArray(new SettingOptions[options.size()]));
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
