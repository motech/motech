package org.motechproject.mds;

import org.motechproject.mds.dto.EntityDto;

import java.util.ArrayList;
import java.util.List;

public class TestData {

    private static List<EntityDto> entities;

    static {
        entities = new ArrayList<>();

        entities.add(new EntityDto(9001L, "org.motechproject.openmrs.ws.resource.model.Patient", "MOTECH OpenMRS Web Services", "navio"));
        entities.add(new EntityDto(9002L, "org.motechproject.openmrs.ws.resource.model.Person", "MOTECH OpenMRS Web Services", "navio"));
        entities.add(new EntityDto(9003L, "org.motechproject.openmrs.ws.resource.model.Patient", "MOTECH OpenMRS Web Services", "accra"));
        entities.add(new EntityDto(9004L, "org.motechproject.openmrs.ws.resource.model.Person", "MOTECH OpenMRS Web Services", "accra"));
        entities.add(new EntityDto(9005L, "org.motechproject.appointments.api.model.Appointment", "MOTECH Appointments API"));
        entities.add(new EntityDto(9006L, "org.motechproject.ivr.domain.CallDetailRecord", "MOTECH IVR API"));
        entities.add(new EntityDto(9007L, "org.motechproject.mds.entity.Voucher"));
        entities.add(new EntityDto(9008L, "org.motechproject.messagecampaign.domain.campaign.Campaign", "MOTECH Message Campaign"));
    }

    public static List<EntityDto> getEntities() {
        return new ArrayList<>(entities);
    }

    public static EntityDto getEntity(Long id) {
        for (EntityDto entity : entities) {
            if (entity.getId().equals(id)) {
                return entity;
            }
        }

        return null;
    }

    private TestData() {
    }
}
