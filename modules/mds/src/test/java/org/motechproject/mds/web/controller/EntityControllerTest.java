package org.motechproject.mds.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mds.dto.AccessOptions;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.RestOptions;
import org.motechproject.mds.dto.SecuritySettingsDto;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.service.EntityService;
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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mds.dto.TypeDto.STRING;

public class EntityControllerTest {
    @Mock
    private EntityService entityService;

    private EntityController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new EntityController();
        controller.setEntityService(entityService);

        EntityController.setExampleData(new ExampleData());
    }

    @Test
    public void shouldReturnRecordsSortedByName() throws Exception {
        List<EntityDto> expected = new ArrayList<>();
        expected.add(new EntityDto("9005", "Appointments", "Appointments"));
        expected.add(new EntityDto("9006", "Call Log Item", "IVR"));
        expected.add(new EntityDto("9008", "Campaign", "Message Campaign"));
        expected.add(new EntityDto("9001", "Patient", "OpenMRS", "navio"));
        expected.add(new EntityDto("9003", "Patient", "OpenMRS", "accra"));
        expected.add(new EntityDto("9002", "Person", "OpenMRS", "navio"));
        expected.add(new EntityDto("9004", "Person", "OpenMRS", "accra"));
        expected.add(new EntityDto("9007", "Voucher"));

        SelectResult<EntityDto> result = controller.getEntities(new SelectData(null, 1, 10));

        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());
    }

    @Test
    public void shouldReturnEntityById() throws Exception {
        assertEquals(new EntityDto("9007", "Voucher"), controller.getEntity("9007"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionIfNotFoundEntity() throws Exception {
        controller.getEntity("10");
    }

    @Test
    public void shouldDeleteEntity() throws Exception {
        controller.deleteEntity(9007L);

        List<EntityDto> expected = new ArrayList<>();
        expected.add(new EntityDto("9005", "Appointments", "Appointments"));
        expected.add(new EntityDto("9006", "Call Log Item", "IVR"));
        expected.add(new EntityDto("9008", "Campaign", "Message Campaign"));
        expected.add(new EntityDto("9001", "Patient", "OpenMRS", "navio"));
        expected.add(new EntityDto("9003", "Patient", "OpenMRS", "accra"));
        expected.add(new EntityDto("9002", "Person", "OpenMRS", "navio"));
        expected.add(new EntityDto("9004", "Person", "OpenMRS", "accra"));

        SelectResult<EntityDto> result = controller.getEntities(new SelectData(null, 1, 10));

        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionIfEntityToDeleteNotExists() throws Exception {
        controller.deleteEntity(1000L);
    }

    @Test(expected = EntityReadOnlyException.class)
    public void shouldThrowExceptionIfEntityToDeleteisReadonly() throws Exception {
        controller.deleteEntity(9001L);
    }

    @Test
    public void shouldCreateNewEntity() throws Exception {
        EntityDto given = new EntityDto("11", "Test");
        EntityDto expected = new EntityDto("9", "Test");

        when(entityService.createEntity(given)).thenReturn(expected);

        assertEquals(expected, controller.saveEntity(given));
        verify(entityService).createEntity(given);
    }

    @Test(expected = EntityAlreadyExistException.class)
    public void shouldThrowOExceptionIfEntityWithGivenNameExists() throws Exception {
        when(entityService.createEntity(any(EntityDto.class))).thenThrow(new EntityAlreadyExistException());
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

        EntityDto entity = controller.getEntity("9007");
        List<FieldDto> fields = controller.getFields("9007");
        FieldDto fieldDto = findFieldById(fields, "2");

        // before change
        assertFalse(entity.isDraft());
        assertNotNull(fieldDto);
        assertEquals("ID", fieldDto.getBasic().getDisplayName());

        // change
        controller.draft("9007", data);

        entity = controller.getEntity("9007");
        fields = controller.getFields("9007");
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
        controller.draft("9001", new DraftData());
    }

    @Test
    public void shouldAbandonChanges() throws Exception {
        DraftData data = new DraftData();
        data.setEdit(true);
        data.setValues(new HashMap<String, Object>());
        data.getValues().put(DraftData.PATH, "basic.displayName");
        data.getValues().put(DraftData.FIELD_ID, "2");
        data.getValues().put(DraftData.VALUE, Arrays.asList("test"));

        controller.draft("9007", data);
        assertTrue(controller.getEntity("9007").isDraft());

        controller.abandonChanges("9007");
        assertFalse(controller.getEntity("9007").isDraft());
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

        controller.draft("9007", data);
        assertTrue(controller.getEntity("9007").isDraft());

        controller.commitChanges(9007L);
        assertFalse(controller.getEntity("9007").isDraft());
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotComitChangesIfEntityNotExists() throws Exception {
        controller.commitChanges(10000L);
    }

    @Test
    public void shouldGetEntityFields() throws Exception {
        List<MetadataDto> exampleMetadata = new LinkedList<>();
        exampleMetadata.add(new MetadataDto("key1", "value1"));
        exampleMetadata.add(new MetadataDto("key2", "value2"));

        List<FieldDto> expected = new LinkedList<>();
        expected.add(
                new FieldDto(
                        "1", "9005", STRING,
                        new FieldBasicDto("ID", "ID", false, "pass", null),
                        exampleMetadata, FieldValidationDto.STRING, null
                )
        );

        assertEquals(expected, controller.getFields("9005"));

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
                "1", "9005", STRING,
                new FieldBasicDto("ID", "ID", false, "pass", null),
                exampleMetadata, FieldValidationDto.STRING, null
        );

        assertEquals(expected, controller.getFieldByName("9005", "ID"));

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

    @Test
    public void shouldGetSecuritySettingsForEntity() throws Exception {
        SecuritySettingsDto expected = new SecuritySettingsDto();
        expected.setId("1");
        expected.setEntityId("7");
        expected.setAccess(AccessOptions.ROLES);
        expected.addRole("User Admin");

        assertEquals(expected, controller.getSecurity("7"));
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
