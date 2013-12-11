//CHECKSTYLE:OFF
package org.motechproject.mds.web;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.dto.BrowsingSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.RestOptions;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TrackingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.HistoryRecord;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.parseBoolean;
import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.motechproject.mds.dto.SettingOptions.POSITIVE;
import static org.motechproject.mds.dto.SettingOptions.REQUIRE;
import static org.motechproject.mds.dto.TypeDto.BOOLEAN;
import static org.motechproject.mds.dto.TypeDto.DATE;
import static org.motechproject.mds.dto.TypeDto.DATETIME;
import static org.motechproject.mds.dto.TypeDto.DOUBLE;
import static org.motechproject.mds.dto.TypeDto.INTEGER;
import static org.motechproject.mds.dto.TypeDto.LIST;
import static org.motechproject.mds.dto.TypeDto.STRING;
import static org.motechproject.mds.dto.TypeDto.TIME;

/**
 * The <code>ExampleData</code> is a temporary class which contains example data for UI.
 * In the future this class should be removed and data should come from database.
 *
 * @see EntityDto
 * @see FieldDto
 * @see AvailableTypeDto
 */
@SuppressWarnings("PMD")
public class ExampleData {
    private List<EntityDto> entities = new ArrayList<>();
    private List<FieldDto> fields = new ArrayList<>();
    private List<FieldInstanceDto> instanceFields = new ArrayList<>();
    private List<AvailableTypeDto> types = new ArrayList<>();
    private List<AdvancedSettingsDto> advancedSettings = new ArrayList<>();
    private List<EntityRecord> entityRecords = new ArrayList<>();
    private List<HistoryRecord> entityHistory = new ArrayList<>();

    private Map<TypeDto, List<SettingDto>> typeSettings = new HashMap<>();
    private Map<TypeDto, FieldValidationDto> typeValidation = new HashMap<>();

    private Map<String, AdvancedSettingsDto> advancedHistroy = new HashMap<>();
    private Map<String, Map<String, FieldDto>> fieldsHistory = new HashMap<>();

    private ObjectMapper mapper = new ObjectMapper();

    public ExampleData() {
        addType(new AvailableTypeDto("1", "int", INTEGER), FieldValidationDto.INTEGER, null);
        addType(new AvailableTypeDto("2", "str", STRING), FieldValidationDto.STRING, null);
        addType(new AvailableTypeDto("3", "bool", BOOLEAN), null);
        addType(new AvailableTypeDto("4", "date", DATE), null);
        addType(new AvailableTypeDto("5", "time", TIME), null);
        addType(new AvailableTypeDto("6", "datetime", DATETIME), null);
        addType(
                new AvailableTypeDto("7", "decimal", DOUBLE),
                FieldValidationDto.DOUBLE,
                new SettingDto("mds.form.label.precision", 9, INTEGER, REQUIRE, POSITIVE),
                new SettingDto("mds.form.label.scale", 2, INTEGER, REQUIRE, POSITIVE)
        );
        addType(
                new AvailableTypeDto("8", "list", LIST),
                null,
                new SettingDto("mds.form.label.values", new LinkedList<>(), LIST, REQUIRE),
                new SettingDto("mds.form.label.allowUserSupplied", false, BOOLEAN),
                new SettingDto("mds.form.label.allowMultipleSelections", false, BOOLEAN)
        );

        entities.add(new EntityDto("1", "Patient", "OpenMRS", "navio"));

        entities.add(new EntityDto("2", "Person", "OpenMRS", "navio"));

        entities.add(new EntityDto("3", "Patient", "OpenMRS", "accra"));

        entities.add(new EntityDto("4", "Person", "OpenMRS", "accra"));

        entities.add(new EntityDto("5", "Appointments", "Appointments"));

        List<MetadataDto> exampleMetadata1 = new LinkedList<>();
        exampleMetadata1.add(new MetadataDto("key1", "value1"));
        exampleMetadata1.add(new MetadataDto("key2", "value2"));

        fields.add(
                new FieldDto(
                        "1", "5", STRING,
                        new FieldBasicDto("ID", "ID", false, "pass", null),
                        exampleMetadata1, FieldValidationDto.STRING, null
                )
        );

        entities.add(new EntityDto("6", "Call Log Item", "IVR"));

        entities.add(new EntityDto("7", "Voucher"));
        fields.add(
                new FieldDto(
                        "2", "7", STRING,
                        new FieldBasicDto("ID", "ID", false, "pass", null),
                        null,
                        FieldValidationDto.STRING, null
                )
        );
        fields.add(
                new FieldDto(
                        "3", "7", LIST,
                        new FieldBasicDto("Drug Regimen", "regimen"),
                        null, null,
                        Arrays.asList(new SettingDto("mds.form.label.values", Arrays.asList("Peldi", "Golden", "Patata"),
                                LIST, REQUIRE), new SettingDto("mds.form.label.allowUserSupplied", false, null),
                                new SettingDto("mds.form.label.allowMultipleSelections", false, null))
                )
        );

        List<MetadataDto> exampleMetadata2 = new LinkedList<>();
        exampleMetadata2.add(new MetadataDto("key1", "value1"));
        exampleMetadata2.add(new MetadataDto("key2", "value2"));
        fields.add(
                new FieldDto(
                        "4", "7", INTEGER,
                        new FieldBasicDto("Voucher Number", "voucherNumber"),
                        exampleMetadata2,
                        FieldValidationDto.INTEGER, null
                )
        );

        List<MetadataDto> exampleMetadata3 = new LinkedList<>();
        exampleMetadata3.add(new MetadataDto("key3", "value3"));
        fields.add(
                new FieldDto(
                        "5", "7", STRING,
                        new FieldBasicDto("Redeemed By", "redeemedBy"),
                        exampleMetadata3,
                        FieldValidationDto.STRING, null
                )
        );

        fields.add(
                new FieldDto(
                        "6", "7", BOOLEAN,
                        new FieldBasicDto("Active", "active"),
                        exampleMetadata3,
                        null, null
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
        exampleAdvancedSetting.setId(String.valueOf(advancedSettings.size() + 1));
        exampleAdvancedSetting.setEntityId("7");
        exampleAdvancedSetting.setRestOptions(exampleRestOptions);
        advancedSettings.add(exampleAdvancedSetting);

        instanceFields.add(new FieldInstanceDto("1", "1", new FieldBasicDto("Date", "date")));
        instanceFields.add(new FieldInstanceDto("2", "1", new FieldBasicDto("User", "user")));
        instanceFields.add(new FieldInstanceDto("3", "1", new FieldBasicDto("Action", "action")));
        instanceFields.add(new FieldInstanceDto("4", "1", new FieldBasicDto("Changes", "changes")));

        entityRecords = createEntityRecords();
        entityHistory = createEntityHistoryRecords();
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

        if (fieldsHistory.containsKey(entityId)) {
            for (Map.Entry<String, FieldDto> entry : fieldsHistory.get(entityId).entrySet()) {
                boolean found = false;

                for (int i = list.size() - 1; i >= 0; --i) {
                    FieldDto field = list.get(i);

                    if (equalsIgnoreCase(field.getId(), entry.getKey())) {
                        list.remove(i);

                        if (null != entry.getValue()) {
                            list.add(i, entry.getValue());
                        }

                        found = true;
                        break;
                    }
                }

                if (!found && null != entry.getValue()) {
                    list.add(entry.getValue());
                }
            }
        }

        Collections.sort(list, new Comparator<FieldDto>() {
            @Override
            public int compare(FieldDto one, FieldDto two) {
                return CASE_INSENSITIVE_ORDER.compare(one.getId(), two.getId());
            }
        });

        return list;
    }

    public List<FieldInstanceDto> getInstanceFields(String instanceId) {
        List<FieldInstanceDto> list = new ArrayList<>();

        for (FieldInstanceDto field : instanceFields) {
            if (equalsIgnoreCase(field.getInstanceId(), instanceId)) {
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

        if (null != found && fieldsHistory.containsKey(found.getEntityId())
                && fieldsHistory.get(found.getEntityId()).containsKey(id)) {
            found = fieldsHistory.get(found.getEntityId()).get(id);
        }

        return found;
    }

    public FieldDto findFieldByName(String entityId, String name) {
        List<FieldDto> fields = getFields(entityId);
        FieldDto found = null;

        for (FieldDto field : fields) {
            if (equalsIgnoreCase(field.getBasic().getName(), name)) {
                found = field;
                break;
            }
        }

        if (null != found && fieldsHistory.containsKey(found.getEntityId())
                && fieldsHistory.get(found.getEntityId()).containsKey(found.getId())) {
            found = fieldsHistory.get(found.getEntityId()).get(found.getId());
        }

        return found;
    }

    public void removeField(String id) {
        for (int i = fields.size() - 1; i >= 0; i--) {
            if (equalsIgnoreCase(fields.get(i).getId(), id)) {
                fields.remove(i);
                break;
            }
        }
    }

    public List<AvailableTypeDto> getTypes() {
        return new ArrayList<>(types);
    }

    public void addOrUpdateField(FieldDto field) {
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
        AdvancedSettingsDto found = clone(AdvancedSettingsDto.class, getPurgeAdvanced(entityId));

        if (advancedHistroy.containsKey(entityId)) {
            AdvancedSettingsDto temporary = advancedHistroy.get(entityId);

            found.setId(temporary.getId());
            found.setEntityId(temporary.getEntityId());
            found.setTracking(temporary.getTracking());
            found.setIndexes(temporary.getIndexes());
            found.setRestOptions(temporary.getRestOptions());
            found.setBrowsing(temporary.getBrowsing());
        }

        return found;
    }

    private AdvancedSettingsDto getPurgeAdvanced(String entityId) {
        AdvancedSettingsDto found = null;

        for (AdvancedSettingsDto item : advancedSettings) {
            if (equalsIgnoreCase(item.getEntityId(), entityId)) {
                found = item;
                break;
            }
        }

        if (null == found) {
            found = new AdvancedSettingsDto();
            found.setId(String.valueOf(advancedSettings.size() + 1));
            found.setEntityId(entityId);

            advancedSettings.add(found);
        }

        return found;
    }

    public AvailableTypeDto getAvailableType(String typeClass) {
        AvailableTypeDto found = null;

        for (AvailableTypeDto item : types) {
            if (equalsIgnoreCase(item.getType().getTypeClass(), typeClass)) {
                found = item;
                break;
            }
        }

        return found;
    }

    public void draft(String entityId, DraftData data) {
        if (data.isCreate()) {
            draftCreate(entityId, data);
        } else if (data.isEdit()) {
            draftEdit(entityId, data);
        } else if (data.isRemove()) {
            draftRemove(entityId, data);
        }
    }

    private void draftCreate(String entityId, DraftData data) {
        if (!fieldsHistory.containsKey(entityId)) {
            fieldsHistory.put(entityId, new HashMap<String, FieldDto>());
        }

        Map<String, FieldDto> map = fieldsHistory.get(entityId);

        String typeClass = data.getValue(DraftData.TYPE_CLASS).toString();
        String displayName = data.getValue(DraftData.DISPLAY_NAME).toString();
        String name = data.getValue(DraftData.NAME).toString();
        String fieldId = String.valueOf(getFields().size() + 1);

        FieldBasicDto basic = new FieldBasicDto();
        basic.setName(name);
        basic.setDisplayName(displayName);

        AvailableTypeDto availableType = getAvailableType(typeClass);
        TypeDto fieldType = availableType.getType();
        List<SettingDto> fieldSettings = typeSettings.get(fieldType);
        FieldValidationDto fieldValidation = typeValidation.get(fieldType);

        FieldDto field = new FieldDto();
        field.setId(fieldId);
        field.setEntityId(entityId);
        field.setBasic(basic);
        field.setType(fieldType);
        field.setValidation(fieldValidation);
        field.setSettings(fieldSettings);

        map.put(fieldId, field);
    }

    private void draftEdit(String entityId, DraftData data) {
        Object advancedValue = data.getValues().get(DraftData.ADVANCED);
        boolean editAdvanced = null != advancedValue && parseBoolean(advancedValue.toString());
        String[] path = data.getValue(DraftData.PATH).toString().split("\\.");
        List value = (List) data.getValue(DraftData.VALUE);
        Object start;

        if (editAdvanced) {
            if (!advancedHistroy.containsKey(entityId)) {
                advancedHistroy.put(
                        entityId, clone(AdvancedSettingsDto.class, getPurgeAdvanced(entityId))
                );
            }

            start = advancedHistroy.get(entityId);
        } else {
            if (!fieldsHistory.containsKey(entityId)) {
                fieldsHistory.put(entityId, new HashMap<String, FieldDto>());
            }

            Map<String, FieldDto> map = fieldsHistory.get(entityId);
            String fieldId = data.getValue(DraftData.FIELD_ID).toString();

            if (!map.containsKey(fieldId)) {
                FieldDto field = getField(fieldId);
                map.put(fieldId, clone(FieldDto.class, field));
            }

            start = map.get(fieldId);
        }

        Object field = findField(path, start);
        setField(field, path[path.length - 1], value);
    }

    private void draftRemove(String entityId, DraftData data) {
        if (!fieldsHistory.containsKey(entityId)) {
            fieldsHistory.put(entityId, new HashMap<String, FieldDto>());
        }

        Map<String, FieldDto> map = fieldsHistory.get(entityId);

        String fieldId = data.getValue(DraftData.FIELD_ID).toString();
        map.put(fieldId, null);
    }

    private Object findField(String[] path, Object start) {
        Object current = start;

        for (int i = 0; i < path.length - 1; ++i) {
            String property = path[i];

            if (current == null) {
                throw new IllegalStateException("Field on path is null");
            } else if (current instanceof List) {
                int idx = Integer.parseInt(property);
                current = ((List) current).get(idx);
            } else if (current instanceof Map) {
                current = ((Map) current).get(property);
            } else {
                try {
                    current = PropertyUtils.getProperty(current, property);
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        return current;
    }

    private void setField(Object current, String property, List value) {
        if (property.startsWith("$")) {
            String methodName = property.substring(1);

            try {
                Class<?> clazz = current.getClass();

                if (value == null) {
                    Method method = clazz.getMethod(methodName);
                    method.invoke(current);
                } else {
                    Class[] clazzes = new Class[value.size()];
                    for (int i = 0; i < value.size(); ++i) {
                        Object item = value.get(i);
                        clazzes[i] = item instanceof List ? List.class : item.getClass();
                    }

                    Method method = clazz.getMethod(methodName, clazzes);
                    method.invoke(current, value.toArray(new Object[value.size()]));
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        } else {
            try {
                PropertyUtils.setProperty(current, property, value.get(0));
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public void commitChanges(String entityId) {
        if (fieldsHistory.containsKey(entityId)) {
            for (Map.Entry<String, FieldDto> entry : fieldsHistory.get(entityId).entrySet()) {
                FieldDto field = entry.getValue();
                boolean found = false;

                for (int i = fields.size() - 1; i >= 0; --i) {
                    if (equalsIgnoreCase(fields.get(i).getId(), entry.getKey())) {
                        if (null == field) {
                            fields.remove(i);
                        } else {
                            fields.set(i, field);
                        }

                        found = true;
                        break;
                    }
                }

                if (!found && null != field) {
                    fields.add(field);
                }
            }
        }

        if (advancedHistroy.containsKey(entityId)) {
            AdvancedSettingsDto current = getPurgeAdvanced(entityId);
            AdvancedSettingsDto temporary = advancedHistroy.get(entityId);

            current.setId(temporary.getId());
            current.setEntityId(temporary.getEntityId());
            current.setTracking(clone(TrackingDto.class, temporary.getTracking()));
            current.setIndexes(temporary.getIndexes());
            current.setRestOptions(clone(RestOptions.class, temporary.getRestOptions()));
            current.setBrowsing(clone(BrowsingSettingsDto.class, temporary.getBrowsing()));
        }

        abandonChanges(entityId);
    }

    public void abandonChanges(String entityId) {
        fieldsHistory.remove(entityId);
        advancedHistroy.remove(entityId);
        getEntity(entityId).setDraft(false);
    }

    private <T> T clone(Class<T> clazz, Object obj) {
        try {
            byte[] bytes = mapper.writeValueAsBytes(obj);

            return mapper.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void addType(AvailableTypeDto type, FieldValidationDto validation,
                         SettingDto... settings) {
        types.add(type);
        typeValidation.put(type.getType(), validation);

        if (ArrayUtils.isEmpty(settings)) {
            typeSettings.put(type.getType(), null);
        } else {
            List<SettingDto> list = new LinkedList<>();
            Collections.addAll(list, settings);

            typeSettings.put(type.getType(), list);
        }
    }

    public List<EntityRecord> createEntityRecords() {
        List<EntityRecord> ret = new ArrayList<>();
        List<FieldRecord> fields = new ArrayList<>();

        fields.add(new FieldRecord("ID", "ID", "f1992e633e"));
        fields.add(new FieldRecord("regimen", "Drug Regimen", "Peldi"));
        fields.add(new FieldRecord("voucherNumber", "Voucher Number", "123"));
        fields.add(new FieldRecord("redeemedBy", "Redeemed By", "Person1"));
        fields.add(new FieldRecord("active", "Active", true));
        EntityRecord entityRecord = new EntityRecord("1", "7", fields);
        ret.add(entityRecord);

        fields = new ArrayList<>();
        fields.add(new FieldRecord("ID", "ID", "dd2b824bbb"));
        fields.add(new FieldRecord("regimen", "Drug Regimen", "Golden"));
        fields.add(new FieldRecord("voucherNumber", "Voucher Number", "456"));
        fields.add(new FieldRecord("redeemedBy", "Redeemed By", "Person2"));
        fields.add(new FieldRecord("active", "Active", true));
        entityRecord = new EntityRecord("2", "7", fields);
        ret.add(entityRecord);

        fields = new ArrayList<>();
        fields.add(new FieldRecord("ID", "ID", "d5411b8d8"));
        fields.add(new FieldRecord("regimen", "Drug Regimen", "Patata"));
        fields.add(new FieldRecord("voucherNumber", "Voucher Number", "312"));
        fields.add(new FieldRecord("redeemedBy", "Redeemed By", "Person3"));
        fields.add(new FieldRecord("active", "Active", false));
        entityRecord = new EntityRecord("3", "7", fields);
        ret.add(entityRecord);

        return ret;
    }

    public List<EntityRecord> getEntityRecordsById(String entityId) {
        List<EntityRecord> entityRecordList = new ArrayList<>();
        for (EntityRecord entityRecord : entityRecords) {
            if (entityRecord.getEntitySchemaId().equals(entityId)) {
                entityRecordList.add(entityRecord);
            }
        }

        return entityRecordList;
    }

    public List<HistoryRecord> createEntityHistoryRecords() {
        List<HistoryRecord> ret = new ArrayList<>();

        List<FieldRecord> fields = new ArrayList<>();
        fields.add(new FieldRecord("date", "Date", "April 15, 2012 10:04 AM"));
        fields.add(new FieldRecord("user", "User", "User1"));
        fields.add(new FieldRecord("action", "Action", "CREATE"));
        fields.add(new FieldRecord("changes", "Changes", ""));
        HistoryRecord historyRecord = new HistoryRecord("1", fields);
        ret.add(historyRecord);

        fields = new ArrayList<>();
        fields.add(new FieldRecord("date", "Date", "April 16, 2012 11:04 AM"));
        fields.add(new FieldRecord("user", "User", "User2"));
        fields.add(new FieldRecord("action", "Action", "UPDATE"));
        fields.add(new FieldRecord("changes", "Changes", "Changed state"));
        historyRecord = new HistoryRecord("1", fields);
        ret.add(historyRecord);

        fields = new ArrayList<>();
        fields.add(new FieldRecord("date", "Date", "May 11, 2013 9:32 PM"));
        fields.add(new FieldRecord("user", "User", "User2"));
        fields.add(new FieldRecord("action", "Action", "UPDATE"));
        fields.add(new FieldRecord("changes", "Changes", "Is Active"));
        historyRecord = new HistoryRecord("1", fields);
        ret.add(historyRecord);

        return ret;
    }

    public List<HistoryRecord> getInstanceHistoryRecordsById(String instanceId) {
        List<HistoryRecord> instanceHistoryList = new ArrayList<>();

        for (HistoryRecord historyRecord : entityHistory) {
            if (historyRecord.getId().equals(instanceId)) {
                instanceHistoryList.add(historyRecord);
            }
        }

        return instanceHistoryList;
    }

    private List<FieldDto> getFields() {
        List<FieldDto> list = new ArrayList<>();

        for (EntityDto entity : entities) {
            list.addAll(getFields(entity.getId()));
        }

        return list;
    }
}
//CHECKSTYLE:ON
