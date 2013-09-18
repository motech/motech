package org.motechproject.seuss.ui.web;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.seuss.ui.domain.ObjectDto;
import org.motechproject.seuss.ui.ex.ObjectAlreadyExistException;
import org.motechproject.seuss.ui.ex.ObjectNotFoundException;
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

public class ObjectControllerTest {
    private ObjectController controller = new ObjectController();
    private MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

    @Before
    public void setUp() throws Exception {
        controller.init();
    }

    @Test
    public void shouldReturnRecordsIfTermIsNotDefined() throws Exception {
        List<ObjectDto> expected = new ArrayList<>();
        expected.add(new ObjectDto("5", "Appointments", "Appointments"));
        expected.add(new ObjectDto("6", "Call Log Item", "IVR"));
        expected.add(new ObjectDto("8", "Campaign", "Message Campaign"));
        expected.add(new ObjectDto("1", "Patient", "OpenMRS", "navio"));
        expected.add(new ObjectDto("3", "Patient", "OpenMRS", "accra"));

        SelectResult result = controller.getObjects(new SelectData());

        assertEquals(expected, result.getResults());
        assertTrue(result.isMore());
    }

    @Test
    public void shouldReturnRecordsAccordingWithTerm() throws Exception {
        List<ObjectDto> expected = new ArrayList<>();
        expected.add(new ObjectDto("1", "Patient", "OpenMRS", "navio"));
        expected.add(new ObjectDto("3", "Patient", "OpenMRS", "accra"));
        expected.add(new ObjectDto("2", "Person", "OpenMRS", "navio"));
        expected.add(new ObjectDto("4", "Person", "OpenMRS", "accra"));

        SelectResult result = controller.getObjects(new SelectData("OpenMRS", null, null));

        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());

        expected.clear();
        expected.add(new ObjectDto("3", "Patient", "OpenMRS", "accra"));
        expected.add(new ObjectDto("4", "Person", "OpenMRS", "accra"));
        result = controller.getObjects(new SelectData("acc, OpenMRS", null, null));

        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());

        expected.clear();
        expected.add(new ObjectDto("3", "Patient", "OpenMRS", "accra"));
        result = controller.getObjects(new SelectData("acc, OpenMRS, Pat", null, null));

        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());

        expected.clear();
        expected.add(new ObjectDto("3", "Patient", "OpenMRS", "accra"));
        result = controller.getObjects(new SelectData("acc, OpenMRS, Pat, ignored", null, null));

        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());
    }

    @Test
    public void shouldReturnCorrectNumberOfRecords() throws Exception {
        List<ObjectDto> expected = new ArrayList<>();
        expected.add(new ObjectDto("5", "Appointments", "Appointments"));
        expected.add(new ObjectDto("6", "Call Log Item", "IVR"));
        expected.add(new ObjectDto("8", "Campaign", "Message Campaign"));
        expected.add(new ObjectDto("1", "Patient", "OpenMRS", "navio"));

        SelectResult result = controller.getObjects(new SelectData(null, 4, 1));

        assertEquals(expected, result.getResults());
        assertTrue(result.isMore());

        expected.clear();
        expected.add(new ObjectDto("3", "Patient", "OpenMRS", "accra"));
        expected.add(new ObjectDto("2", "Person", "OpenMRS", "navio"));
        result = controller.getObjects(new SelectData(null, 2, 3));

        assertEquals(expected, result.getResults());
        assertTrue(result.isMore());

        expected.clear();
        expected.add(new ObjectDto("4", "Person", "OpenMRS", "accra"));
        expected.add(new ObjectDto("7", "Voucher"));
        result = controller.getObjects(new SelectData(null, 3, 4));

        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());
    }

    @Test
    public void shouldReturnObjectById() throws Exception {
        assertEquals(
                new ObjectDto("7", "Voucher"),
                controller.getObject("7")
        );
    }

    @Test(expected = ObjectNotFoundException.class)
    public void shouldReturnNullIfNotFoundObject() throws Exception {
        controller.getObject("10");
    }

    @Test
    public void shouldCreateNewObject() throws Exception {
        assertEquals(
                new ObjectDto("9", "Test"),
                controller.saveObject(new ObjectDto("11", "Test"))
        );
    }

    @Test(expected = ObjectAlreadyExistException.class)
    public void shouldThrowOExceptionIfObjectWithGivenNameExists() throws Exception {
        controller.saveObject(new ObjectDto("", "Voucher"));
    }

    @Test
    public void shouldHandleObjectNotFoundException() throws Exception {
        mvc.perform(
                get("/objects/11")
        ).andExpect(
                status().is(HttpStatus.SC_CONFLICT)
        ).andExpect(
                content().string("key:seuss.error.objectNotFound")
        );
    }

    @Test
    public void shouldHandleObjectAlreadyExistException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("name", "Voucher");

        mvc.perform(
                post("/objects").body(json.toString().getBytes())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_CONFLICT)
        ).andExpect(
                content().string("key:seuss.error.objectAlreadyExist")
        );
    }

}
