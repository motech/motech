package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The <code>SettingsDto</code> is a abstract class that present a dto with a list of settings.
 */
public abstract class SettingsDto {
    private List<SettingDto> settings;

    public SettingsDto(SettingDto... settings) {
        if (null != settings && settings.length > 0) {
            this.settings = new LinkedList<>();

            Collections.addAll(this.settings, settings);
        }
    }

    public List<SettingDto> getSettings() {
        return settings;
    }

    public void setSettings(List<SettingDto> settings) {
        this.settings = settings;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
