//CHECKSTYLE:OFF
package org.motechproject.mds.web;

import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.RestOptions;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.web.domain.FieldRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.motechproject.mds.dto.SettingOptions.POSITIVE;
import static org.motechproject.mds.dto.SettingOptions.REQUIRE;
import static org.motechproject.mds.dto.TypeDto.INTEGER;
import static org.motechproject.mds.dto.TypeDto.DOUBLE;
import static org.motechproject.mds.dto.TypeDto.STRING;
import static org.motechproject.mds.dto.TypeDto.BOOLEAN;
import static org.motechproject.mds.dto.TypeDto.DATE;
import static org.motechproject.mds.dto.TypeDto.TIME;
import static org.motechproject.mds.dto.TypeDto.DATETIME;
import static org.motechproject.mds.dto.TypeDto.LIST;

/**
 * The <code>ExampleData</code> is a temporary class which contains example data for UI.
 * In the future this class should be removed and data should come from database.
 *
 * @see EntityDto
 * @see FieldDto
 * @see AvailableTypeDto
 */
@SuppressWarnings("PMD")
public final class ExampleData {
    private List<EntityDto> entities = new ArrayList<>();
    private List<FieldDto> fields = new ArrayList<>();
    private List<AvailableTypeDto> types = new ArrayList<>();
    private List<AdvancedSettingsDto> advancedSettings = new ArrayList<>();
    private List<EntityRecord> entityRecords = new ArrayList<>();

    public ExampleData() {
        types.add(new AvailableTypeDto("1", "int", INTEGER));
        types.add(new AvailableTypeDto("2", "str", STRING));
        types.add(new AvailableTypeDto("3", "bool", BOOLEAN));
        types.add(new AvailableTypeDto("4", "date", DATE));
        types.add(new AvailableTypeDto("5", "time", TIME));
        types.add(new AvailableTypeDto("6", "datetime", DATETIME));
        types.add(
                new AvailableTypeDto(
                        "7", "decimal", DOUBLE,
                        new SettingDto("mds.form.label.precision", 9, INTEGER, REQUIRE, POSITIVE),
                        new SettingDto("mds.form.label.scale", 2, INTEGER, REQUIRE, POSITIVE)
                )
        );
        types.add(
                new AvailableTypeDto(
                        "8", "list", LIST,
                        new SettingDto("mds.form.label.values", new LinkedList<>(), LIST, REQUIRE),
                        new SettingDto("mds.form.label.allowUserSupplied", false, BOOLEAN),
                        new SettingDto("mds.form.label.allowMultipleSelections", false, BOOLEAN)
                )
        );

        entities.add(new EntityDto("1", "Patient", "OpenMRS", "navio"));

        entities.add(new EntityDto("2", "Person", "OpenMRS", "navio"));

        entities.add(new EntityDto("3", "Patient", "OpenMRS", "accra"));

        entities.add(new EntityDto("4", "Person", "OpenMRS", "accra"));

        entities.add(new EntityDto("5", "Appointments", "Appointments"));

        Map<String, String> exampleMap1 = new HashMap();
        exampleMap1.put("key1", "value1");
        exampleMap1.put("key2", "value2");
        fields.add(
                new FieldDto(
                        "1", "5", STRING,
                        new FieldBasicDto("ID", "ID", false, "pass", null),
                        exampleMap1, null
                )
        );

        entities.add(new EntityDto("6", "Call Log Item", "IVR"));

        entities.add(new EntityDto("7", "Voucher"));
        fields.add(
                new FieldDto(
                        "2", "7", STRING,
                        new FieldBasicDto("ID", "ID", false, "pass", null),
                        null,
                        FieldValidationDto.STRING
                )
        );
        fields.add(
                new FieldDto(
                        "3", "7", STRING,
                        new FieldBasicDto("Drug Regimen", "regimen"),
                        null,
                        FieldValidationDto.STRING
                )
        );

        Map<String, String> exampleMap2 = new HashMap();
        exampleMap2.put("key1", "value1");
        exampleMap2.put("key2", "value2");
        fields.add(
                new FieldDto(
                        "4", "7", INTEGER,
                        new FieldBasicDto("Voucher Number", "voucherNumber"),
                        exampleMap2,
                        FieldValidationDto.INTEGER
                )
        );

        Map<String, String> exampleMap3 = new HashMap();
        exampleMap3.put("key3", "value3");
        fields.add(
                new FieldDto(
                        "5", "7", STRING,
                        new FieldBasicDto("Redeemed By", "redeemedBy"),
                        exampleMap3,
                        FieldValidationDto.STRING
                )
        );

        entities.add(new EntityDto("8", "Campaign", "Message Campaign"));

        AdvancedSettingsDto exampleAdvancedSetting = new AdvancedSettingsDto();
        RestOptions exampleRestOptions = new RestOptions();
        List<String> fields = new LinkedList<>();
        fields.add("2");
        fields.add("5");
        exampleRestOptions.setCreate(true);
        exampleRestOptions.setFieldIds(fields);
        exampleAdvancedSetting.setObjectId("7");
        exampleAdvancedSetting.setRestOptions(exampleRestOptions);
        advancedSettings.add(exampleAdvancedSetting);

        entityRecords = createEntityRecords();
    }

    public EntityDto getEntity(String id) {
        EntityDto found = null;

        for (EntityDto entity : entities) {
            if (equalsIgnoreCase(entity.getId(), id)) {
                found = entity;
            }
        }

        return found;
    }


    public void addEntity(EntityDto entity) {
        entity.setId(String.valueOf(entities.size() + 1));
        entities.add(entity);
    }

    public void removeEntity(EntityDto entity) {
        entities.remove(entity);
    }

    public boolean hasEntityWithName(String name) {
        EntityDto found = null;

        for (EntityDto entity : entities) {
            if (equalsIgnoreCase(entity.getName(), name)) {
                found = entity;
            }
        }

        return null != found;
    }

    public List<EntityDto> getEntities() {
        return new ArrayList<>(entities);
    }

    public List<FieldDto> getFields(String entityId) {
        List<FieldDto> list = new ArrayList<>();

        for (FieldDto field : fields) {
            if (equalsIgnoreCase(field.getEntityId(), entityId)) {
                list.add(field);
            }
        }

        return list;
    }

    public FieldDto getField(String id) {
        FieldDto found = null;

        for (FieldDto field : fields) {
            if (equalsIgnoreCase(field.getId(), id)) {
                found = field;
            }
        }

        return found;
    }

    public boolean removeField(String id) {
        boolean removed = false;

        for (int i = fields.size() - 1; i >= 0; i--) {
            if (fields.get(i).getId().equalsIgnoreCase(id)) {
                fields.remove(i);
                removed = true;
                break;
            }
        }

        return removed;
    }

    public List<AvailableTypeDto> getTypes() {
        return new ArrayList<>(types);
    }

    public void addField(FieldDto field) {
        FieldDto found = getField(field.getId());

        if (null == found) {
            field.setId(String.valueOf(fields.size() + 1));
            fields.add(field);
        } else {
            found.setBasic(field.getBasic());
            found.setEntityId(field.getEntityId());
            found.setSettings(field.getSettings());
            found.setType(field.getType());
            found.setMetadata(field.getMetadata());
            found.setValidation(field.getValidation());
        }

    }

    public AdvancedSettingsDto getAdvanced(String entityId) {
        AdvancedSettingsDto found = null;

        for (AdvancedSettingsDto item : advancedSettings) {
            if (item.getObjectId().equalsIgnoreCase(entityId)) {
                found = item;
                break;
            }
        }

        return found;
    }

    public void saveAdvanced(AdvancedSettingsDto advanced) {
        AdvancedSettingsDto dto = getAdvanced(advanced.getObjectId());

        if (null == dto) {
            advancedSettings.add(advanced);
        } else {
            dto.setTracking(advanced.getTracking());
            dto.setIndexes(advanced.getIndexes());
            dto.setRestOptions(advanced.getRestOptions());
        }
    }

    public List<EntityRecord> createEntityRecords() {
        List<EntityRecord> ret = new ArrayList<>();
        List<FieldRecord> fields = new ArrayList<>();

        fields.add(new FieldRecord("ID", "f1992e633e"));
        fields.add(new FieldRecord("Drug Regimen", "Peldi"));
        fields.add(new FieldRecord("Voucher Number", "123"));
        fields.add(new FieldRecord("Redeemed By", "Peson1"));
        EntityRecord entityRecord = new EntityRecord("7", fields);
        ret.add(entityRecord);

        fields = new ArrayList<>();
        fields.add(new FieldRecord("ID", "dd2b824bbb"));
        fields.add(new FieldRecord("Drug Regimen", "Golden"));
        fields.add(new FieldRecord("Voucher Number", "456"));
        fields.add(new FieldRecord("Redeemed By", "Peson2"));
        entityRecord = new EntityRecord("7", fields);
        ret.add(entityRecord);

        fields = new ArrayList<>();
        fields.add(new FieldRecord("ID", "d5411b8d8"));
        fields.add(new FieldRecord("Drug Regimen", "Patata"));
        fields.add(new FieldRecord("Voucher Number", "312"));
        fields.add(new FieldRecord("Redeemed By", "Peson3"));
        entityRecord = new EntityRecord("7", fields);
        ret.add(entityRecord);

        return ret;
    }

    public List<EntityRecord> getEntityRecordsById(String entityId) {
        List<EntityRecord> entityRecordList = new ArrayList<>();
        for (EntityRecord entityRecord : entityRecords) {
            if (entityRecord.getId().equals(entityId)) {
                entityRecordList.add(entityRecord);
            }
        }

        return entityRecordList;
    }
}
//CHECKSTYLE:ON
