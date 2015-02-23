package org.motechproject.mds.dto;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.motechproject.mds.util.LookupName;

import java.util.Iterator;
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
    private List<LookupFieldDto> lookupFields;
    private boolean readOnly;
    private String methodName;
    private boolean referenced;

    public LookupDto() {
        this(null, false, false);
    }

    public LookupDto(String lookupName, boolean singleObjectReturn, boolean exposedViaRest) {
        this(lookupName, singleObjectReturn, exposedViaRest, null, false);
    }

    public LookupDto(String lookupName, boolean singleObjectReturn, boolean exposedViaRest,
                     List<LookupFieldDto> lookupFields) {
        this(lookupName, singleObjectReturn, exposedViaRest, lookupFields, false);
    }

    public LookupDto(String lookupName, boolean singleObjectReturn, boolean exposedViaRest,
                     List<LookupFieldDto> lookupFields, boolean readOnly) {
        this(lookupName, singleObjectReturn, exposedViaRest, lookupFields, readOnly, null);
    }

    public LookupDto(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, List<LookupFieldDto> lookupFields,
                     boolean readOnly, String methodName) {
        this.lookupName = lookupName;
        this.singleObjectReturn = singleObjectReturn;
        this.exposedViaRest = exposedViaRest;
        this.readOnly = readOnly;
        this.methodName = methodName;
        this.lookupFields = lookupFields;
        this.referenced = false;
    }

    public LookupDto(Long id, String lookupName, boolean singleObjectReturn, boolean exposedViaRest,
                     List<LookupFieldDto> lookupFields, boolean readOnly, String methodName) {
        this(lookupName, singleObjectReturn, exposedViaRest, lookupFields, readOnly,
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
        this.lookupFields.add(new LookupFieldDto(field, null, LookupFieldType.VALUE));
    }

    public void addField(Integer field) {
        addField(field.longValue());
    }

    public void insertField(Integer idx, Integer fieldId) {
        insertField(idx, fieldId.longValue());
    }

    public void insertField(Integer idx, Long fieldId) {
        if (idx != null && idx < lookupFields.size()) {
            this.lookupFields.remove(idx.intValue());
            this.lookupFields.add(idx, new LookupFieldDto(fieldId, null, LookupFieldType.VALUE));
        }
    }

    public void removeField(Long fieldId) {
        Iterator<LookupFieldDto> it = lookupFields.iterator();
        while (it.hasNext()) {
            LookupFieldDto lookupField = it.next();
            if (Objects.equals(fieldId, lookupField.getId())) {
                it.remove();
            }
        }
    }

    public void removeField(Integer fieldId) {
        removeField(fieldId.longValue());
    }

    public final List<LookupFieldDto> getLookupFields() {
        if (lookupFields == null) {
            lookupFields = new LinkedList<>();
        }
        return lookupFields;
    }

    public void setLookupFields(List<LookupFieldDto> lookupFields) {
        this.lookupFields = lookupFields;
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

    public boolean isReferenced() {
        return referenced;
    }

    public void setReferenced(boolean referenced) {
        this.referenced = referenced;
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

        return singleObjectReturn == other.singleObjectReturn && Objects.equals(lookupFields, other.lookupFields) &&
                Objects.equals(lookupName, other.lookupName) && exposedViaRest == other.exposedViaRest &&
                Objects.equals(methodName, other.methodName) && referenced == other.referenced;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
