//CHECKSTYLE:OFF
package org.motechproject.mds.web;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.mds.dto.AccessOptions;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.BrowsingSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.SecuritySettingsDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TrackingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.dto.ValidationCriterionDto;
import org.motechproject.mds.util.FieldHelper;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.PreviousRecord;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.parseBoolean;
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
 * @see TypeDto
 */
@SuppressWarnings("PMD")
public class ExampleData {
    private List<FieldDto> fields = new ArrayList<>();
    private List<FieldDto> draftFields = new ArrayList<>();
    private List<FieldInstanceDto> instanceFields = new ArrayList<>();
    private List<TypeDto> types = new ArrayList<>();
    private List<AdvancedSettingsDto> advancedSettings = new ArrayList<>();
    private List<SecuritySettingsDto> securitySettings = new ArrayList<>();
    private List<EntityRecord> entityRecords = new ArrayList<>();
    private List<HistoryRecord> entityHistory = new ArrayList<>();
    private List<PreviousRecord> entityRecordsHistory = new ArrayList<>();

    private Map<TypeDto, List<SettingDto>> typeSettings = new HashMap<>();
    private Map<TypeDto, FieldValidationDto> typeValidation = new HashMap<>();

    private Map<Long, AdvancedSettingsDto> advancedHistory = new HashMap<>();
    private Map<Long, SecuritySettingsDto> securityHistory = new HashMap<>();
    private Map<Long, Map<Long, FieldDto>> fieldsHistory = new HashMap<>();

    private ObjectMapper mapper = new ObjectMapper();

    public ExampleData() {
        addType(INTEGER, FieldValidationDto.INTEGER);
        addType(STRING, FieldValidationDto.STRING);
        addType(BOOLEAN, null);
        addType(DATE, null);
        addType(TIME, null);
        addType(DATETIME, null);
        addType(
                DOUBLE,
                FieldValidationDto.DOUBLE,
                new SettingDto("mds.form.label.precision", 9, INTEGER, REQUIRE, POSITIVE),
                new SettingDto("mds.form.label.scale", 2, INTEGER, REQUIRE, POSITIVE)
        );
        addType(
                LIST,
                null,
                new SettingDto("mds.form.label.values", new LinkedList<>(), LIST, REQUIRE),
                new SettingDto("mds.form.label.allowUserSupplied", false, BOOLEAN),
                new SettingDto("mds.form.label.allowMultipleSelections", false, BOOLEAN)
        );

        List<MetadataDto> exampleMetadata1 = new LinkedList<>();
        exampleMetadata1.add(new MetadataDto("key1", "value1"));
        exampleMetadata1.add(new MetadataDto("key2", "value2"));

        fields.add(
                new FieldDto(
                        1L, 9005L, STRING,
                        new FieldBasicDto("ID", "ID", false, "pass", null),
                        false, exampleMetadata1, FieldValidationDto.STRING, null, null
                )
        );

        fields.add(
                new FieldDto(
                        2L, 9007L, STRING,
                        new FieldBasicDto("ID", "ID", false, "pass", null),
                        false, null,
                        FieldValidationDto.STRING, null, null
                )
        );
        fields.add(
                new FieldDto(
                        3L, 9007L, LIST,
                        new FieldBasicDto("Drug Regimen", "regimen"),
                        false, null, null,
                        Arrays.asList(new SettingDto("mds.form.label.values", Arrays.asList("Peldi", "Golden", "Patata"),
                                LIST, REQUIRE), new SettingDto("mds.form.label.allowUserSupplied", false, BOOLEAN),
                                new SettingDto("mds.form.label.allowMultipleSelections", false, BOOLEAN)), null
                )
        );

        List<MetadataDto> exampleMetadata2 = new LinkedList<>();
        exampleMetadata2.add(new MetadataDto("key1", "value1"));
        exampleMetadata2.add(new MetadataDto("key2", "value2"));
        fields.add(
                new FieldDto(
                        4L, 9007L, INTEGER,
                        new FieldBasicDto("Voucher Number", "voucherNumber"),
                        false, exampleMetadata2,
                        FieldValidationDto.INTEGER, null, null
                )
        );

        List<MetadataDto> exampleMetadata3 = new LinkedList<>();
        exampleMetadata3.add(new MetadataDto("key3", "value3"));
        fields.add(
                new FieldDto(
                        5L, 9007L, STRING,
                        new FieldBasicDto("Redeemed By", "redeemedBy"),
                        false, exampleMetadata3,
                        FieldValidationDto.STRING, null, null
                )
        );

        fields.add(
                new FieldDto(
                        6L, 9007L, BOOLEAN,
                        new FieldBasicDto("Active", "active"),
                        false, exampleMetadata3,
                        null, null, null
                )
        );

        AdvancedSettingsDto exampleAdvancedSetting = new AdvancedSettingsDto();
        RestOptionsDto exampleRestOptions = new RestOptionsDto();

        List<Number> fields = new LinkedList<>();
        fields.add(2L);
        fields.add(5L);
        exampleRestOptions.setCreate(true);
        exampleRestOptions.setFieldIds(fields);
        exampleAdvancedSetting.setId(advancedSettings.size() + 1L);
        exampleAdvancedSetting.setEntityId(7L);
        exampleAdvancedSetting.setRestOptions(exampleRestOptions);
        advancedSettings.add(exampleAdvancedSetting);

        SecuritySettingsDto exampleSecuritySettings = new SecuritySettingsDto();
        exampleSecuritySettings.setId(securitySettings.size() + 1L);
        exampleSecuritySettings.setEntityId(7L);
        exampleSecuritySettings.setAccess(AccessOptions.ROLES);
        exampleSecuritySettings.addRole("User Admin");
        securitySettings.add(exampleSecuritySettings);

        instanceFields.add(new FieldInstanceDto(1L, 1L, new FieldBasicDto("Date", "date")));
        instanceFields.add(new FieldInstanceDto(2L, 1L, new FieldBasicDto("User", "user")));
        instanceFields.add(new FieldInstanceDto(3L, 1L, new FieldBasicDto("Action", "action")));
        instanceFields.add(new FieldInstanceDto(4L, 1L, new FieldBasicDto("Changes", "changes")));

        entityRecords = createEntityRecords();
        entityHistory = createEntityHistoryRecords();
        entityRecordsHistory = createEntityRecordsHistory();
    }

    public List<FieldDto> getFields(Long entityId) {
        List<FieldDto> list = new ArrayList<>();

        for (FieldDto field : draftFields) {
            if (field.getEntityId().equals(entityId)) {
                list.add(field);
            }
        }

        if (fieldsHistory.containsKey(entityId)) {
            for (Map.Entry<Long, FieldDto> entry : fieldsHistory.get(entityId).entrySet()) {
                boolean found = false;

                for (int i = list.size() - 1; i >= 0; --i) {
                    FieldDto field = list.get(i);

                    if (field.getId().equals(entry.getKey())) {
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
                return one.getId().compareTo(two.getId());
            }
        });

        return list;
    }

    public List<FieldInstanceDto> getInstanceFields(Long instanceId) {
        List<FieldInstanceDto> list = new ArrayList<>();

        for (FieldInstanceDto field : instanceFields) {
            if (field.getInstanceId().equals(instanceId)) {
                list.add(field);
            }
        }

        return list;
    }

    public FieldDto getField(Long id) {
        FieldDto found = null;

        for (FieldDto field : draftFields) {
            if (field.getId().equals(id)) {
                found = field;
            }
        }

        if (null != found && fieldsHistory.containsKey(found.getEntityId())
                && fieldsHistory.get(found.getEntityId()).containsKey(id)) {
            found = fieldsHistory.get(found.getEntityId()).get(id);
        }

        return found;
    }

    public FieldDto findFieldByName(Long entityId, String name) {
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

    public void removeField(Long id) {
        for (int i = draftFields.size() - 1; i >= 0; i--) {
            if (fields.get(i).getId().equals(id)) {
                fields.remove(i);
                break;
            }
        }
    }

    public List<TypeDto> getTypes() {
        return new ArrayList<>(types);
    }

    public AdvancedSettingsDto getAdvanced(Long entityId) {
        AdvancedSettingsDto found = clone(AdvancedSettingsDto.class, getPurgeAdvanced(entityId));

        if (advancedHistory.containsKey(entityId)) {
            AdvancedSettingsDto temporary = advancedHistory.get(entityId);

            found.setId(temporary.getId());
            found.setEntityId(temporary.getEntityId());
            found.setTracking(temporary.getTracking());
            found.setIndexes(temporary.getIndexes());
            found.setRestOptions(temporary.getRestOptions());
            found.setBrowsing(temporary.getBrowsing());
        }

        return found;
    }

    public AdvancedSettingsDto getPurgeAdvanced(Long entityId) {
        AdvancedSettingsDto found = null;

        for (AdvancedSettingsDto item : advancedSettings) {
            if (item.getEntityId().equals(entityId)) {
                found = item;
                break;
            }
        }

        if (null == found) {
            found = new AdvancedSettingsDto();
            found.setId(advancedSettings.size() + 1L);
            found.setEntityId(entityId);

            advancedSettings.add(found);
        }

        return found;
    }

    public SecuritySettingsDto getSecurity(Long entityId) {
        SecuritySettingsDto found = clone(SecuritySettingsDto.class, getPurgeSecurity(entityId));

        if (securityHistory.containsKey(entityId)) {
            SecuritySettingsDto temporary = securityHistory.get(entityId);
            found.setId(temporary.getId());
            found.setEntityId(temporary.getEntityId());
            found.setAccess(temporary.getAccess());
            found.setUsers(temporary.getUsers());
            found.setRoles(temporary.getRoles());
        }

        return found;
    }

    private SecuritySettingsDto getPurgeSecurity(Long entityId) {
        SecuritySettingsDto found = null;
        for (SecuritySettingsDto item : securitySettings) {
            if (item.getEntityId().equals(entityId)) {
                found = item;
                break;
            }
        }

        if (null == found) {
            found = new SecuritySettingsDto();
            found.setId(securitySettings.size() + 1L);
            found.setEntityId(entityId);
            found.setAccess(AccessOptions.EVERYONE);

            securitySettings.add(found);
        }

        return found;
    }

    public TypeDto getType(String typeClass) {
        TypeDto found = null;

        for (TypeDto item : types) {
            if (equalsIgnoreCase(item.getTypeClass(), typeClass)) {
                found = item;
                break;
            }
        }

        return found;
    }

    public void draft(Long entityId, DraftData data) {
        if (data.isCreate()) {
            draftCreate(entityId, data);
        } else if (data.isEdit()) {
            draftEdit(entityId, data);
        } else if (data.isRemove()) {
            draftRemove(entityId, data);
        }
    }

    private void draftCreate(Long entityId, DraftData data) {
        if (!fieldsHistory.containsKey(entityId)) {
            fieldsHistory.put(entityId, new HashMap<Long, FieldDto>());
        }

        Map<Long, FieldDto> map = fieldsHistory.get(entityId);

        String typeClass = data.getValue(DraftData.TYPE_CLASS).toString();
        String displayName = data.getValue(DraftData.DISPLAY_NAME).toString();
        String name = data.getValue(DraftData.NAME).toString();
        Long fieldId = draftFields.size() + 1L;

        FieldBasicDto basic = new FieldBasicDto();
        basic.setName(name);
        basic.setDisplayName(displayName);

        TypeDto fieldType = getType(typeClass);
        List<SettingDto> fieldSettings = typeSettings.get(fieldType);
        FieldValidationDto fieldValidation = null;
        if (typeValidation.get(fieldType) != null) {
            fieldValidation = new FieldValidationDto(typeValidation.get(fieldType).getCriteria().toArray(new ValidationCriterionDto[typeValidation.get(fieldType).getCriteria().size()]));
        }

        FieldDto field = new FieldDto();
        field.setId(fieldId);
        field.setEntityId(entityId);
        field.setBasic(basic);
        field.setType(fieldType);
        field.setValidation(fieldValidation);
        field.setSettings(fieldSettings);

        map.put(fieldId, field);
    }

    private void draftEdit(Long entityId, DraftData data) {
        Object advancedValue = data.getValues().get(DraftData.ADVANCED);
        boolean editAdvanced = null != advancedValue && parseBoolean(advancedValue.toString());
        Object securityValue = data.getValues().get(DraftData.SECURITY);
        boolean editSecurity = null != securityValue && parseBoolean(securityValue.toString());
        String[] path = data.getValue(DraftData.PATH).toString().split("\\.");
        List value = (List) data.getValue(DraftData.VALUE);
        Object start = null;

        if (editAdvanced) {
            if (!advancedHistory.containsKey(entityId)) {
                advancedHistory.put(
                        entityId, clone(AdvancedSettingsDto.class, getPurgeAdvanced(entityId))
                );
            }
            start = advancedHistory.get(entityId);
        } else if (editSecurity) {
            if (!securityHistory.containsKey(entityId)) {
                securityHistory.put(
                        entityId, clone(SecuritySettingsDto.class, getPurgeSecurity(entityId))
                );
            }
            start = securityHistory.get(entityId);
        } else {
            if (!fieldsHistory.containsKey(entityId)) {
                fieldsHistory.put(entityId, new HashMap<Long, FieldDto>());
            }

            Map<Long, FieldDto> map = fieldsHistory.get(entityId);
            Long fieldId = Long.valueOf(data.getValue(DraftData.FIELD_ID).toString());

            for (FieldDto fieldDto : map.values()) {
                if (fieldDto.getId().equals(fieldId)) {
                    start = fieldDto;
                    break;
                }
            }

            if (start == null) {
                FieldDto field = getField(fieldId);
                map.put(fieldId, clone(FieldDto.class, field));
            }

            start = map.get(fieldId);
        }

        Object field = findField(path, start);
        FieldHelper.setField(field, path[path.length - 1], value);
    }

    private void draftRemove(Long entityId, DraftData data) {
        if (!fieldsHistory.containsKey(entityId)) {
            fieldsHistory.put(entityId, new HashMap<Long, FieldDto>());
        }

        Map<Long, FieldDto> map = fieldsHistory.get(entityId);

        Long fieldId = Long.valueOf(data.getValue(DraftData.FIELD_ID).toString());
        map.put(fieldId, null);
    }

    public boolean isAnyChangeInFields(Long entityId) {
        if (fieldsHistory.containsKey(entityId)) {
            for (Map.Entry<Long, FieldDto> entry : fieldsHistory.get(entityId).entrySet()) {
                FieldDto field = entry.getValue();
                boolean found = false;

                for (int i = draftFields.size() - 1; i >= 0; --i) {
                    if (draftFields.get(i).getId().equals(entry.getKey())) {
                        if (null == field || !field.equals(draftFields.get(i))) {
                            return true;
                        }
                        found = true;
                    }
                }

                if (!found && null != field) {
                    return true;
                }
            }
        }
        if (advancedHistory.containsKey(entityId)) {
            AdvancedSettingsDto current = getPurgeAdvanced(entityId);
            AdvancedSettingsDto temporary = advancedHistory.get(entityId);

            if (!temporary.equals(current)) {
                return true;
            }
        }
        if (securityHistory.containsKey(entityId)) {
            SecuritySettingsDto current = getPurgeSecurity(entityId);
            SecuritySettingsDto temporary = securityHistory.get(entityId);

            if (!temporary.equals(current)) {
                return true;
            }
        }

        return false;
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

    public void commitChanges(Long entityId) {
        if (fieldsHistory.containsKey(entityId)) {
            for (Map.Entry<Long, FieldDto> entry : fieldsHistory.get(entityId).entrySet()) {
                FieldDto field = entry.getValue();
                boolean found = false;

                for (int i = draftFields.size() - 1; i >= 0; --i) {
                    if (draftFields.get(i).getId().equals(entry.getKey())) {
                        if (null == field) {
                            draftFields.remove(i);
                        } else {
                            draftFields.set(i, field);
                        }

                        found = true;
                        break;
                    }
                }

                if (!found && null != field) {
                    draftFields.add(field);
                }
            }
        }

        if (advancedHistory.containsKey(entityId)) {
            AdvancedSettingsDto current = getPurgeAdvanced(entityId);
            AdvancedSettingsDto temporary = advancedHistory.get(entityId);

            current.setId(temporary.getId());
            current.setEntityId(temporary.getEntityId());
            current.setTracking(clone(TrackingDto.class, temporary.getTracking()));
            current.setIndexes(temporary.getIndexes());
            current.setRestOptions(clone(RestOptionsDto.class, temporary.getRestOptions()));
            current.setBrowsing(clone(BrowsingSettingsDto.class, temporary.getBrowsing()));
        }

        if (securityHistory.containsKey(entityId)) {
            SecuritySettingsDto current = getPurgeSecurity(entityId);
            SecuritySettingsDto temporary = securityHistory.get(entityId);

            current.setId(temporary.getId());
            current.setEntityId(temporary.getEntityId());
            current.setAccess(temporary.getAccess());
            current.setUsers(temporary.getUsers());
            current.setRoles(temporary.getRoles());
        }

        abandonChanges(entityId);
    }

    public void abandonChanges(Long entityId) {
        fieldsHistory.remove(entityId);
        advancedHistory.remove(entityId);
        securityHistory.remove(entityId);
    }

    private <T> T clone(Class<T> clazz, Object obj) {
        try {
            byte[] bytes = mapper.writeValueAsBytes(obj);

            return mapper.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void addType(TypeDto type, FieldValidationDto validation,
                         SettingDto... settings) {
        types.add(type);
        typeValidation.put(type, validation);

        if (ArrayUtils.isEmpty(settings)) {
            typeSettings.put(type, null);
        } else {
            List<SettingDto> list = new LinkedList<>();
            Collections.addAll(list, settings);

            typeSettings.put(type, list);
        }
    }

    public List<EntityRecord> createEntityRecords() {
        return new ArrayList<>();
    }

    public List<EntityRecord> getEntityRecordsById(Long entityId) {
        List<EntityRecord> entityRecordList = new ArrayList<>();
        for (EntityRecord entityRecord : entityRecords) {
            if (entityRecord.getEntitySchemaId().equals(entityId)) {
                entityRecordList.add(entityRecord);
            }
        }

        return entityRecordList;
    }

    public List<HistoryRecord> createEntityHistoryRecords() {
        return new ArrayList<>();
    }

    public List<HistoryRecord> getInstanceHistoryRecordsById(Long instanceId) {
        List<HistoryRecord> instanceHistoryList = new ArrayList<>();

        for (HistoryRecord historyRecord : entityHistory) {
            if (historyRecord.getInstanceId().equals(instanceId)) {
                instanceHistoryList.add(historyRecord);
            }
        }

        return instanceHistoryList;
    }

    public List<PreviousRecord> createEntityRecordsHistory() {
        return new ArrayList<>();
    }

    public List<PreviousRecord> getPreviousRecordsById(Long historyId) {
        List<PreviousRecord> previousRecordList = new ArrayList<>();
        for (PreviousRecord previousRecord : entityRecordsHistory) {
            if (previousRecord.getHistoryId().equals(historyId)) {
                previousRecordList.add(previousRecord);
            }
        }
        return previousRecordList;
    }

    public List<SettingDto> getTypeSettings(TypeDto typeDto) {
        return typeSettings.get(typeDto);
    }

    public FieldValidationDto getFieldValidationForType(TypeDto typeDto) {
        return typeValidation.get(typeDto);
    }
}
//CHECKSTYLE:ON
