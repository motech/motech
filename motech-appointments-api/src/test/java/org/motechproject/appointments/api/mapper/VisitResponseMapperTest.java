package org.motechproject.appointments.api.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.util.DateUtil;

import java.util.Collections;

import static junit.framework.Assert.*;

public class VisitResponseMapperTest {

    @Test
    public void mapVisitToVisitResponse() {
        DateTime now = DateUtil.now();
        String visitName = "visitName";
        String typeOfVisit = "scheduled";

        Visit visit = new Visit();
        visit.name(visitName).typeOfVisit(typeOfVisit).visitDate(now).markAsMissed()
                .addAppointment(now, Collections.<Reminder>emptyList()).addData("key", "value");

        VisitResponse visitResponse = new VisitResponseMapper().map(visit);

        assertEquals(visitName, visitResponse.getName());
        assertEquals(typeOfVisit, visitResponse.getTypeOfVisit());
        assertEquals(now, visitResponse.getVisitDate());
        assertTrue(visitResponse.isMissed());
        assertEquals("value", visitResponse.getVisitData().get("key"));
        assertEquals(now, visitResponse.getOriginalAppointmentDueDate());
        assertEquals(now, visitResponse.getAppointmentDueDate());
        assertNull(visitResponse.getAppointmentConfirmDate());
    }
}