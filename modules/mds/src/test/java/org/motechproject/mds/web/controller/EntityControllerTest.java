package org.motechproject.mds.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.web.ExampleData;
import org.motechproject.mds.web.SelectData;
import org.motechproject.mds.web.SelectResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
        controller.saveEntity(new EntityDto("7", "Voucher"));
    }

}
