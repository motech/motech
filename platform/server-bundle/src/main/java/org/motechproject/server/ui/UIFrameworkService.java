package org.motechproject.server.ui;

import java.util.Collection;
import java.util.Map;

public interface UIFrameworkService {
    String MODULES_WITH_SUBMENU = "individuals";
    String MODULES_WITHOUT_SUBMENU = "links";

    void registerModule(ModuleRegistrationData module);

    void unregisterModule(String moduleName);

    Map<String, Collection<ModuleRegistrationData>> getRegisteredModules();

    ModuleRegistrationData getModuleData(String moduleName);
}
