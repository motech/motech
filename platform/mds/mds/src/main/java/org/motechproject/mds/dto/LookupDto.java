package org.motechproject.mds.dto;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.motechproject.mds.util.LookupName;

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
    private List<Long> fieldList;
    private List<String> fieldNames;
    private boolean readOnly;
    private String methodName;

    public LookupDto() {
        this(null, false, false);
    }

    public LookupDto(String lookupName, boolean singleObjectReturn, boolean exposedViaRest) {
        this(lookupName, singleObjectReturn, exposedViaRest, null, false);
    }

    public LookupDto(String lookupName, boolean singleObjectReturn, boolean exposedViaRest,
                     List<Long> fieldList, boolean readOnly) {
        this(lookupName, singleObjectReturn, exposedViaRest, fieldList, null, readOnly, null);
    }

    public LookupDto(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, List<Long> fieldList,
                     List<String> fieldNames, boolean readOnly, String methodName) {
        this.lookupName = lookupName;
        this.singleObjectReturn = singleObjectReturn;
        this.exposedViaRest = exposedViaRest;
        this.fieldList = CollectionUtils.isEmpty(fieldList)
                ? new LinkedList<Long>()
                : fieldList;
        this.fieldNames = CollectionUtils.isEmpty(fieldNames)
                ? new LinkedList<String>()
                : fieldNames;
        this.readOnly = readOnly;
        this.methodName = methodName;
    }

    public LookupDto(Long id, String lookupName, boolean singleObjectReturn, boolean exposedViaRest,
                     List<Long> fieldList, List<String> fieldNames, boolean readOnly, String methodName) {
        this(lookupName, singleObjectReturn, exposedViaRest, fieldList, fieldNames, readOnly,
                methodName);
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

    public void addField(Long field) {
        this.fieldList.add(field);
    }

    public void addField(Integer field) {
        this.fieldList.add(field.longValue());
    }

    public void insertField(Integer idx, Integer fieldId) {
        insertField(idx, fieldId.longValue());
    }

    public void insertField(Integer idx, Long fieldId) {
        if (idx != null && idx < fieldList.size()) {
            this.fieldList.remove(idx.intValue());
            this.fieldList.add(idx, fieldId);
        }
    }

    public void removeField(Long fieldId) {
        this.fieldList.remove(fieldId);
    }

    public void removeField(Integer fieldId) {
        this.fieldList.remove(fieldId.longValue());
    }


    public List<Long> getFieldList() {
        return fieldList;
    }


    public void setFieldList(List<Long> fieldList) {
        this.fieldList = CollectionUtils.isEmpty(fieldList)
                ? new LinkedList<Long>()
                : fieldList;
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getMethodName() {
        return (StringUtils.isBlank(methodName)) ? LookupName.lookupMethod(lookupName) : methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
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
                Objects.equals(lookupName, other.lookupName) && exposedViaRest == other.exposedViaRest &&
                Objects.equals(methodName, other.methodName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
