package org.motechproject.server.ui;

import org.junit.Test;
import org.motechproject.server.ui.ex.AlreadyRegisteredException;
import org.motechproject.server.ui.impl.UIFrameworkServiceImpl;

import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class UIFrameworkServiceTest {

    UIFrameworkService service = new UIFrameworkServiceImpl();

    ModuleRegistrationData moduleRegistration = moduleRegistration();

    @Test
    public void testRegisterUnregisterModule() {
        // register
        service.registerModule(moduleRegistration);

        Collection<ModuleRegistrationData> result = service.getRegisteredModules();

        assertEquals(1, result.size());
        assertTrue(result.contains(moduleRegistration));

        // unregister
        service.unregisterModule(moduleRegistration.getModuleName());

        result = service.getRegisteredModules();

        assertTrue(result.isEmpty());
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
