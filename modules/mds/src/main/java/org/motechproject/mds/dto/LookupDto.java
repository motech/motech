package org.motechproject.mds.dto;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.LinkedList;
import java.util.List;

/**
 * The <code>LookupDto</code> class contains information about single lookup defined by user
 */
public class LookupDto {
    private String lookupName;
    private boolean singleObjectReturn;
    private List<String> fieldList;

    public LookupDto() {
        this(null, false);
    }

    public LookupDto(String lookupName, boolean singleObjectReturn) {
        this(lookupName, singleObjectReturn, null);
    }

    public LookupDto(String lookupName, boolean singleObjectReturn,
                     List<String> fieldList) {
        this.lookupName = lookupName;
        this.singleObjectReturn = singleObjectReturn;
        this.fieldList = CollectionUtils.isEmpty(fieldList)
                ? new LinkedList<String>()
                : fieldList;
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

    public void addField(String field) {
        this.fieldList.add(field);
    }

    public void insertField(Integer idx, String fieldId) {
        if (idx != null && idx < fieldList.size()) {
            this.fieldList.remove(idx.intValue());
            this.fieldList.add(idx, fieldId);
        }
    }

    public void removeField(String fieldId) {
        this.fieldList.remove(fieldId);
    }

    public List<String> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<String> fieldList) {
        this.fieldList = CollectionUtils.isEmpty(fieldList)
                ? new LinkedList<String>()
                : fieldList;
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
