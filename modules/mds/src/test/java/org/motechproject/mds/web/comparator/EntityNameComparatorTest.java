package org.motechproject.mds.web.comparator;

import org.junit.Test;
import org.motechproject.mds.dto.EntityDto;

import static org.junit.Assert.assertTrue;

public class EntityNameComparatorTest {
    private EntityNameComparator comparator = new EntityNameComparator();

    @Test
    public void shouldCompareTwoEntities() throws Exception {
        EntityDto person = new EntityDto("1", "Person");
        EntityDto appointment = new EntityDto("2", "Appointment");

        assertTrue(comparator.compare(person, appointment) > 0);
        assertTrue(comparator.compare(appointment, person) < 0);
        assertTrue(comparator.compare(person, person) == 0);
        assertTrue(comparator.compare(appointment, appointment) == 0);
    }
}
