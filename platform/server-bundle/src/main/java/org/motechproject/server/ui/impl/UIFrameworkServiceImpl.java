package org.motechproject.server.ui.impl;

import org.motechproject.server.ui.ModuleRegistrationData;
import org.motechproject.server.ui.UIFrameworkService;
import org.motechproject.server.ui.ex.AlreadyRegisteredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;

@Service("uiFrameworkService")
public class UIFrameworkServiceImpl implements UIFrameworkService {

    private static final Logger LOG = LoggerFactory.getLogger(UIFrameworkServiceImpl.class);

    private HashMap<String, ModuleRegistrationData> modules = new HashMap<>();

    @Override
    public void registerModule(ModuleRegistrationData module) {
        String moduleName = module.getModuleName();

        if (modules.containsKey(moduleName)) {
            throw new AlreadyRegisteredException("Module already registered");
        }

        modules.put(moduleName, module);
    }

    @Override
    public void unregisterModule(String moduleName) {
        modules.remove(moduleName);
    }

    @Override
    public Collection<ModuleRegistrationData> getRegisteredModules() {
        return modules.values();
    }

    @Override
    public ModuleRegistrationData getModuleData(String moduleName) {
        return modules.get(moduleName);
    }
}
