package org.motechproject.mds.service.impl;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mds.service.MdsBundleRegenerationService;
import org.motechproject.mds.service.JarGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Default implementation of the <code>MdsBundleRegenerationService</code> interface. It uses the
 * {@link org.motechproject.mds.service.JarGeneratorService} to perform the MDS Entities Bundle regeneration
 * and messages broadcasting for communication with other Motech instances.
 *
 * @see org.motechproject.mds.service.JarGeneratorService
 * @see org.motechproject.event.listener.EventRelay#broadcastEventMessage(org.motechproject.event.MotechEvent)
 * @see org.motechproject.event.listener.annotations.MotechListener
 */
@Service
public class MdsBundleRegenerationServiceImpl implements MdsBundleRegenerationService {

    private static final String REGENERATE_MDS_DATA_BUNDLE =
            "org.motechproject.mds.regenerate_mds_data_bundle";
    private static final String REGENERATE_MDS_DATA_BUNDLE_AFTER_DDE_ENHANCEMENT =
            "org.motechproject.mds.regenerate_mds_data_bundle_after_dde_enhancement";

    private static final String REGENERATE_REQUEST_ID_EVENT_PARAM = "regenerate_request_id";
    private static final String MODULE_NAMES_EVENT_PARAM = "module_names";

    private EventRelay eventRelay;
    private JarGeneratorService jarGeneratorService;
    private final Set<UUID> regenerateRequestIds = Collections.synchronizedSet(new HashSet<UUID>());

    @Override
    public void regenerateMdsDataBundle() {
        MotechEvent event = new MotechEvent(REGENERATE_MDS_DATA_BUNDLE);
        broadcast(event);
        jarGeneratorService.regenerateMdsDataBundle();
    }

    @Override
    public void regenerateMdsDataBundleAfterDdeEnhancement(String... moduleNames) {
        MotechEvent event = new MotechEvent(REGENERATE_MDS_DATA_BUNDLE_AFTER_DDE_ENHANCEMENT);
        event.getParameters().put(MODULE_NAMES_EVENT_PARAM, moduleNames);
        broadcast(event);
        jarGeneratorService.regenerateMdsDataBundleAfterDdeEnhancement(moduleNames);
    }

    @MotechListener(subjects = REGENERATE_MDS_DATA_BUNDLE)
    public void handleMdsDataBundleRegeneration(MotechEvent event) {
        if (!isBroadcastFromThisInstance(event)) {
            jarGeneratorService.regenerateMdsDataBundle();
        }
    }

    @MotechListener(subjects = REGENERATE_MDS_DATA_BUNDLE_AFTER_DDE_ENHANCEMENT)
    public void handleMdsDataBundleRegenerationAfterDdeEnhancement(MotechEvent event) {
        if (!isBroadcastFromThisInstance(event)) {
            String[] moduleNames = (String[]) event.getParameters().get(MODULE_NAMES_EVENT_PARAM);
            jarGeneratorService.regenerateMdsDataBundleAfterDdeEnhancement(moduleNames);
        }
    }

    private boolean isBroadcastFromThisInstance(MotechEvent event) {
        UUID regenerateRequestId = (UUID) event.getParameters().get(REGENERATE_REQUEST_ID_EVENT_PARAM);
        if (null != regenerateRequestId && regenerateRequestIds.contains(regenerateRequestId)) {
            regenerateRequestIds.remove(regenerateRequestId);
            return true;
        } else {
            return false;
        }
    }

    private void broadcast(MotechEvent event) {
        UUID regenerateRequestId = UUID.randomUUID();
        event.getParameters().put(REGENERATE_REQUEST_ID_EVENT_PARAM, regenerateRequestId);
        regenerateRequestIds.add(regenerateRequestId);
        eventRelay.broadcastEventMessage(event);
    }

    @Autowired
    public void setEventRelay(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    @Autowired
    public void setJarGeneratorService(JarGeneratorService jarGeneratorService) {
        this.jarGeneratorService = jarGeneratorService;
    }
}
