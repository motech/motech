package org.motechproject.mds.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.RestOptions;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.web.DraftData;
import org.motechproject.mds.web.ExampleData;
import org.motechproject.mds.web.SelectData;
import org.motechproject.mds.web.SelectResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.motechproject.mds.dto.TypeDto.STRING;

public class EntityControllerTest {
    private EntityController controller = new EntityController();

    @Before
    public void setUp() throws Exception {
        EntityController.setExampleData(new ExampleData());
    }

    @Test
    public void shouldReturnRecordsSortedByName() throws Exception {
        List<EntityDto> expected = new ArrayList<>();
        expected.add(new EntityDto("5", "Appointments", "Appointments"));
        expected.add(new EntityDto("6", "Call Log Item", "IVR"));
        expected.add(new EntityDto("8", "Campaign", "Message Campaign"));
        expected.add(new EntityDto("1", "Patient", "OpenMRS", "navio"));
        expected.add(new EntityDto("3", "Patient", "OpenMRS", "accra"));
        expected.add(new EntityDto("2", "Person", "OpenMRS", "navio"));
        expected.add(new EntityDto("4", "Person", "OpenMRS", "accra"));
        expected.add(new EntityDto("7", "Voucher"));

        SelectResult<EntityDto> result = controller.getEntities(new SelectData(null, 1, 10));

        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());
    }

    @Test
    public void shouldReturnEntityById() throws Exception {
        assertEquals(new EntityDto("7", "Voucher"), controller.getEntity("7"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionIfNotFoundEntity() throws Exception {
        controller.getEntity("10");
    }

    @Test
    public void shouldDeleteEntity() throws Exception {
        controller.deleteEntity("7");

        List<EntityDto> expected = new ArrayList<>();
        expected.add(new EntityDto("5", "Appointments", "Appointments"));
        expected.add(new EntityDto("6", "Call Log Item", "IVR"));
        expected.add(new EntityDto("8", "Campaign", "Message Campaign"));
        expected.add(new EntityDto("1", "Patient", "OpenMRS", "navio"));
        expected.add(new EntityDto("3", "Patient", "OpenMRS", "accra"));
        expected.add(new EntityDto("2", "Person", "OpenMRS", "navio"));
        expected.add(new EntityDto("4", "Person", "OpenMRS", "accra"));

        SelectResult<EntityDto> result = controller.getEntities(new SelectData(null, 1, 10));

        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionIfEntityToDeleteNotExists() throws Exception {
        controller.deleteEntity("1000");
    }

    @Test(expected = EntityReadOnlyException.class)
    public void shouldThrowExceptionIfEntityToDeleteisReadonly() throws Exception {
        controller.deleteEntity("1");
    }

    @Test
    public void shouldCreateNewEntity() throws Exception {
        assertEquals(
                new EntityDto("9", "Test"),
                controller.saveEntity(new EntityDto("11", "Test"))
        );
    }

    @Test(expected = EntityAlreadyExistException.class)
    public void shouldThrowOExceptionIfEntityWithGivenNameExists() throws Exception {
        controller.saveEntity(new EntityDto("7", "Voucher"));
    }

    @Test
    public void shouldSaveTemporaryChange() throws Exception {
        DraftData data = new DraftData();
        data.setEdit(true);
        data.setValues(new HashMap<String, Object>());
        data.getValues().put(DraftData.PATH, "basic.displayName");
        data.getValues().put(DraftData.FIELD_ID, "2");
        data.getValues().put(DraftData.VALUE, Arrays.asList("test"));

        EntityDto entity = controller.getEntity("7");
        List<FieldDto> fields = controller.getFields("7");
        FieldDto fieldDto = findFieldById(fields, "2");

        // before change
        assertFalse(entity.isDraft());
        assertNotNull(fieldDto);
        assertEquals("ID", fieldDto.getBasic().getDisplayName());

        // change
        controller.draft("7", data);

        entity = controller.getEntity("7");
        fields = controller.getFields("7");
        fieldDto = findFieldById(fields, "2");

        // after change
        assertTrue(entity.isDraft());
        assertNotNull(fieldDto);
        assertEquals("test", fieldDto.getBasic().getDisplayName());
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotSaveTemporaryChangeIfEntityNotExists() throws Exception {
        controller.draft("100", new DraftData());
    }

    @Test(expected = EntityReadOnlyException.class)
    public void shouldNotSaveTemporaryChangeIfEntityIsReadonly() throws Exception {
        controller.draft("1", new DraftData());
    }

    @Test
    public void shouldAbandonChanges() throws Exception {
        DraftData data = new DraftData();
        data.setEdit(true);
        data.setValues(new HashMap<String, Object>());
        data.getValues().put(DraftData.PATH, "basic.displayName");
        data.getValues().put(DraftData.FIELD_ID, "2");
        data.getValues().put(DraftData.VALUE, Arrays.asList("test"));

        controller.draft("7", data);
        assertTrue(controller.getEntity("7").isDraft());

        controller.abandonChanges("7");
        assertFalse(controller.getEntity("7").isDraft());
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotAbandonChangesIfEntityNotExists() throws Exception {
        controller.abandonChanges("10000");
    }

    @Test
    public void shouldComitChanges() throws Exception {
        DraftData data = new DraftData();
        data.setEdit(true);
        data.setValues(new HashMap<String, Object>());
        data.getValues().put(DraftData.PATH, "basic.displayName");
        data.getValues().put(DraftData.FIELD_ID, "2");
        data.getValues().put(DraftData.VALUE, Arrays.asList("test"));

        controller.draft("7", data);
        assertTrue(controller.getEntity("7").isDraft());

        controller.commitChanges("7");
        assertFalse(controller.getEntity("7").isDraft());
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotComitChangesIfEntityNotExists() throws Exception {
        controller.commitChanges("10000");
    }

    @Test
    public void shouldGetEntityFields() throws Exception {
        List<MetadataDto> exampleMetadata = new LinkedList<>();
        exampleMetadata.add(new MetadataDto("key1", "value1"));
        exampleMetadata.add(new MetadataDto("key2", "value2"));

        List<FieldDto> expected = new LinkedList<>();
        expected.add(
                new FieldDto(
                        "1", "5", STRING,
                        new FieldBasicDto("ID", "ID", false, "pass", null),
                        exampleMetadata, FieldValidationDto.STRING, null
                )
        );

        assertEquals(expected, controller.getFields("5"));

    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotGetEntityFieldsIfEntityNotExists() throws Exception {
        controller.getFields("10000");
    }

    @Test
    public void shouldFindFieldByName() throws Exception {
        List<MetadataDto> exampleMetadata = new LinkedList<>();
        exampleMetadata.add(new MetadataDto("key1", "value1"));
        exampleMetadata.add(new MetadataDto("key2", "value2"));

        FieldDto expected = new FieldDto(
                "1", "5", STRING,
                new FieldBasicDto("ID", "ID", false, "pass", null),
                exampleMetadata, FieldValidationDto.STRING, null
        );

        assertEquals(expected, controller.getFieldByName("5", "ID"));

    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotFindFieldByNameIfEntityNotExists() throws Exception {
        controller.getFieldByName("500", "ID");
    }

    @Test
    public void shouldGetAdvancedSettingsForEntity() throws Exception {
        AdvancedSettingsDto expected = new AdvancedSettingsDto();
        RestOptions restOptions = new RestOptions();
        List<String> fields = new LinkedList<>();
        fields.add("2");
        fields.add("5");
        restOptions.setCreate(true);
        restOptions.setFieldIds(fields);
        expected.setId("1");
        expected.setEntityId("7");
        expected.setRestOptions(restOptions);

        assertEquals(expected, controller.getAdvanced("7"));
    }

    private FieldDto findFieldById(List<FieldDto> fields, String id) {
        FieldDto found = null;

        for (FieldDto field : fields) {
            if (equalsIgnoreCase(field.getId(), id)) {
                found = field;
                break;
            }
        }

        return found;
    }


}
