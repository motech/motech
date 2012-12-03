package org.motechproject.server.bundle;

import java.util.HashMap;
import java.util.Map;

public class Activator extends org.motechproject.osgi.web.Activator {
    protected Map<String, String> resourceMappings() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("/server/resources", "/webapp/resources");
        return mapping;
    }
}
