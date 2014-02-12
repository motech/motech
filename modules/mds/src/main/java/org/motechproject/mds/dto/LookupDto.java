package org.motechproject.mds.dto;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * The <code>LookupDto</code> class contains information about single lookup defined by user
 */
public class LookupDto {
    private Long id;
    private String lookupName;
    private boolean singleObjectReturn;
    private boolean exposedViaRest;
    private List<String> fieldList;

    public LookupDto() {
        this(null, false, false);
    }

    public LookupDto(String lookupName, boolean singleObjectReturn, boolean exposedViaRest) {
        this(lookupName, singleObjectReturn, exposedViaRest, null);
    }

    public LookupDto(String lookupName, boolean singleObjectReturn, boolean exposedViaRest,
                     List<String> fieldList) {
        this.lookupName = lookupName;
        this.singleObjectReturn = singleObjectReturn;
        this.exposedViaRest = exposedViaRest;
        this.fieldList = CollectionUtils.isEmpty(fieldList)
                ? new LinkedList<String>()
                : fieldList;
    }

    public LookupDto(Long id, String lookupName, boolean singleObjectReturn, boolean exposedViaRest, List<String> fieldList) {
        this(lookupName, singleObjectReturn, exposedViaRest, fieldList);
        this.id = id;
    }

    public String getLookupName() {
        return lookupName;
    }

    public void setLookupName(String lookupName) {
        this.lookupName = lookupName;
    }

    public boolean isSingleObjectReturn() {
        return singleObjectReturn;
    }

    public void setSingleObjectReturn(boolean singleObjectReturn) {
        this.singleObjectReturn = singleObjectReturn;
    }

    public boolean isExposedViaRest() {
        return exposedViaRest;
    }

    public void setExposedViaRest(boolean isExposedViaRest) {
        this.exposedViaRest = isExposedViaRest;
    }

    public void addField(Integer field) {
        this.fieldList.add(field.toString());
    }

    public void insertField(Integer idx, Integer fieldId) {
        if (idx != null && idx < fieldList.size()) {
            this.fieldList.remove(idx.intValue());
            this.fieldList.add(idx, fieldId.toString());
        }
    }

    public void removeField(Integer fieldId) {
        this.fieldList.remove(fieldId.toString());
    }

    public List<String> getFieldList() {
        return fieldList;
    }


    public void setFieldList(List<String> fieldList) {
        this.fieldList = CollectionUtils.isEmpty(fieldList)
                ? new LinkedList<String>()
                : fieldList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof LookupDto)) {
            return false;
        }

        LookupDto other = (LookupDto) o;

        return singleObjectReturn == other.singleObjectReturn && Objects.equals(fieldList, other.fieldList) &&
                Objects.equals(lookupName, other.lookupName) && exposedViaRest == other.exposedViaRest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
