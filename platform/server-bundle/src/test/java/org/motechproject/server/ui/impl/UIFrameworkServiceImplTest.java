package org.motechproject.server.ui.impl;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.service.UIFrameworkService;
import org.motechproject.osgi.web.util.ModuleRegistrations;
import org.motechproject.server.ui.ex.AlreadyRegisteredException;
import org.motechproject.server.ui.impl.UIFrameworkServiceImpl;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class UIFrameworkServiceImplTest {

    UIFrameworkService service = new UIFrameworkServiceImpl();

    ModuleRegistrationData moduleRegistration = moduleRegistration();

    @Test
    public void testRegisterUnregisterModule() {
        // register
        service.registerModule(moduleRegistration);

       ModuleRegistrations result = service.getRegisteredModules();

        assertTrue(result.getModulesWithSubMenu().isEmpty());
        assertTrue(result.getModulesWithoutSubmenu().contains(moduleRegistration));

        // unregister
        service.unregisterModule(moduleRegistration.getModuleName());

        result = service.getRegisteredModules();

        assertTrue(result.getModulesWithSubMenu().isEmpty());
        assertTrue(result.getModulesWithSubMenu().isEmpty());
    }

    @Test(expected = AlreadyRegisteredException.class)
    public void testDoubleRegistration() {
        service.registerModule(moduleRegistration());
        service.registerModule(moduleRegistration());
    }

    @Test
    public void shouldCheckIfModuleRegistered() {
        UIFrameworkServiceImpl uiFrameworkService = new UIFrameworkServiceImpl();
        ModuleRegistrationData testModule = new ModuleRegistrationData("test-module", "http://goo.gl");

        assertFalse(uiFrameworkService.isModuleRegistered("test-module"));

        uiFrameworkService.registerModule(testModule);

        Assert.assertTrue(uiFrameworkService.isModuleRegistered("test-module"));

    }

    @Test
    public void shouldSetAndUnsetAttentionNeededFlag() {
        UIFrameworkServiceImpl uiFrameworkService = new UIFrameworkServiceImpl();
        ModuleRegistrationData testModule = new ModuleRegistrationData("test-module", "http://goo.gl");
        uiFrameworkService.registerModule(testModule);

        uiFrameworkService.moduleNeedsAttention("test-module", "test msg");
        Assert.assertTrue(uiFrameworkService.getModuleData("test-module").isNeedsAttention());
        Assert.assertEquals(uiFrameworkService.getModuleData("test-module").getCriticalMessage(), "test msg");

        uiFrameworkService.moduleBackToNormal("test-module");
        Assert.assertFalse(uiFrameworkService.getModuleData("test-module").isNeedsAttention());
        Assert.assertTrue(StringUtils.isBlank(uiFrameworkService.getModuleData("test-module").getCriticalMessage()));
    }

    @Test
    public void shouldIgnoreNonExistentModules() {
        UIFrameworkServiceImpl uiFrameworkService = new UIFrameworkServiceImpl();
        uiFrameworkService.moduleNeedsAttention("nonexistent", "test msg");
        uiFrameworkService.moduleBackToNormal("nonexistent");
    }

    private ModuleRegistrationData moduleRegistration() {
        ModuleRegistrationData registration = new ModuleRegistrationData();
        registration.setModuleName("demo");
        registration.setUrl("url");
        return registration;
    }
}
