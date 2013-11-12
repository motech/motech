package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>BrowsingSettingsDto</code> contains informations about filed browsing settings
 */
public class BrowsingSettingsDto {

    private List<String> filterableFields = new ArrayList<>();
    private List<String> displayedFields = new ArrayList<>();

    public void addFilterableField(String id) {
        this.filterableFields.add(id);
    }

    public void removeFilterableField(String id) {
        this.filterableFields.remove(id);
    }

    public List<String> getFilterableFields() {
        return filterableFields;
    }

    public void setFilterableFields(List<String> filterableFields) {
        this.filterableFields = null != filterableFields ? filterableFields : new ArrayList<String>();
    }

    public List<String> getDisplayedFields() {
        return displayedFields;
    }

    public void setDisplayedFields(List<String> displayedFields) {
        this.displayedFields = null != displayedFields ? displayedFields : new ArrayList<String>();
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
