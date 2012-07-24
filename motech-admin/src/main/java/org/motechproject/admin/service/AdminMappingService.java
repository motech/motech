package org.motechproject.admin.service;

import java.util.Map;

public interface AdminMappingService {

    void registerMapping(String bundleName, String url);

    void unregisterMapping(String bundleName);

    Map<String, String> getAllMappings();
}
