package org.motechproject.mds.web;

import org.junit.Test;
import org.motechproject.mds.dto.EntityDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SelectResultTest {
    private static final List<EntityDto> ENTITIES = new ExampleData().getEntities();

    @Test
    public void shouldReturnCorrectNumberOfRecords() throws Exception {
        List<EntityDto> expected = new ArrayList<>();
        expected.add(new EntityDto("9001", "Patient", "OpenMRS", "navio"));
        expected.add(new EntityDto("9002", "Person", "OpenMRS", "navio"));
        expected.add(new EntityDto("9003", "Patient", "OpenMRS", "accra"));
        expected.add(new EntityDto("9004", "Person", "OpenMRS", "accra"));

        SelectResult<EntityDto> result = new SelectResult<>(new SelectData(null, 1, 4), ENTITIES);
        assertEquals(expected, result.getResults());
        assertTrue(result.isMore());

        expected.clear();
        expected.add(new EntityDto("9005", "Appointments", "Appointments"));
        expected.add(new EntityDto("9006", "Call Log Item", "IVR"));

        result = new SelectResult<>(new SelectData(null, 3, 2), ENTITIES);
        assertEquals(expected, result.getResults());
        assertTrue(result.isMore());

        expected.clear();
        expected.add(new EntityDto("9007", "Voucher"));
        expected.add(new EntityDto("9008", "Campaign", "Message Campaign"));

        result = new SelectResult<>(new SelectData(null, 4, 3), ENTITIES);
        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());
    }

}
