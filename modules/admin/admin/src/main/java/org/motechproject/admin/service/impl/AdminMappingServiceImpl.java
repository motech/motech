package org.motechproject.admin.service.impl;

import org.ektorp.CouchDbConnector;
import org.motechproject.admin.domain.AdminMapping;
import org.motechproject.admin.ex.NoDbException;
import org.motechproject.admin.repository.AllAdminMappings;
import org.motechproject.admin.service.AdminMappingService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("adminMappingService")
public class AdminMappingServiceImpl implements AdminMappingService {

    private static final Logger LOG = LoggerFactory.getLogger(AdminMappingServiceImpl.class);
    private static final String GRAPHITE = "graphite";

    @Autowired
    private PlatformSettingsService platformSettingsService;

    @Autowired(required = false)
    private AllAdminMappings allAdminMappings;

    @Override
    public void registerMapping(String bundleName, String url) {
        String finalUrl = url;

        if (bundleName == null) {
            throw new IllegalArgumentException("Bundle name cannot be null");
        } else if (url == null) {
            throw new IllegalArgumentException("URL cannot be null");
        } else if (url.startsWith("//")) {
            finalUrl = url.substring(1);
        }

        AdminMapping mapping = new AdminMapping(bundleName, finalUrl);
        if (getAllAdminMappings() == null) {
            throw new NoDbException("Can't map because of no DB connection");
        } else {
            allAdminMappings.addOrUpdate(mapping);
        }
    }

    @Override
    public void unregisterMapping(String bundleName) {
        if (getAllAdminMappings() == null) {
            throw new NoDbException("Can't map because of no DB connection");
        } else {
            allAdminMappings.removeByBundleName(bundleName);
        }
    }

    @Override
    public Map<String, String> getAllMappings() {
        Map<String, String> result = new HashMap<>();
        if (getAllAdminMappings() != null) {
            List<AdminMapping> mappings = allAdminMappings.getAll();
            for (AdminMapping mapping : mappings) {
                result.put(mapping.getBundleName(), mapping.getDestination());
            }
        } else {
            LOG.error("Can't retrieve mappings because of no DB connection");
        }
        return result;
    }

    @Override
    public void registerGraphiteUrl(String url) {
        registerMapping(GRAPHITE, url);
    }

    @Override
    public String getGraphiteUrl() {
        return getAllMappings().get(GRAPHITE);
    }

    private AllAdminMappings getAllAdminMappings() {
        if (allAdminMappings == null) {
            try {
                CouchDbConnector connector = platformSettingsService.getCouchConnector("motech-admin");
                allAdminMappings = new AllAdminMappings(connector);
            } catch (RuntimeException e) {
                LOG.error("No db connection");
            }
        }
        return allAdminMappings;
    }
}
