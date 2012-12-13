package org.motechproject.ivr.osgi;

import org.motechproject.ivr.service.IVRService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public final class AllIvrBundles {

    private Map<String, IVRService> modules;

    private static class SingletonHolder {
        public static final AllIvrBundles INSTANCE = new AllIvrBundles();
    }

    public static AllIvrBundles instance() {
        return SingletonHolder.INSTANCE;
    }

    private AllIvrBundles() {
        modules = new HashMap<>();
    }

    public void register(String name, IVRService ivrService) {
        modules.put(name, ivrService);
    }

    public void deRegister(String name) {
        modules.remove(name);
    }

    public List<String> getAll() {
        return new ArrayList<>(modules.keySet());
    }

    public IVRService getIvrService(String name) {
        return modules.get(name);
    }
}
