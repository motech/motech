package org.motechproject.mds.dto;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.mds.util.LookupName;

import java.util.ArrayList;
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
    private List<String> fieldsOrder;
    private boolean indexRequired = true;

    public LookupDto() {
        this(null, false, false);
    }

    public LookupDto(String lookupName, boolean singleObjectReturn, boolean exposedViaRest) {
        this(lookupName, singleObjectReturn, exposedViaRest, null, false);
    }

    public LookupDto(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, boolean indexRequired) {
        this(lookupName, singleObjectReturn, exposedViaRest, null, false);
        this.indexRequired = indexRequired;
    }

    public LookupDto(String lookupName, boolean singleObjectReturn, boolean exposedViaRest,
                     List<LookupFieldDto> lookupFields) {
        this(lookupName, singleObjectReturn, exposedViaRest, lookupFields, false);
    }

    public LookupDto(String lookupName, boolean singleObjectReturn, boolean exposedViaRest,
                     List<LookupFieldDto> lookupFields, boolean readOnly) {
        this(lookupName, singleObjectReturn, exposedViaRest, lookupFields, readOnly, null, new ArrayList<>());
    }

    public LookupDto(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, List<LookupFieldDto> lookupFields,
                     boolean readOnly, String methodName, List<String> fieldsOrder) {
        this.lookupName = lookupName;
        this.singleObjectReturn = singleObjectReturn;
        this.exposedViaRest = exposedViaRest;
        this.readOnly = readOnly;
        this.methodName = methodName;
        this.lookupFields = lookupFields;
        this.referenced = false;
        this.fieldsOrder = fieldsOrder;
    }

    public LookupDto(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, List<LookupFieldDto> lookupFields,
                     boolean readOnly, String methodName, List<String> fieldsOrder, boolean indexRequired) {
        this(lookupName, singleObjectReturn, exposedViaRest, lookupFields, readOnly, methodName, fieldsOrder);
        this.indexRequired = indexRequired;
    }

    public LookupDto(Long id, String lookupName, boolean singleObjectReturn, boolean exposedViaRest,
                     List<LookupFieldDto> lookupFields, boolean readOnly, String methodName, List<String> fieldsOrder) {
        this(lookupName, singleObjectReturn, exposedViaRest, lookupFields, readOnly,
                methodName, fieldsOrder);
        this.id = id;
    }

    public LookupDto(Long id, String lookupName, boolean singleObjectReturn, boolean exposedViaRest,
                     List<LookupFieldDto> lookupFields, boolean readOnly, String methodName, List<String> fieldsOrder, boolean indexRequired) {
        this(id, lookupName, singleObjectReturn, exposedViaRest, lookupFields, readOnly,
                methodName, fieldsOrder);
        this.indexRequired = indexRequired;
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

    public void insertField(Integer idx, Integer fieldId, String relatedFieldName) {
        insertField(idx, fieldId.longValue(), relatedFieldName);
    }

    public void insertField(Integer idx, Long fieldId, String relatedFieldName) {
        insertField(idx, fieldId, LookupFieldType.VALUE.name(), relatedFieldName);
    }

    public void insertField(Integer idx, Integer fieldId, String lookupFieldType, String relatedFieldName) {
        insertField(idx, Long.valueOf(fieldId), lookupFieldType, relatedFieldName);
    }

    public void insertField(Integer idx, Long fieldId, String lookupFieldType, String relatedFieldName) {
        if (idx != null && idx < lookupFields.size()) {
            this.lookupFields.remove(idx.intValue());
            LookupFieldDto lokLookupFieldDto = new LookupFieldDto(fieldId, null, LookupFieldType.valueOf(lookupFieldType));
            lokLookupFieldDto.setRelatedName(relatedFieldName);
            this.lookupFields.add(idx, lokLookupFieldDto);
        }
    }

    public void updateTypeForLookupField(Integer idx, String lookupFieldType) {
        if (idx != null && idx < lookupFields.size()) {
            this.lookupFields.get(idx.intValue()).setType(LookupFieldType.valueOf(lookupFieldType));
        }
    }

    public void updateCustomOperatorForLookupField(Integer idx, String customOperator) {
        if (idx != null && idx < lookupFields.size()) {
            this.lookupFields.get(idx.intValue()).setCustomOperator(customOperator);
        }
    }

    public void updateFieldRelatedName(Integer idx, String relatedName) {
        if (idx != null && idx < lookupFields.size()) {
            this.lookupFields.get(idx.intValue()).setRelatedName(relatedName);
        }
    }

    public void removeField(String name) {
        Iterator<LookupFieldDto> it = lookupFields.iterator();
        while (it.hasNext()) {
            LookupFieldDto lookupField = it.next();
            if (Objects.equals(name, lookupField.getLookupFieldName())) {
                it.remove();
            }
        }
    }

    public void removeField(Integer idx) {
        String fieldName = fieldsOrder.get(idx);
        fieldsOrder.remove(idx.intValue());
        removeField(fieldName);
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


    public List<String> getFieldsOrder() {
        return fieldsOrder;
    }

    public void setFieldsOrder(List<String> fieldsOrder) {
        this.fieldsOrder = fieldsOrder;
    }




    @JsonIgnore
    public LookupFieldDto getLookupField(String fieldName) {
        for (LookupFieldDto lookupField : lookupFields) {
            if (StringUtils.equals(fieldName, lookupField.getName())) {
                return lookupField;
            }
        }
        return null;
    }


    public boolean isIndexRequired() {
        return indexRequired;
    }

    public void setIndexRequired(boolean indexRequired) {
        this.indexRequired = indexRequired;
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
                Objects.equals(methodName, other.methodName) && referenced == other.referenced &&
                indexRequired == other.indexRequired;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
