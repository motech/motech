package org.motechproject.mds.domain;

import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.SettingOptions;
import org.motechproject.mds.util.Pair;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.List;

import static org.motechproject.mds.util.Constants.Util.TRUE;

/**
 * Represents settings of a single field.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = TRUE)
public class FieldSetting implements Pair<String, String> {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private Field field;

    @Persistent(defaultFetchGroup = TRUE)
    @Column(name = "DETAILS_ID")
    private TypeSetting details;

    @Column(jdbcType = "CLOB")
    private String value;

    public FieldSetting() {
        this(null, null, null);
    }

    public FieldSetting(Field field, TypeSetting details) {
        this(field, details, details.getDefaultValue());
    }

    public FieldSetting(Field field, TypeSetting details, String value) {
        this.field = field;
        this.details = details;
        this.value = value;
    }

    public SettingDto toDto() {
        List<SettingOptions> options = new ArrayList<>();
        for (TypeSettingOption option : details.getTypeSettingOptions()) {
            options.add(SettingOptions.valueOf(option.getName()));
        }

        Type valueType = details.getValueType();

        SettingDto dto = new SettingDto();
        dto.setType(valueType.toDto());
        dto.setValue(valueType.parse(value));
        dto.setName(details.getName());
        dto.setOptions(options);

        return dto;
    }

    public FieldSetting copy() {
        FieldSetting copy = new FieldSetting();
        copy.setDetails(details);
        copy.setValue(value);

        return copy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public TypeSetting getDetails() {
        return details;
    }

    public void setDetails(TypeSetting details) {
        this.details = details;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getKey() {
        return getDetails().getName();
    }
}
