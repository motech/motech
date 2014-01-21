package org.motechproject.mds;

import org.motechproject.mds.dto.EntityDto;

import java.util.ArrayList;
import java.util.List;

public class TestData {

    private static List<EntityDto> entities;

    static {
        entities = new ArrayList<>();

        entities.add(new EntityDto(9001L, "Patient", "OpenMRS", "navio"));
        entities.add(new EntityDto(9002L, "Person", "OpenMRS", "navio"));
        entities.add(new EntityDto(9003L, "Patient", "OpenMRS", "accra"));
        entities.add(new EntityDto(9004L, "Person", "OpenMRS", "accra"));
        entities.add(new EntityDto(9005L, "Appointments", "Appointments"));
        entities.add(new EntityDto(9006L, "Call Log Item", "IVR"));
        entities.add(new EntityDto(9007L, "Voucher"));
        entities.add(new EntityDto(9008L, "Campaign", "Message Campaign"));
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
