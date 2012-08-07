package org.motechproject.admin.settings;

import java.util.HashMap;
import java.util.Map;

public final class NameConversionUtil {

    private static Map<String, String> mappings = new HashMap<>();

    static {
        mappings.put("port", "db.port");
        mappings.put("password", "db.password");
        mappings.put("username", "db.username");
        mappings.put("host", "db.host");
        mappings.put("maxConnections", "db.maxConnections");
        mappings.put("connectionTimeout", "db.connectionTimeout");
        mappings.put("socketTimeout", "db.socketTimeout");
    }

    private NameConversionUtil() {
        // static util class
    }

    public static String convertName(String name) {
        String result = mappings.get(name);
        return (result == null ? name : result);
    }
}
