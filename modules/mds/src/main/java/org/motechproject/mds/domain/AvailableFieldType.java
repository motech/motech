package org.motechproject.mds.domain;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.dto.TypeDto;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * The <code>FieldTypeMapping</code> class is a representation of database records
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class AvailableFieldType {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent
    private String defaultName;

    @Persistent
    @Unique
    private String displayName;

    @Persistent
    private String description;

    @Persistent
    private String typeClass;

    public AvailableFieldType() {
        this(null, null, null);
    }

    public AvailableFieldType(Long id, String defaultName, TypeDto type) {
        this.id = id;
        this.defaultName = defaultName;
        this.displayName = type == null ? null : type.getDisplayName();
        this.description = type == null ? null : type.getDescription();
        this.typeClass = type == null ? null : type.getTypeClass();
    }

    public AvailableTypeDto toDto() {
        return new AvailableTypeDto(this.getId(), this.getDefaultName(), new TypeDto(displayName, description, typeClass));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public String getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(String typeClass) {
        this.typeClass = typeClass;
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

    @NotPersistent
    public Object parse(String str) {
        if (StringUtils.isBlank(str)) {
            return (String.class.getName().equals(typeClass)) ? "" : null;
        }

        if (DateTime.class.getName().equals(typeClass)) {
            return new DateTime(str);
        } else if (Date.class.getName().equals(typeClass)) {
            return new DateTime(str).toDate();
        }

        try {
            Class<?> clazz = getClass().getClassLoader().loadClass(typeClass);

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

}
