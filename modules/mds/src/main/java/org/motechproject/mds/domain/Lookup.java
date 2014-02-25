package org.motechproject.mds.domain;

import org.motechproject.mds.dto.LookupDto;

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

    @Persistent(table = "LookupFields")
    @Join
    private List<Field> fields;

    public Lookup() {
        this(null, false, false, null);
    }

    public Lookup(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, List<Field> fields) {
        this.lookupName = lookupName;
        this.singleObjectReturn = singleObjectReturn;
        this.exposedViaRest = exposedViaRest;
        this.fields = fields;
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
        return new LookupDto(id, lookupName, singleObjectReturn, exposedViaRest, fieldIds, fieldNames);
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

    public Lookup copy(List<Field> fields) {
        List<Field> lookupFields = new ArrayList<>();
        for (Field field : fields) {
            for (Field lookupField : this.fields) {
                if (lookupField.getName().equals(field.getName())) {
                    lookupFields.add(field);
                }
            }
        }
        return new Lookup(lookupName, singleObjectReturn, exposedViaRest, lookupFields);
    }

    public final void update(LookupDto lookupDto, List<Field> lookupFields) {
        singleObjectReturn = lookupDto.isSingleObjectReturn();
        exposedViaRest = lookupDto.isExposedViaRest();
        lookupName = lookupDto.getLookupName();
        fields = lookupFields;
    }
}
