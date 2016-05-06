package org.motechproject.mds.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.exception.lookup.LookupWrongFieldNameException;
import org.motechproject.mds.util.LookupName;
import org.motechproject.mds.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Key;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Value;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.lang.BooleanUtils.toBoolean;
import static org.motechproject.mds.util.Constants.Util.TRUE;

/**
 * The <code>Lookup</code> class contains information about single lookup
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = TRUE)
public class Lookup {

    private static final Logger LOGGER = LoggerFactory.getLogger(Lookup.class);

    private static final String LOOKUP_ID = "id_OID";
    private static final String FIELD_NAME = "fieldName";

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
    private Long id;

    @Persistent
    private String lookupName;

    @Persistent
    private boolean singleObjectReturn;

    @Persistent
    private boolean exposedViaRest;

    @Persistent
    private boolean indexRequired;

    @Persistent
    private Entity entity;

    @Persistent
    private boolean readOnly;

    @Persistent
    private String methodName;

    @Persistent(table = "LookupFields")
    @Join
    private List<Field> fields;

    @Join(table = "Lookup_fieldsOrder", column = LOOKUP_ID)
    @Element(column = FIELD_NAME)
    private List<String> fieldsOrder;

    @Join(table = "Lookup_rangeLookupFields", column = LOOKUP_ID)
    @Element(column = FIELD_NAME)
    private List<String> rangeLookupFields;

    @Join(table = "Lookup_setLookupFields", column = LOOKUP_ID)
    @Element(column = FIELD_NAME)
    private List<String> setLookupFields;

    @Join(table = "Lookup_customOperators", column = LOOKUP_ID)
    @Key(column = FIELD_NAME)
    @Value(column = "operator")
    private Map<String, String> customOperators;

    @Join(table = "Lookup_userGenericParams", column = LOOKUP_ID)
    @Key(column = "param")
    @Value(column = "value")
    private Map<String, Boolean> useGenericParams;

    public Lookup() {
        this(null, false, false, null);
    }

    public Lookup(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, List<Field> fields) {
        this(lookupName, singleObjectReturn, exposedViaRest, fields, false, LookupName.lookupMethod(lookupName));
    }

    public Lookup(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, List<Field> fields, boolean readOnly,
                  String methodName) {
        this(lookupName, singleObjectReturn, exposedViaRest, fields, readOnly, methodName, Collections.<String>emptyList(),
                Collections.<String>emptyList(), new HashMap<>(), new HashMap<>(), new ArrayList<>());
    }

    public Lookup(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, List<Field> fields, boolean readOnly,
                  String methodName, List<String> rangeLookupFields, List<String> setLookupFields,
                  Map<String, String> customOperators) {
        this(lookupName, singleObjectReturn, exposedViaRest, fields, readOnly, methodName, rangeLookupFields,
                setLookupFields, customOperators, new HashMap<>(), new ArrayList<>());
    }

    public Lookup(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, List<Field> fields, boolean readOnly,
                  String methodName, List<String> rangeLookupFields, List<String> setLookupFields,
                  Map<String, String> customOperators, Map<String, Boolean> useGenericParams, List<String> fieldsOrder) {
        setLookupName(lookupName);
        this.singleObjectReturn = singleObjectReturn;
        this.exposedViaRest = exposedViaRest;
        this.fields = fields;
        this.readOnly = readOnly;
        this.methodName = methodName;
        this.rangeLookupFields = rangeLookupFields;
        this.setLookupFields = setLookupFields;
        this.customOperators = customOperators;
        this.useGenericParams = useGenericParams;
        this.fieldsOrder = fieldsOrder;
    }

    public Lookup(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, List<Field> fields, boolean readOnly,
                  String methodName, List<String> rangeLookupFields, List<String> setLookupFields,
                  Map<String, String> customOperators, Map<String, Boolean> useGenericParams, List<String> fieldsOrder, boolean indexRequired) {
        this(lookupName, singleObjectReturn, exposedViaRest, fields, readOnly, methodName, rangeLookupFields, setLookupFields, customOperators,
                useGenericParams, fieldsOrder);
        this.indexRequired = indexRequired;

    }

    public Lookup(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, List<Field> fields, Entity entity) {
        this(lookupName, singleObjectReturn, exposedViaRest, fields);
        this.entity = entity;
    }

    public Lookup(LookupDto lookupDto, List<Field> lookupFields) {
        update(lookupDto, lookupFields);
    }

    public LookupDto toDto() {
        List<LookupFieldDto> lookupFields = new ArrayList<>();

        for (String lookupFieldName : getFieldsOrder()) {
            Field field = getLookupFieldByName(LookupName.getFieldName(lookupFieldName));
            LookupFieldType lookupFieldType = LookupFieldType.VALUE;

            if (isRangeParam(lookupFieldName)) {
                lookupFieldType = LookupFieldType.RANGE;
            } else if (isSetParam(lookupFieldName)) {
                lookupFieldType = LookupFieldType.SET;
            }

            String customOperator = getCustomOperators().get(lookupFieldName);
            boolean useGenericParam = toBoolean(getUseGenericParams().get(lookupFieldName));
            if (field != null) {
                LookupFieldDto lookupField = new LookupFieldDto(field.getId(), field.getName(), lookupFieldType, customOperator,
                        useGenericParam, LookupName.getRelatedFieldName(lookupFieldName));
                lookupFields.add(lookupField);
            } else {
                throw new LookupWrongFieldNameException(String.format("Can't create LookupFieldDto. Field with given name %s does not exist in %s lookup", lookupFieldName, lookupName));
            }
        }

        return new LookupDto(id, lookupName, singleObjectReturn, exposedViaRest,
                lookupFields, readOnly, methodName, fieldsOrder, indexRequired);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLookupName() {
        return lookupName;
    }

    public final void setLookupName(String lookupName) {
        ValidationUtil.validateNoJavaKeyword(lookupName);
        this.lookupName = lookupName;
    }

    public Map<String, String> getCustomOperators() {
        if (customOperators == null) {
            customOperators = new HashMap<>();
        }
        return customOperators;
    }

    public void setCustomOperators(Map<String, String> customOperators) {
        this.customOperators = customOperators;
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

    public void setExposedViaRest(boolean exposedViaRest) {
        this.exposedViaRest = exposedViaRest;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
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

    public final List<String> getRangeLookupFields() {
        if (rangeLookupFields == null) {
            rangeLookupFields = new ArrayList<>();
        }
        return rangeLookupFields;
    }

    public void setRangeLookupFields(List<String> rangeLookupFields) {
        this.rangeLookupFields = rangeLookupFields;
    }

    public final List<String> getSetLookupFields() {
        if (setLookupFields == null) {
            setLookupFields = new ArrayList<>();
        }
        return setLookupFields;
    }

    public void setSetLookupFields(List<String> setLookupFields) {
        this.setLookupFields = setLookupFields;
    }

    public final Field getLookupFieldByName(String name) {
        for (Field field : getFields()) {
            if (StringUtils.equals(name, field.getName())) {
                return field;
            }
        }
        LOGGER.warn("Can't get field with name {}", name);
        return null;
    }

    public final Field getLookupFieldById(Long id) {
        for (Field field : getFields()) {
            if (Objects.equals(field.getId(), id)) {
                return field;
            }
        }
        return null;
    }

    public Map<String, Boolean> getUseGenericParams() {
        if (useGenericParams == null) {
            useGenericParams = new HashMap<>();
        }

        return useGenericParams;
    }

    public void setUseGenericParams(Map<String, Boolean> useGenericParams) {
        this.useGenericParams = useGenericParams;
    }

    public List<String> getFieldsOrder() {
        if (fieldsOrder == null) {
            return new ArrayList<>();
        }
        return fieldsOrder;
    }

    public void setFieldsOrder(List<String> fieldsOrder) {
        this.fieldsOrder = fieldsOrder;
    }

    public Lookup copy(List<Field> fields) {
        List<Field> lookupFields = new ArrayList<>();
        for (Field lookupField : this.fields) {
            for (Field newField : fields) {
                if (lookupField.getName().equals(newField.getName())) {
                    lookupFields.add(newField);
                    break;
                }
            }
        }

        List<String> fieldsOrderCopy = new ArrayList<>(getFieldsOrder());
        List<String> rangeLookupFieldsCopy = new ArrayList<>(getRangeLookupFields());
        List<String> setLookupFieldsCopy = new ArrayList<>(getSetLookupFields());
        Map<String, String> customOperatorsCopy = new HashMap<>(getCustomOperators());
        Map<String, Boolean> useGenericParamsCopy = new HashMap<>(getUseGenericParams());

        return new Lookup(lookupName, singleObjectReturn, exposedViaRest, lookupFields, readOnly, methodName,
                rangeLookupFieldsCopy, setLookupFieldsCopy, customOperatorsCopy, useGenericParamsCopy, fieldsOrderCopy);
    }

    public final void update(LookupDto lookupDto, List<Field> lookupFields) {
        singleObjectReturn = lookupDto.isSingleObjectReturn();
        exposedViaRest = lookupDto.isExposedViaRest();
        setLookupName(lookupDto.getLookupName());
        fields = lookupFields;
        methodName = lookupDto.getMethodName();
        readOnly = lookupDto.isReadOnly();
        indexRequired = lookupDto.isIndexRequired();

        updateFieldsOrder(lookupDto.getFieldsOrder());
        updateCustomOperators(lookupDto);
        updateLookupFields(lookupDto);
        updateUseGenericParams(lookupDto);
    }

    public boolean isRangeParam(String field) {
        return getRangeLookupFields().contains(field);
    }

    public boolean isSetParam(String field) {
        return getSetLookupFields().contains(field);
    }

    private void updateFieldsOrder(List<String> newOrder) {
        if (fieldsOrder == null) {
            fieldsOrder = new ArrayList<>();
        } else {
            fieldsOrder.clear();
        }
        fieldsOrder.addAll(newOrder);
    }

    private void updateLookupFields(LookupDto lookupDto) {
        getRangeLookupFields().clear();
        getSetLookupFields().clear();
        for (LookupFieldDto lookupFieldDto : lookupDto.getLookupFields()) {
            String name = lookupFieldDto.getLookupFieldName();

            if (lookupFieldDto.getType() == LookupFieldType.RANGE) {
                getRangeLookupFields().add(name);
            } else if (lookupFieldDto.getType() == LookupFieldType.SET) {
                getSetLookupFields().add(name);
            }
        }
    }

    private void updateCustomOperators(LookupDto lookupDto) {
        getCustomOperators().clear();
        for (LookupFieldDto lookupField : lookupDto.getLookupFields()) {
            String customOperator = lookupField.getCustomOperator();
            if (StringUtils.isNotBlank(customOperator)) {
                getCustomOperators().put(lookupField.getLookupFieldName(), customOperator);
            }
        }
    }

    private void updateUseGenericParams(LookupDto lookupDto) {
        getUseGenericParams().clear();

        for (LookupFieldDto lookupField : lookupDto.getLookupFields()) {
            getUseGenericParams().put(lookupField.getLookupFieldName(), lookupField.isUseGenericParam());
        }
    }

    public LookupFieldType getLookupFieldType(String fieldName) {
        if (getRangeLookupFields().contains(fieldName)) {
            return LookupFieldType.RANGE;
        } else if (getSetLookupFields().contains(fieldName)) {
            return LookupFieldType.SET;
        } else {
            return LookupFieldType.VALUE;
        }
    }

    public boolean isIndexRequired() {
        return indexRequired;
    }

    public void setIndexRequired(boolean indexRequired) {
        this.indexRequired = indexRequired;
    }
}
