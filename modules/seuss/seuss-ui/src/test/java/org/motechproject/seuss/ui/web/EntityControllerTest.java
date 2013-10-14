package org.motechproject.seuss.ui.web;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.seuss.ui.domain.EntityDto;
import org.motechproject.seuss.ui.ex.EntityAlreadyExistException;
import org.motechproject.seuss.ui.ex.EntityNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class EntityControllerTest {
    private EntityController controller = new EntityController();
    private MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

    @Before
    public void setUp() throws Exception {
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
        assertEquals(
                new EntityDto("9", "Test"),
                controller.saveEntity(new EntityDto("11", "Test"))
        );
    }

    @Test(expected = EntityAlreadyExistException.class)
    public void shouldThrowOExceptionIfEntityWithGivenNameExists() throws Exception {
        controller.saveEntity(new EntityDto("", "Voucher"));
    }

    @Test
    public void shouldHandleEntityNotFoundException() throws Exception {
        mvc.perform(
                get("/entities/11")
        ).andExpect(
                status().is(HttpStatus.SC_CONFLICT)
        ).andExpect(
                content().string("key:seuss.error.entityNotFound")
        );
    }

    @Test
    public void shouldHandleEntityAlreadyExistException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("name", "Voucher");

        mvc.perform(
                post("/entities").body(json.toString().getBytes())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_CONFLICT)
        ).andExpect(
                content().string("key:seuss.error.entityAlreadyExist")
        );
    }

}
