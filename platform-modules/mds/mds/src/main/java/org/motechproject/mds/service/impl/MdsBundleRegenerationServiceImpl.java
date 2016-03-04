package org.motechproject.mds.service.impl;

import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.MdsBundleRegenerationService;
import org.motechproject.server.osgi.event.OsgiEventProxy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Default implementation of the <code>MdsBundleRegenerationService</code> interface. It uses the
 * {@link org.motechproject.mds.service.JarGeneratorService} to perform the MDS Entities Bundle regeneration
 * and messages broadcasting for communication with other Motech instances. This class uses {@link OsgiEventProxy}
 * to proxy Motech events though OSGi events, in order to avoid a dependency on the event module.
 *
 * @see org.motechproject.mds.service.JarGeneratorService
 */
@Service
public class MdsBundleRegenerationServiceImpl implements MdsBundleRegenerationService, EventHandler {

    private static final String REGENERATE_REQUEST_ID_EVENT_PARAM = "regenerate_request_id";
    private static final String MODULE_NAMES_EVENT_PARAM = "module_names";

    private OsgiEventProxy osgiEventProxy;
    private JarGeneratorService jarGeneratorService;
    private final Set<UUID> regenerateRequestIds = Collections.synchronizedSet(new HashSet<>());
    private EntityService entityService;

    @Override
    public void regenerateMdsDataBundle() {
        broadcast(REGENERATE_MDS_DATA_BUNDLE);
        SchemaHolder schemaHolder = entityService.getSchema();
        jarGeneratorService.regenerateMdsDataBundle(schemaHolder);
    }

    @Override
    public void regenerateMdsDataBundleAfterDdeEnhancement(String... moduleNames) {
        Map<String, Object> params = new HashMap<>();
        params.put(MODULE_NAMES_EVENT_PARAM, moduleNames);

        broadcast(REGENERATE_MDS_DATA_BUNDLE_AFTER_DDE_ENHANCEMENT, params);

        SchemaHolder schemaHolder = entityService.getSchema();
        jarGeneratorService.regenerateMdsDataBundleAfterDdeEnhancement(schemaHolder, moduleNames);
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.getTopic()) {
            case REGENERATE_MDS_DATA_BUNDLE:
                handleMdsDataBundleRegeneration(event);
                break;
            case REGENERATE_MDS_DATA_BUNDLE_AFTER_DDE_ENHANCEMENT:
                handleMdsDataBundleRegenerationAfterDdeEnhancement(event);
                break;
            default:
                throw new IllegalStateException("Received an event with an unknown subject/topic: " + event.getTopic());
        }
    }

    private void handleMdsDataBundleRegeneration(Event event) {
        if (!isBroadcastFromThisInstance(event)) {
            SchemaHolder schemaHolder = entityService.getSchema();
            jarGeneratorService.regenerateMdsDataBundle(schemaHolder);
        }
    }

    private void handleMdsDataBundleRegenerationAfterDdeEnhancement(Event event) {
        if (!isBroadcastFromThisInstance(event)) {
            SchemaHolder schemaHolder = entityService.getSchema();
            String[] moduleNames = (String[]) event.getProperty(MODULE_NAMES_EVENT_PARAM);
            jarGeneratorService.regenerateMdsDataBundleAfterDdeEnhancement(schemaHolder, moduleNames);
        }
    }

    private boolean isBroadcastFromThisInstance(Event event) {
        UUID regenerateRequestId = (UUID) event.getProperty(REGENERATE_REQUEST_ID_EVENT_PARAM);
        if (null != regenerateRequestId && regenerateRequestIds.contains(regenerateRequestId)) {
            regenerateRequestIds.remove(regenerateRequestId);
            return true;
        } else {
            return false;
        }
    }

    private void broadcast(String subject) {
        broadcast(subject, new HashMap<>());
    }

    private void broadcast(String subject, Map<String, Object> params) {
        UUID regenerateRequestId = UUID.randomUUID();
        regenerateRequestIds.add(regenerateRequestId);

        params.put(REGENERATE_REQUEST_ID_EVENT_PARAM, regenerateRequestId);

        osgiEventProxy.broadcastEvent(subject, params, true);
    }

    @Autowired
    public void setOsgiEventProxy(OsgiEventProxy osgiEventProxy) {
        this.osgiEventProxy = osgiEventProxy;
    }

    @Autowired
    public void setJarGeneratorService(JarGeneratorService jarGeneratorService) {
        this.jarGeneratorService = jarGeneratorService;
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }
}
