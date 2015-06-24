package org.motechproject.mds.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ClassNameTest {

    @Test
    public void shouldTrimHistoryTrashClassNamesCorrectly() {
        assertEquals("org.mydomain.domain.TestClass",
                ClassName.trimTrashHistorySuffix("org.mydomain.domain.history.TestClass__History"));
        assertEquals("org.mydomain.domain.TestClass",
                ClassName.trimTrashHistorySuffix("org.mydomain.domain.history.TestClass__Trash"));
        assertEquals("org.motechproject.entities.GGGTtest",
                ClassName.trimTrashHistorySuffix("org.motechproject.entities.history.GGGTtest__History"));
        assertEquals("org.motechproject.entities.GGGTtest",
                ClassName.trimTrashHistorySuffix("org.motechproject.entities.history.GGGTtest__Trash"));
    }

    @Test
    public void shouldNotTrimRegularClassNames() {
        assertNull(ClassName.trimTrashHistorySuffix(null));
        assertEquals("", ClassName.trimTrashHistorySuffix(""));
        assertEquals("org.motechproject.domain.Test", ClassName.trimTrashHistorySuffix("org.motechproject.domain.Test"));
        assertEquals("org.motechproject.domain.history.TestHistory",
                ClassName.trimTrashHistorySuffix("org.motechproject.domain.history.TestHistory"));
        assertEquals("org.motechproject.domain.history.TestTrash",
                ClassName.trimTrashHistorySuffix("org.motechproject.domain.history.TestTrash"));
    }

    @Test
    public void shouldRecognizeTrashAndHistoryNames() {
        assertTrue(ClassName.isHistoryClassName("org.motechproject.Patient__History"));
        assertTrue(ClassName.isTrashClassName("org.motechproject.Patient__Trash"));

        assertFalse(ClassName.isTrashClassName("org.motechproject.Patient__History"));
        assertFalse(ClassName.isHistoryClassName("org.motechproject.Patient__Trash"));

        assertFalse(ClassName.isTrashClassName("org.motechproject.Patient"));
        assertFalse(ClassName.isHistoryClassName("org.motechproject.Patient"));
    }

    @Test
    public void shouldParseModuleNamesForRest() {
        assertNull(ClassName.simplifiedModuleName(null));
        assertEquals("", ClassName.simplifiedModuleName(""));
        assertEquals("admin", ClassName.simplifiedModuleName("MOTECH Admin"));
    }

    @Test
    public void shouldGenerateCorrectRestIds() {
        assertEquals("rest-eudeent", ClassName.restId("eudeEnt", null, ""));
        assertEquals("rest-admin-notificationrule", ClassName.restId("NotificationRule", "MOTECH Admin", ""));
        assertEquals("rest-openmrs-accra-patient", ClassName.restId("Patient", "OpenMRS", "accra"));
        assertEquals("rest-email-record", ClassName.restId("Record", "MOTECH Platform Email", null));
    }

    @Test
    public void shouldGenerateCorrectInterfaceNames() {
        //EUDE
        assertEquals("org.motechproject.mds.entity.service.ExampleService", ClassName.getInterfaceName("Example"));
        assertEquals("org.motechproject.mds.entity.service.EntityService", ClassName.getInterfaceName("Entity"));

        //DDE
        assertEquals("org.motechproject.admin.mds.mdsservice.UserService", ClassName.getInterfaceName("org.motechproject.admin.mds.User"));
        assertEquals("org.motechproject.email.domain.mdsservice.EmailService", ClassName.getInterfaceName("org.motechproject.email.domain.Email"));
    }
}
