package org.motechproject.mds.web;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
 * The <code>ExampleData</code> is a class containing some MDS test data.
 */
public class ExampleData {
    private List<FieldDto> fields = new ArrayList<>();
    private List<FieldDto> draftFields = new ArrayList<>();
    private List<FieldInstanceDto> instanceFields = new ArrayList<>();
    private List<TypeDto> types = new ArrayList<>();
    private List<AdvancedSettingsDto> advancedSettings = new ArrayList<>();


    private Map<TypeDto, List<SettingDto>> typeSettings = new HashMap<>();
    private Map<TypeDto, FieldValidationDto> typeValidation = new HashMap<>();

    private Map<Long, AdvancedSettingsDto> advancedHistory = new HashMap<>();
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

        List<String> fields = new LinkedList<>();
        fields.add("field1");
        fields.add("field2");
        exampleRestOptions.setCreate(true);
        exampleRestOptions.setFieldNames(fields);
        exampleAdvancedSetting.setId(advancedSettings.size() + 1L);
        exampleAdvancedSetting.setEntityId(7L);
        exampleAdvancedSetting.setRestOptions(exampleRestOptions);
        advancedSettings.add(exampleAdvancedSetting);

        instanceFields.add(new FieldInstanceDto(1L, 1L, new FieldBasicDto("Date", "date")));
        instanceFields.add(new FieldInstanceDto(2L, 1L, new FieldBasicDto("User", "user")));
        instanceFields.add(new FieldInstanceDto(3L, 1L, new FieldBasicDto("Action", "action")));
        instanceFields.add(new FieldInstanceDto(4L, 1L, new FieldBasicDto("Changes", "changes")));
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
}
