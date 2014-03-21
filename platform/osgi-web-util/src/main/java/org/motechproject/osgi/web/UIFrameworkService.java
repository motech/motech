package org.motechproject.osgi.web;

import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.Map;

/**
 * Service responsible for managing the interface. Provides methods for registering/un-registering modules.
 * All modules are represented by {@link ModuleRegistrationData} objects, either registered directly through this
 * service or automatically by exposing it in their spring context. This service also allows manipulation of module
 * state, by marking given modules as requiring attention on the UI.
 */
public interface UIFrameworkService {
    String MODULES_WITH_SUBMENU = "individuals";
    String MODULES_WITHOUT_SUBMENU = "links";
    String MODULES_WITHOUT_UI = "withoutUI";

    void registerModule(ModuleRegistrationData module);

    void unregisterModule(String moduleName);

    Map<String, Collection<ModuleRegistrationData>> getRegisteredModules();

    ModuleRegistrationData getModuleData(String moduleName);

    ModuleRegistrationData getModuleDataByBundle(Bundle bundle);

    ModuleRegistrationData getModuleDataByAngular(String angularModule);

    boolean isModuleRegistered(String moduleName);

    void moduleNeedsAttention(String moduleName, String message);

    void moduleNeedsAttention(String moduleName, String submenu, String message);

    void moduleBackToNormal(String moduleName);

    void moduleBackToNormal(String moduleName, String submenu);
}
