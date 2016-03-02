package org.motechproject.mds.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.DraftData;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.exception.entity.EntityAlreadyExistException;
import org.motechproject.mds.exception.entity.EntityNotFoundException;
import org.motechproject.mds.exception.entity.EntityReadOnlyException;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.MdsBundleRegenerationService;
import org.motechproject.mds.service.UserPreferencesService;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.mds.web.ExampleData;
import org.motechproject.mds.web.SelectData;
import org.motechproject.mds.web.SelectResult;
import org.motechproject.mds.web.TestData;
import org.springframework.http.MediaType;
import org.motechproject.mds.web.domain.GridFieldSelectionUpdate;
import org.motechproject.mds.web.domain.GridSelectionAction;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.ResultActions;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
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
import static org.motechproject.mds.dto.TypeDto.STRING;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class EntityControllerTest {

    private static final String ENTITY_NOT_FOUND = "key:mds.error.entityNotFound";
    private static final String ENTITY_READ_ONLY = "key:mds.error.entityIsReadOnly";
    private static final String ENTITY_ALREADY_EXSIST = "key:mds.error.entityAlreadyExist";

    @Mock
    private EntityService entityService;

    @Mock
    private MdsBundleRegenerationService mdsBundleRegenerationService;

    @Mock
    private UserPreferencesService userPreferencesService;

    @InjectMocks
    private EntityController entityController = new EntityController();

    private MockMvc controller;

    private final ExampleData exampleData = new ExampleData();

    ArgumentCaptor<DraftData> draftDataCaptor = ArgumentCaptor.forClass(DraftData.class);

    @Before
    public void setUp() throws Exception {
        controller = MockMvcBuilders.standaloneSetup(entityController).build();

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
        expected.add(new EntityDto(9005L, "org.motechproject.appointments.api.model.Appointment", "MOTECH Appointments API", SecurityMode.EVERYONE, null));
        expected.add(new EntityDto(9006L, "org.motechproject.ivr.domain.CallDetailRecord", "MOTECH IVR API", SecurityMode.EVERYONE, null));
        expected.add(new EntityDto(9008L, "org.motechproject.messagecampaign.domain.campaign.Campaign", "MOTECH Message Campaign", SecurityMode.EVERYONE, null));
        expected.add(new EntityDto(9001L, "org.motechproject.openmrs.ws.resource.model.Patient", "MOTECH OpenMRS Web Services", "navio", SecurityMode.EVERYONE, null));
        expected.add(new EntityDto(9003L, "org.motechproject.openmrs.ws.resource.model.Patient", "MOTECH OpenMRS Web Services", "accra", SecurityMode.EVERYONE, null));
        expected.add(new EntityDto(9002L, "org.motechproject.openmrs.ws.resource.model.Person", "MOTECH OpenMRS Web Services", "navio", SecurityMode.EVERYONE, null));
        expected.add(new EntityDto(9004L, "org.motechproject.openmrs.ws.resource.model.Person", "MOTECH OpenMRS Web Services", "accra", SecurityMode.EVERYONE, null));
        expected.add(new EntityDto(9007L, "org.motechproject.mds.entity.Voucher", SecurityMode.EVERYONE, null));
        SelectResult expectedResult = new SelectResult(new SelectData(null, 1, 10), expected);

        controller.perform(get("/selectEntities")
                .param("term", "")
                .param("page", String.valueOf(1))
                .param("pageLimit", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expectedResult)));
    }

    @Test
    public void shouldReturnEntityById() throws Exception {
        controller.perform(get("/entities/9007"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(new EntityDto(9007L, "org.motechproject.mds.entity.Voucher",
                        SecurityMode.EVERYONE, null))));
    }

    @Test
    public void shouldThrowExceptionIfNotFoundEntity() throws Exception {
        doThrow(new EntityNotFoundException(10L)).when(entityService).getEntityForEdit(10L);
        controller.perform(get("/entities/10"))
                .andExpect(status().isConflict())
                .andExpect(content().string(ENTITY_NOT_FOUND));
    }

    @Test
    public void shouldDeleteEntity() throws Exception {
        controller.perform(delete("/entities/9007"))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(entityService).deleteEntity(captor.capture());

        assertEquals(Long.valueOf(9007), captor.getValue());
    }

    @Test
    public void shouldThrowExceptionIfEntityToDeleteNotExists() throws Exception {
        doThrow(new EntityNotFoundException(1000L)).when(entityService).deleteEntity(1000L);
        controller.perform(delete("/entities/1000"))
                .andExpect(status().isConflict())
                .andExpect(content().string(ENTITY_NOT_FOUND));
    }

    @Test
    public void shouldThrowExceptionIfEntityToDeleteisReadonly() throws Exception {
        doThrow(new EntityReadOnlyException("EntityName")).when(entityService).deleteEntity(9001L);
        controller.perform(delete("/entities/9001"))
                .andExpect(status().isConflict())
                .andExpect(content().string(ENTITY_READ_ONLY));
    }

    @Test
    public void shouldCreateNewEntity() throws Exception {
        EntityDto given = new EntityDto(11L, "Test", SecurityMode.EVERYONE, null);
        EntityDto expected = new EntityDto(9L, "Test", SecurityMode.EVERYONE, null);

        doReturn(expected).when(entityService).createEntity(given);
        doReturn(expected).when(entityService).getEntityForEdit(9L);

        controller.perform(post("/entities")
                .body(new ObjectMapper().writeValueAsBytes(given))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));

        verify(entityService).createEntity(given);
    }

    @Test
    public void shouldThrowExceptionIfEntityWithGivenNameExists() throws Exception {
        doThrow(new EntityAlreadyExistException("Voucher")).when(entityService).createEntity(any(EntityDto.class));
        controller.perform(post("/entities")
                .body(new ObjectMapper().writeValueAsBytes(new EntityDto(7L, "Voucher", SecurityMode.EVERYONE, null)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(ENTITY_ALREADY_EXSIST));
    }

    @Test
    public void shouldSaveTemporaryChange() throws Exception {
        DraftData data = new DraftData();
        data.setEdit(true);
        data.setValues(new HashMap<>());
        data.getValues().put(DraftData.PATH, "basic.displayName");
        data.getValues().put(DraftData.FIELD_ID, "2");
        data.getValues().put(DraftData.VALUE, singletonList("test"));

        ResultActions actions = controller.perform(get("/entities/9007"))
                .andExpect(status().isOk());
        EntityDto entity = new ObjectMapper().readValue(actions.andReturn().getResponse().getContentAsByteArray(), EntityDto.class);

        actions = controller.perform(get("/entities/9007/fields"))
                .andExpect(status().isOk());
        List<FieldDto> fields = new ObjectMapper().readValue(actions.andReturn().getResponse().getContentAsByteArray(),
                new TypeReference<List<FieldDto>>() {});
        FieldDto fieldDto = findFieldById(fields, 12L);

        // before change
        assertFalse(entity.isModified());
        assertNotNull(fieldDto);
        assertEquals("ID", fieldDto.getBasic().getDisplayName());

        // change
        controller.perform(post("/entities/9007/draft")
                .body(new ObjectMapper().writeValueAsBytes(data))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(entityService).saveDraftEntityChanges(eq(9007L), draftDataCaptor.capture());
        DraftData captured = draftDataCaptor.getValue();
        assertTrue(captured.isEdit());
        assertEquals("basic.displayName", captured.getPath());
    }

    @Test
    public void shouldNotSaveTemporaryChangeIfEntityNotExists() throws Exception {
        doThrow(new EntityNotFoundException(100L)).when(entityService).saveDraftEntityChanges(eq(100L), any(DraftData.class));
        controller.perform(post("/entities/100/draft")
                .body(new ObjectMapper().writeValueAsString(new DraftData()).getBytes())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(ENTITY_NOT_FOUND));
    }

    @Test
    public void shouldNotSaveTemporaryChangeIfEntityIsReadonly() throws Exception {
        doThrow(new EntityReadOnlyException("EntityName")).when(entityService).saveDraftEntityChanges(eq(9001L), any(DraftData.class));
        controller.perform(post("/entities/9001/draft")
                .body(new ObjectMapper().writeValueAsString(new DraftData()).getBytes())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(ENTITY_READ_ONLY));
    }

    @Test
    public void shouldAbandonChanges() throws Exception {
        DraftData data = new DraftData();
        data.setEdit(true);
        data.setValues(new HashMap<String, Object>());
        data.getValues().put(DraftData.PATH, "basic.displayName");
        data.getValues().put(DraftData.FIELD_ID, "2");
        data.getValues().put(DraftData.VALUE, singletonList("test"));

        controller.perform(post("/entities/9007/draft")
                .body(new ObjectMapper().writeValueAsBytes(data))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(entityService).saveDraftEntityChanges(eq(9007l), draftDataCaptor.capture());

        DraftData capuredData = draftDataCaptor.getValue();
        assertNotNull(capuredData);
        assertTrue(capuredData.isEdit());
        assertEquals("basic.displayName", capuredData.getPath());

        controller.perform(post("/entities/9007/abandon")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(entityService).abandonChanges(9007L);
    }

    @Test
    public void shouldNotAbandonChangesIfEntityNotExists() throws Exception {
        doThrow(new EntityNotFoundException(10000L)).when(entityService).abandonChanges(10000L);
        controller.perform(post("/entities/10000/abandon")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(ENTITY_NOT_FOUND));
        verify(entityService).abandonChanges(10000L);
    }

    @Test
    public void shouldCommitChanges() throws Exception {
        when(entityService.commitChanges(9007L)).thenReturn(new ArrayList<>());

        controller.perform(post("/entities/9007/commit")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(entityService).commitChanges(9007l);
        verify(mdsBundleRegenerationService).regenerateMdsDataBundle();
    }

    @Test
    public void shouldNotComitChangesIfEntityNotExists() throws Exception {
        doThrow(new EntityNotFoundException(10000L)).when(entityService).commitChanges(10000L);
        controller.perform(post("/entities/10000/commit")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(ENTITY_NOT_FOUND));
    }

    @Test
    public void shouldGetEntityFields() throws Exception {
        List<MetadataDto> exampleMetadata = new LinkedList<>();
        exampleMetadata.add(new MetadataDto("key1", "value1"));
        exampleMetadata.add(new MetadataDto("key2", "value2"));

        List<FieldDto> expected = new LinkedList<>();
        expected.add(
                new FieldDto(
                        14L, 9005L, STRING,
                        new FieldBasicDto("Other", "Other", false, false, "test", null, null),
                        false, exampleMetadata, FieldValidationDto.STRING, null, null
                )
        );

        ResultActions actions = controller.perform(get("/entities/9005/fields"))
                .andExpect(status().isOk());
        List<FieldDto> fields = new ObjectMapper().readValue(actions.andReturn().getResponse().getContentAsByteArray(),
                new TypeReference<List<FieldDto>>() {});
        assertEquals(expected, fields);
    }

    @Test
    public void shouldNotGetEntityFieldsIfEntityNotExists() throws Exception {
        doThrow(new EntityNotFoundException(10000L)).when(entityService).getFields(10000L);
        controller.perform(get("/entities/10000/fields"))
                .andExpect(status().isConflict())
                .andExpect(content().string(ENTITY_NOT_FOUND));
    }

    @Test
    public void shouldFindFieldByName() throws Exception {
        List<MetadataDto> exampleMetadata = new LinkedList<>();
        exampleMetadata.add(new MetadataDto("key1", "value1"));
        exampleMetadata.add(new MetadataDto("key2", "value2"));

        FieldDto expected = new FieldDto(
                14L, 9005L, STRING,
                new FieldBasicDto("Other", "Other", false, false, "test", null, null),
                false, exampleMetadata, FieldValidationDto.STRING, null, null
        );

        ResultActions actions = controller.perform(get("/entities/9005/fields/Other"))
                .andExpect(status().isOk());
        FieldDto field = new ObjectMapper().readValue(actions.andReturn().getResponse().getContentAsByteArray(),
                FieldDto.class);
        assertEquals(expected, field);
    }

    @Test
    public void shouldNotFindFieldByNameIfEntityNotExists() throws Exception {
        doThrow(new EntityNotFoundException(500L)).when(entityService).findFieldByName(500L, "ID");
        controller.perform(get("/entities/500/fields/ID"))
                .andExpect(status().isConflict())
                .andExpect(content().string(ENTITY_NOT_FOUND));
    }

    @Test
    public void shouldGetAdvancedSettingsForEntity() throws Exception {
        AdvancedSettingsDto expected = new AdvancedSettingsDto();
        RestOptionsDto restOptions = new RestOptionsDto();
        List<String> fields = new LinkedList<>();
        fields.add("field1");
        fields.add("field2");
        restOptions.setCreate(true);
        restOptions.setFieldNames(fields);
        expected.setId(1L);
        expected.setEntityId(7L);
        expected.setRestOptions(restOptions);

        controller.perform(get("/entities/7/advanced"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));
    }

    @Test
    public void shouldGetEntitiesByBundle() throws Exception {
        EntityDto lessons = new EntityDto(null, "org.motechproject.mtraining.domain.Lesson", "Lesson", "mtraining", null,
                SecurityMode.EVERYONE, null);
        EntityDto chapters = new EntityDto(null, "org.motechproject.mtraining.domain.Chapter", "Chapter", "mtraining", null,
                SecurityMode.EVERYONE, null);
        List<EntityDto> expected = asList(lessons, chapters);

        when(entityService.listEntitiesByBundle("org.motechproject.mtraining")).thenReturn(expected);

        controller.perform(get("/entities/getEntitiesByBundle")
                .param("symbolicName", "org.motechproject.mtraining"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));
    }

    @Test
    public void shouldUpdateGridSize() throws Exception {
        setUpSecurityContext();
        controller.perform(post("/entities/2/preferences/gridSize")
                .body(new ObjectMapper().writeValueAsBytes(20))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userPreferencesService).updateGridSize(2l, "motech", 20);
    }

    @Test
    public void shouldSelectField() throws Exception {
        setUpSecurityContext();
        controller.perform(post("/entities/2/preferences/fields")
                .body(new ObjectMapper().writeValueAsString(new GridFieldSelectionUpdate("fieldName",
                        GridSelectionAction.ADD)).getBytes(Charset.forName("UTF-8")))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userPreferencesService).selectField(2l, "motech", "fieldName");
    }


    @Test
    public void shouldUnselectField() throws Exception {
        setUpSecurityContext();
        controller.perform(post("/entities/2/preferences/fields")
                .body(new ObjectMapper().writeValueAsString(new GridFieldSelectionUpdate("fieldName",
                        GridSelectionAction.REMOVE)).getBytes(Charset.forName("UTF-8")))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userPreferencesService).unselectField(2l, "motech", "fieldName");
    }


    @Test
    public void shouldSelectFields() throws Exception {
        setUpSecurityContext();
        controller.perform(post("/entities/2/preferences/fields")
                .body(new ObjectMapper().writeValueAsString(new GridFieldSelectionUpdate("fieldName",
                        GridSelectionAction.ADD_ALL)).getBytes(Charset.forName("UTF-8")))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userPreferencesService).selectFields(2l, "motech");
    }


    @Test
    public void shouldUnselectFields() throws Exception {
        setUpSecurityContext();
        controller.perform(post("/entities/2/preferences/fields")
                .body(new ObjectMapper().writeValueAsString(new GridFieldSelectionUpdate("fieldName",
                        GridSelectionAction.REMOVE_ALL)).getBytes(Charset.forName("UTF-8")))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userPreferencesService).unselectFields(2l, "motech");
    }

    private void setUpSecurityContext() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("mdsSchemaAccess");
        List<SimpleGrantedAuthority> authorities = asList(authority);

        User principal = new User("motech", "motech", authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "motech", authorities);

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);
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
