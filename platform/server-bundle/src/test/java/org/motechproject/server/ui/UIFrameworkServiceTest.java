package org.motechproject.server.ui;

import org.junit.Test;
import org.motechproject.server.ui.ex.AlreadyRegisteredException;
import org.motechproject.server.ui.impl.UIFrameworkServiceImpl;

import java.util.Collection;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.server.ui.UIFrameworkService.MODULES_WITHOUT_SUBMENU;
import static org.motechproject.server.ui.UIFrameworkService.MODULES_WITH_SUBMENU;

public class UIFrameworkServiceTest {

    UIFrameworkService service = new UIFrameworkServiceImpl();

    ModuleRegistrationData moduleRegistration = moduleRegistration();

    @Test
    public void testRegisterUnregisterModule() {
        // register
        service.registerModule(moduleRegistration);

        Map<String, Collection<ModuleRegistrationData>> result = service.getRegisteredModules();

        assertEquals(2, result.size());
        assertTrue(result.containsKey(MODULES_WITH_SUBMENU));
        assertTrue(result.containsKey(MODULES_WITHOUT_SUBMENU));
        assertTrue(result.get(MODULES_WITH_SUBMENU).isEmpty());
        assertTrue(result.get(MODULES_WITHOUT_SUBMENU).contains(moduleRegistration));

        // unregister
        service.unregisterModule(moduleRegistration.getModuleName());

        result = service.getRegisteredModules();

        assertEquals(2, result.size());
        assertTrue(result.containsKey(MODULES_WITH_SUBMENU));
        assertTrue(result.containsKey(MODULES_WITHOUT_SUBMENU));
        assertTrue(result.get(MODULES_WITH_SUBMENU).isEmpty());
        assertTrue(result.get(MODULES_WITHOUT_SUBMENU).isEmpty());
    }

    @Test(expected = AlreadyRegisteredException.class)
    public void testDoubleRegistration() {
        service.registerModule(moduleRegistration());
        service.registerModule(moduleRegistration());
    }

    private ModuleRegistrationData moduleRegistration() {
        ModuleRegistrationData registration = new ModuleRegistrationData();
        registration.setModuleName("demo");
        return registration;
    }
}
