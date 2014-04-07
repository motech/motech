package org.motechproject.mds.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.util.LookupName;
import org.motechproject.mds.util.ValidationUtil;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>Lookup</code> class contains information about single lookup
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public class Lookup {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private String lookupName;

    @Persistent
    private boolean singleObjectReturn;

    @Persistent
    private boolean exposedViaRest;

    @Persistent
    private Entity entity;

    @Persistent
    private boolean readOnly;

    @Persistent
    private String methodName;

    @Persistent(table = "LookupFields")
    @Join
    private List<Field> fields;

    public Lookup() {
        this(null, false, false, null);
    }

    public Lookup(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, List<Field> fields) {
        this(lookupName, singleObjectReturn, exposedViaRest, fields, false, LookupName.lookupMethod(lookupName));
    }

    public Lookup(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, List<Field> fields, boolean readOnly,
                  String methodName) {
        setLookupName(lookupName);
        this.singleObjectReturn = singleObjectReturn;
        this.exposedViaRest = exposedViaRest;
        this.fields = fields;
        this.readOnly = readOnly;
        this.methodName = methodName;
    }

    public Lookup(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, List<Field> fields, Entity entity) {
        this(lookupName, singleObjectReturn, exposedViaRest, fields);
        this.entity = entity;
    }

    public Lookup(LookupDto lookupDto, List<Field> lookupFields) {
        update(lookupDto, lookupFields);
    }

    public LookupDto toDto() {
        List<Long> fieldIds = new ArrayList<>();
        List<String> fieldNames = new ArrayList<>();

        if (fields != null) {
            for (Field field : fields) {
                fieldIds.add(field.getId());
                fieldNames.add(field.getName());
            }
        }
        return new LookupDto(id, lookupName, singleObjectReturn, exposedViaRest,
                fieldIds, fieldNames, readOnly, methodName);
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

    public Lookup copy(List<Field> fields) {
        List<Field> lookupFields = new ArrayList<>();
        for (Field field : fields) {
            for (Field lookupField : this.fields) {
                if (lookupField.getName().equals(field.getName())) {
                    lookupFields.add(field);
                }
            }
        }
        return new Lookup(lookupName, singleObjectReturn, exposedViaRest, lookupFields, readOnly, methodName);
    }

    public final void update(LookupDto lookupDto, List<Field> lookupFields) {
        singleObjectReturn = lookupDto.isSingleObjectReturn();
        exposedViaRest = lookupDto.isExposedViaRest();
        setLookupName(lookupDto.getLookupName());
        fields = lookupFields;
        methodName = lookupDto.getMethodName();
        readOnly = lookupDto.isReadOnly();
    }
}
