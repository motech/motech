package org.motechproject.mds.web.controller;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.motechproject.mds.TestData;
import org.motechproject.mds.dto.AccessOptions;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.RestOptionsDto;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mds.dto.TypeDto.STRING;

public class EntityControllerTest {

    @Mock
    private EntityService entityService;

    private EntityController controller;

    private final ExampleData exampleData = new ExampleData();

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        controller = new EntityController();
        controller.setEntityService(entityService);

        when(entityService.listEntities()).thenReturn(TestData.getEntities());

        when(entityService.getEntityForEdit(anyLong())).thenAnswer(new Answer<EntityDto>() {
            @Override
            public EntityDto answer(InvocationOnMock invocation) throws Throwable {
                return TestData.getEntity((Long) invocation.getArguments()[0]);
            }
        });
        when(entityService.getFields(anyLong())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return exampleData.getFields((Long) invocation.getArguments()[0]);
            }
        });
        when(entityService.getSecuritySettings(anyLong())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return exampleData.getSecurity((Long) invocation.getArguments()[0]);
            }
        });
        when(entityService.findFieldByName(anyLong(), anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return exampleData.findFieldByName((Long) invocation.getArguments()[0],
                        (String) invocation.getArguments()[1]);
            }
        });
        when(entityService.getAdvancedSettings(anyLong())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return exampleData.getAdvanced((Long) invocation.getArguments()[0]);
            }
        });
    }

    @Test
    public void shouldReturnRecordsSortedByName() throws Exception {
        List<EntityDto> expected = new ArrayList<>();
        expected.add(new EntityDto(9005L, "org.motechproject.appointments.api.model.Appointment", "MOTECH Appointments API"));
        expected.add(new EntityDto(9006L, "org.motechproject.ivr.domain.CallDetailRecord", "MOTECH IVR API"));
        expected.add(new EntityDto(9008L, "org.motechproject.messagecampaign.domain.campaign.Campaign", "MOTECH Message Campaign"));
        expected.add(new EntityDto(9001L, "org.motechproject.openmrs.ws.resource.model.Patient", "MOTECH OpenMRS Web Services", "navio"));
        expected.add(new EntityDto(9003L, "org.motechproject.openmrs.ws.resource.model.Patient", "MOTECH OpenMRS Web Services", "accra"));
        expected.add(new EntityDto(9002L, "org.motechproject.openmrs.ws.resource.model.Person", "MOTECH OpenMRS Web Services", "navio"));
        expected.add(new EntityDto(9004L, "org.motechproject.openmrs.ws.resource.model.Person", "MOTECH OpenMRS Web Services", "accra"));
        expected.add(new EntityDto(9007L, "org.motechproject.mds.entity.Voucher"));

        SelectResult<EntityDto> result = controller.getEntities(new SelectData(null, 1, 10));

        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());
    }

    @Test
    public void shouldReturnEntityById() throws Exception {
        assertEquals(new EntityDto(9007L, "org.motechproject.mds.entity.Voucher"), controller.getEntity(9007L));
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionIfNotFoundEntity() throws Exception {
        doThrow(new EntityNotFoundException()).when(entityService).getEntityForEdit(10L);
        controller.getEntity(10L);
    }

    @Test
    public void shouldDeleteEntity() throws Exception {
        controller.deleteEntity(9007L);

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(entityService).deleteEntity(captor.capture());

        assertEquals(Long.valueOf(9007), captor.getValue());
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionIfEntityToDeleteNotExists() throws Exception {
        doThrow(new EntityNotFoundException()).when(entityService).deleteEntity(1000L);
        controller.deleteEntity(1000L);
    }

    @Test(expected = EntityReadOnlyException.class)
    public void shouldThrowExceptionIfEntityToDeleteisReadonly() throws Exception {
        doThrow(new EntityReadOnlyException()).when(entityService).deleteEntity(9001L);
        controller.deleteEntity(9001L);
    }

    @Test
    public void shouldCreateNewEntity() throws Exception {
        EntityDto given = new EntityDto(11L, "Test");
        EntityDto expected = new EntityDto(9L, "Test");

        doReturn(expected).when(entityService).createEntity(given);
        doReturn(expected).when(entityService).getEntityForEdit(9L);

        assertEquals(expected, controller.saveEntity(given));
        verify(entityService).createEntity(given);
    }

    @Test(expected = EntityAlreadyExistException.class)
    public void shouldThrowExceptionIfEntityWithGivenNameExists() throws Exception {
        doThrow(new EntityAlreadyExistException()).when(entityService).createEntity(any(EntityDto.class));

        controller.saveEntity(new EntityDto(7L, "Voucher"));
    }

    @Ignore("Ignored until we get fields from database")
    @Test
    public void shouldSaveTemporaryChange() throws Exception {
        DraftData data = new DraftData();
        data.setEdit(true);
        data.setValues(new HashMap<String, Object>());
        data.getValues().put(DraftData.PATH, "basic.displayName");
        data.getValues().put(DraftData.FIELD_ID, "2");
        data.getValues().put(DraftData.VALUE, Arrays.asList("test"));

        EntityDto entity = controller.getEntity(9007L);
        List<FieldDto> fields = controller.getFields(9007L);
        FieldDto fieldDto = findFieldById(fields, 2L);

        // before change
        assertFalse(entity.isModified());
        assertNotNull(fieldDto);
        assertEquals("ID", fieldDto.getBasic().getDisplayName());

        // change
        controller.draft(9007L, data);

        verify(entityService).saveDraftEntityChanges(9007L, data);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotSaveTemporaryChangeIfEntityNotExists() throws Exception {
        doThrow(new EntityNotFoundException()).when(entityService).saveDraftEntityChanges(eq(100L), any(DraftData.class));
        controller.draft(100L, new DraftData());
    }

    @Test(expected = EntityReadOnlyException.class)
    public void shouldNotSaveTemporaryChangeIfEntityIsReadonly() throws Exception {
        doThrow(new EntityReadOnlyException()).when(entityService).saveDraftEntityChanges(eq(9001L), any(DraftData.class));
        controller.draft(9001L, new DraftData());
    }

    @Test
    public void shouldAbandonChanges() throws Exception {
        DraftData data = new DraftData();
        data.setEdit(true);
        data.setValues(new HashMap<String, Object>());
        data.getValues().put(DraftData.PATH, "basic.displayName");
        data.getValues().put(DraftData.FIELD_ID, "2");
        data.getValues().put(DraftData.VALUE, Arrays.asList("test"));

        controller.draft(9007L, data);
        verify(entityService).saveDraftEntityChanges(9007L, data);

        controller.abandonChanges(9007L);
        verify(entityService).abandonChanges(9007L);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotAbandonChangesIfEntityNotExists() throws Exception {
        doThrow(new EntityNotFoundException()).when(entityService).abandonChanges(10000L);
        controller.abandonChanges(10000L);
    }

    @Ignore("Ignored until we get drafts working with db")
    @Test
    public void shouldCommitChanges() throws Exception {
        DraftData data = new DraftData();
        data.setEdit(true);
        data.setValues(new HashMap<String, Object>());
        data.getValues().put(DraftData.PATH, "basic.displayName");
        data.getValues().put(DraftData.FIELD_ID, "2");
        data.getValues().put(DraftData.VALUE, Arrays.asList("test"));

        controller.draft(9007L, data);
        assertTrue(controller.getEntity(9007L).isModified());

        controller.commitChanges(9007L);
        assertFalse(controller.getEntity(9007L).isModified());
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotComitChangesIfEntityNotExists() throws Exception {
        doThrow(new EntityNotFoundException()).when(entityService).commitChanges(10000L);
        controller.commitChanges(10000L);
    }

    @Ignore("Ignored until we get fields from database")
    @Test
    public void shouldGetEntityFields() throws Exception {
        List<MetadataDto> exampleMetadata = new LinkedList<>();
        exampleMetadata.add(new MetadataDto("key1", "value1"));
        exampleMetadata.add(new MetadataDto("key2", "value2"));

        List<FieldDto> expected = new LinkedList<>();
        expected.add(
                new FieldDto(
                        1L, 9005L, STRING,
                        new FieldBasicDto("ID", "ID", false, "pass", null),
                        false, exampleMetadata, FieldValidationDto.STRING, null, null
                )
        );

        assertEquals(expected, controller.getFields(9005L));

    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotGetEntityFieldsIfEntityNotExists() throws Exception {
        doThrow(new EntityNotFoundException()).when(entityService).getFields(10000L);
        controller.getFields(10000L);
    }

    @Ignore("Ignored until we get fields from database")
    @Test
    public void shouldFindFieldByName() throws Exception {
        List<MetadataDto> exampleMetadata = new LinkedList<>();
        exampleMetadata.add(new MetadataDto("key1", "value1"));
        exampleMetadata.add(new MetadataDto("key2", "value2"));

        FieldDto expected = new FieldDto(
                1L, 9005L, STRING,
                new FieldBasicDto("ID", "ID", false, "pass", null),
                false, exampleMetadata, FieldValidationDto.STRING, null, null
        );

        assertEquals(expected, controller.getFieldByName(9005L, "ID"));

    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotFindFieldByNameIfEntityNotExists() throws Exception {
        doThrow(new EntityNotFoundException()).when(entityService).findFieldByName(500L, "ID");
        controller.getFieldByName(500L, "ID");
    }

    @Test
    public void shouldGetAdvancedSettingsForEntity() throws Exception {
        AdvancedSettingsDto expected = new AdvancedSettingsDto();
        RestOptionsDto restOptions = new RestOptionsDto();
        List<Number> fields = new LinkedList<>();
        fields.add(2);
        fields.add(5);
        restOptions.setCreate(true);
        restOptions.setFieldIds(fields);
        expected.setId(1L);
        expected.setEntityId(7L);
        expected.setRestOptions(restOptions);

        assertEquals(expected, controller.getAdvanced(7L));
    }

    @Test
    public void shouldGetSecuritySettingsForEntity() throws Exception {
        SecuritySettingsDto expected = new SecuritySettingsDto();
        expected.setId(1L);
        expected.setEntityId(7L);
        expected.setAccess(AccessOptions.ROLES);
        expected.addRole("User Admin");

        assertEquals(expected, controller.getSecurity(7L));
    }

    private FieldDto findFieldById(List<FieldDto> fields, Long id) {
        FieldDto found = null;

        for (FieldDto field : fields) {
            if (field.getId().equals(id)) {
                found = field;
                break;
            }
        }

        return found;
    }


}
