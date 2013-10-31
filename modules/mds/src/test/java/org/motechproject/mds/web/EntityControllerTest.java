package org.motechproject.mds.web;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.exception.EntityAlreadyExistException;
import org.motechproject.mds.exception.EntityNotFoundException;
import org.motechproject.mds.exception.MDSValidationException;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.utils.JsonUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class EntityControllerTest {
    @Mock
    private EntityService entityService;

    private EntityController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        controller = new EntityController(entityService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        controller.init();
    }

    @Test
    public void shouldReturnRecordsIfTermIsNotDefined() throws Exception {
        List<EntityDto> expected = new ArrayList<>();
        expected.add(new EntityDto("5", "Appointments", "Appointments"));
        expected.add(new EntityDto("6", "Call Log Item", "IVR"));
        expected.add(new EntityDto("8", "Campaign", "Message Campaign"));
        expected.add(new EntityDto("1", "Patient", "OpenMRS", "navio"));
        expected.add(new EntityDto("3", "Patient", "OpenMRS", "accra"));

        SelectResult result = controller.getEntities(new SelectData());

        assertEquals(expected, result.getResults());
        assertTrue(result.isMore());
    }

    @Test
    public void shouldReturnRecordsAccordingWithTerm() throws Exception {
        List<EntityDto> expected = new ArrayList<>();
        expected.add(new EntityDto("1", "Patient", "OpenMRS", "navio"));
        expected.add(new EntityDto("3", "Patient", "OpenMRS", "accra"));
        expected.add(new EntityDto("2", "Person", "OpenMRS", "navio"));
        expected.add(new EntityDto("4", "Person", "OpenMRS", "accra"));

        SelectResult result = controller.getEntities(new SelectData("OpenMRS", null, null));

        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());

        expected.clear();
        expected.add(new EntityDto("3", "Patient", "OpenMRS", "accra"));
        expected.add(new EntityDto("4", "Person", "OpenMRS", "accra"));
        result = controller.getEntities(new SelectData("acc, OpenMRS", null, null));

        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());

        expected.clear();
        expected.add(new EntityDto("3", "Patient", "OpenMRS", "accra"));
        result = controller.getEntities(new SelectData("acc, OpenMRS, Pat", null, null));

        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());

        expected.clear();
        expected.add(new EntityDto("3", "Patient", "OpenMRS", "accra"));
        result = controller.getEntities(new SelectData("acc, OpenMRS, Pat, ignored", null, null));

        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());
    }

    @Test
    public void shouldReturnCorrectNumberOfRecords() throws Exception {
        List<EntityDto> expected = new ArrayList<>();
        expected.add(new EntityDto("5", "Appointments", "Appointments"));
        expected.add(new EntityDto("6", "Call Log Item", "IVR"));
        expected.add(new EntityDto("8", "Campaign", "Message Campaign"));
        expected.add(new EntityDto("1", "Patient", "OpenMRS", "navio"));

        SelectResult result = controller.getEntities(new SelectData(null, 4, 1));

        assertEquals(expected, result.getResults());
        assertTrue(result.isMore());

        expected.clear();
        expected.add(new EntityDto("3", "Patient", "OpenMRS", "accra"));
        expected.add(new EntityDto("2", "Person", "OpenMRS", "navio"));
        result = controller.getEntities(new SelectData(null, 2, 3));

        assertEquals(expected, result.getResults());
        assertTrue(result.isMore());

        expected.clear();
        expected.add(new EntityDto("4", "Person", "OpenMRS", "accra"));
        expected.add(new EntityDto("7", "Voucher"));
        result = controller.getEntities(new SelectData(null, 3, 4));

        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());
    }

    @Test
    public void shouldReturnEntityById() throws Exception {
        assertEquals(
                new EntityDto("7", "Voucher"),
                controller.getEntity("7")
        );
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldReturnNullIfNotFoundEntity() throws Exception {
        controller.getEntity("10");
    }

    @Test
    public void shouldCreateNewEntity() throws Exception {
        EntityDto entityToCreate = new EntityDto(null, "Patient", "mrs");
        String requestBody = JsonUtils.toJson(entityToCreate);

        MvcResult mvcResult = mockMvc
                .perform(post("/entities")
                        .body(requestBody.getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(entityService).create(entityToCreate);
        EntityDto createdEntity = JsonUtils.fromJson(mvcResult.getResponse().getContentAsString(), EntityDto.class);
        assertEquals(entityToCreate, createdEntity);
    }

    @Test
    public void shouldHandleEntityNotFoundException() throws Exception {
        mockMvc.perform(
                get("/entities/11")
        ).andExpect(
                status().is(HttpStatus.SC_CONFLICT)
        ).andExpect(
                content().string("key:mds.error.entityNotFound")
        );
    }

    @Test
    public void shouldHandleEntityAlreadyExistException() throws Exception {
        EntityDto entityToCreate = new EntityDto(null, "Patient", "mrs");
        String requestBody = JsonUtils.toJson(entityToCreate);
        doThrow(new EntityAlreadyExistException("key:mds.validation.error.entityAlreadyExist")).when(entityService).create(entityToCreate);

        mockMvc.perform(post("/entities")
                        .body(requestBody.getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.SC_CONFLICT))
                .andExpect(content().string("key:mds.validation.error.entityAlreadyExist"))
                .andReturn();
    }

    @Test
    public void shouldHandleMDSValidationException() throws Exception {
        EntityDto entityToCreate = new EntityDto(null, "Patient", "mrs");
        String requestBody = JsonUtils.toJson(entityToCreate);
        doThrow(new MDSValidationException("key:mds.validation.error.entityNameLength")).when(entityService).create(entityToCreate);

        mockMvc.perform(post("/entities")
                        .body(requestBody.getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.SC_BAD_REQUEST))
                .andExpect(content().string("key:mds.validation.error.entityNameLength"))
                .andReturn();
    }
}
