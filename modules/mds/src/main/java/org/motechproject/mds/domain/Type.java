package org.motechproject.mds.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.dto.TypeDto;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * The <code>Type</code> class contains information about a single type in mds system. The mds
 * type can have a settings and validations that can be assigned to field with the same type.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class Type {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent
    private String displayName;

    @Persistent
    private String description;

    @Persistent
    private Class<?> typeClass;

    @Persistent(table = "TYPE_TYPE_SETTING")
    @Join(column = "TYPE_ID_OID")
    @Element(column = "TYPE_SETTING_ID_EID")
    private List<TypeSetting> settings;

    @Persistent(table = "TYPE_TYPE_VALIDATION")
    @Join(column = "TYPE_ID_OID")
    @Element(column = "TYPE_VALIDATION_ID_EID")
    private List<TypeValidation> validations;

    public TypeDto toDto() {
        return new TypeDto(displayName, description, typeClass.getName());
    }

    @NotPersistent
    public Object parse(String str) {
        String className = typeClass.getName();

        if (StringUtils.isBlank(str)) {
            return (String.class.getName().equals(className)) ? "" : null;
        }

        if (DateTime.class.getName().equals(className)) {
            return new DateTime(str);
        } else if (Date.class.getName().equals(className)) {
            return new DateTime(str).toDate();
        }

        try {
            Class<?> clazz = getClass().getClassLoader().loadClass(className);

            if (clazz.isAssignableFrom(List.class)) {
                List list = new ArrayList();

                list.addAll(Arrays.asList(StringUtils.split(str, '\n')));

                return list;
            } else {
                return MethodUtils.invokeStaticMethod(clazz, "valueOf", str);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse value", e);
        }
    }

    @NotPersistent
    public String format(Object obj) {
        if (obj instanceof List) {
            return StringUtils.join((List) obj, '\n');
        } else if (obj instanceof Time) {
            return ((Time) obj).timeStr();
        } else if (obj instanceof Date) {
            return new DateTime(((Date) obj).getTime()).toString();
        } else {
            return (obj == null) ? "" : obj.toString();
        }
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeClassName() {
        return typeClass.getName();
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public boolean hasSettings() {
        return CollectionUtils.isNotEmpty(settings);
    }

    public List<TypeSetting> getSettings() {
        return settings;
    }

    public void setSettings(List<TypeSetting> settings) {
        this.settings = settings;
    }

    public boolean hasValidation() {
        return CollectionUtils.isNotEmpty(validations);
    }

    public List<TypeValidation> getValidations() {
        return validations;
    }

    public void setValidations(List<TypeValidation> validations) {
        this.validations = validations;
    }
}
