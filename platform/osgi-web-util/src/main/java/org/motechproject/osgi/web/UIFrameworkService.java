package org.motechproject.osgi.web;

import java.util.Collection;
import java.util.Map;

public interface UIFrameworkService {
    String MODULES_WITH_SUBMENU = "individuals";
    String MODULES_WITHOUT_SUBMENU = "links";
    String MODULES_WITHOUT_UI = "withoutUI";

    void registerModule(ModuleRegistrationData module);

    void unregisterModule(String moduleName);

    Map<String, Collection<ModuleRegistrationData>> getRegisteredModules();

    ModuleRegistrationData getModuleData(String moduleName);

    boolean isModuleRegistered(String moduleName);
}
