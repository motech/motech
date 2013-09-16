package org.motechproject.admin.service;

import java.util.Map;

/**
 * Interface for mapping bundles into specific url
 */

public interface AdminMappingService {

    void registerMapping(String bundleName, String url);

    void unregisterMapping(String bundleName);

    Map<String, String> getAllMappings();
}
