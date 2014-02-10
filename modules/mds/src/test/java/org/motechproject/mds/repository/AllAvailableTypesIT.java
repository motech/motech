package org.motechproject.mds.repository;

import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.domain.AvailableType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AllAvailableTypesIT extends BaseIT {

    @Autowired
    private AllAvailableTypes allAvailableTypes;

    @Test
    public void shouldReturnDefaultData() throws Exception {
        List<AvailableType> availableTypes = allAvailableTypes.retrieveAll();

        assertNotNull(availableTypes);
        assertEquals(availableTypes.size(), 8);

        assertAvailableType(availableTypes, "int");
        assertAvailableType(availableTypes, "str");
        assertAvailableType(availableTypes, "bool");
        assertAvailableType(availableTypes, "time");
        assertAvailableType(availableTypes, "date");
        assertAvailableType(availableTypes, "datetime");
        assertAvailableType(availableTypes, "list");
        assertAvailableType(availableTypes, "decimal");
    }

    private void assertAvailableType(List<AvailableType> availableTypes, String defaultName) {
        AvailableType availableType = retrieveByDefaultName(availableTypes, defaultName);

        assertNotNull("Not found available type with default name: " + defaultName, availableType);
        assertNotNull("Available type should have id", availableType.getId());
        assertNotNull("Available type should be connected with the type", availableType.getType());
        assertEquals(defaultName, availableType.getDefaultName());
    }

    private AvailableType retrieveByDefaultName(List<AvailableType> availableTypes,
                                                String defaultName) {
        AvailableType found = null;

        for (AvailableType availableType : availableTypes) {
            if (availableType.getDefaultName().equalsIgnoreCase(defaultName)) {
                found = availableType;
                break;
            }
        }

        return found;
    }

}
