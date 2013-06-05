package org.motechproject.osgi.web;

import org.osgi.framework.Bundle;

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

    ModuleRegistrationData getModuleDataByBundle(Bundle bundle);

    boolean isModuleRegistered(String moduleName);

    void moduleNeedsAttention(String moduleName, String message);

    void moduleNeedsAttention(String moduleName, String submenu, String message);

    void moduleBackToNormal(String moduleName);

    void moduleBackToNormal(String moduleName, String submenu);
}
