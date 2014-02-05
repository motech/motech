package org.motechproject.mds.dto;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.motechproject.mds.util.NumberPredicate;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>BrowsingSettingsDto</code> contains informations about filed browsing settings
 */
public class BrowsingSettingsDto {

    private List<Number> filterableFields = new ArrayList<>();
    private List<Number> displayedFields = new ArrayList<>();

    public void addFilterableField(Number id) {
        this.filterableFields.add(id);
    }

    public void removeFilterableField(Number id) {
        this.filterableFields.remove(id);
    }

    public boolean containsFilterableField(Number number) {
        return CollectionUtils.exists(filterableFields, new NumberPredicate(number));
    }

    public List<Number> getFilterableFields() {
        return filterableFields;
    }

    public void setFilterableFields(List<Number> filterableFields) {
        this.filterableFields = null != filterableFields
                ? filterableFields
                : new ArrayList<Number>();
    }

    public void addDisplayedField(Number id) {
        this.displayedFields.add(id);
    }

    public boolean containsDisplayedField(Long number) {
        return CollectionUtils.exists(displayedFields, new NumberPredicate(number));
    }

    public long indexOfDisplayedField(Long id) {
        NumberPredicate predicate = new NumberPredicate(id);
        Long idx;

        for (idx = 0L; idx < displayedFields.size(); idx++) {
            if (predicate.evaluate(displayedFields.get(idx.intValue()))) {
                break;
            }
        }

        return idx;
    }

    public List<Number> getDisplayedFields() {
        return displayedFields;
    }

    public void setDisplayedFields(List<Number> displayedFields) {
        this.displayedFields = null != displayedFields
                ? displayedFields
                : new ArrayList<Number>();
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
