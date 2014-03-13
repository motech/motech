package org.motechproject.mds.web;

import org.junit.Test;
import org.motechproject.mds.TestData;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.mds.dto.EntityDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SelectResultTest {
    private static final List<EntityDto> ENTITIES = TestData.getEntities();

    @Test
    public void shouldReturnCorrectNumberOfRecords() throws Exception {
        List<EntityDto> expected = new ArrayList<>();
        expected.add(new EntityDto(9001L, "org.motechproject.openmrs.ws.resource.model.Patient", "MOTECH OpenMRS Web Services", "navio", SecurityMode.EVERYONE, null));
        expected.add(new EntityDto(9002L, "org.motechproject.openmrs.ws.resource.model.Person", "MOTECH OpenMRS Web Services", "navio", SecurityMode.EVERYONE, null));
        expected.add(new EntityDto(9003L, "org.motechproject.openmrs.ws.resource.model.Patient", "MOTECH OpenMRS Web Services", "accra", SecurityMode.EVERYONE, null));
        expected.add(new EntityDto(9004L, "org.motechproject.openmrs.ws.resource.model.Person", "MOTECH OpenMRS Web Services", "accra", SecurityMode.EVERYONE, null));

        SelectResult<EntityDto> result = new SelectResult<>(new SelectData(null, 1, 4), ENTITIES);
        assertEquals(expected, result.getResults());
        assertTrue(result.isMore());

        expected.clear();
        expected.add(new EntityDto(9005L, "org.motechproject.appointments.api.model.Appointment", "MOTECH Appointments API", SecurityMode.EVERYONE, null));
        expected.add(new EntityDto(9006L, "org.motechproject.ivr.domain.CallDetailRecord", "MOTECH IVR API", SecurityMode.EVERYONE, null));

        result = new SelectResult<>(new SelectData(null, 3, 2), ENTITIES);
        assertEquals(expected, result.getResults());
        assertTrue(result.isMore());

        expected.clear();
        expected.add(new EntityDto(9007L, "org.motechproject.mds.entity.Voucher", SecurityMode.EVERYONE, null));
        expected.add(new EntityDto(9008L, "org.motechproject.messagecampaign.domain.campaign.Campaign", "MOTECH Message Campaign", SecurityMode.EVERYONE, null));

        result = new SelectResult<>(new SelectData(null, 4, 3), ENTITIES);
        assertEquals(expected, result.getResults());
        assertFalse(result.isMore());
    }

}
