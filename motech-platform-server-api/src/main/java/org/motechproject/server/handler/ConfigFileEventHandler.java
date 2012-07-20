package org.motechproject.server.handler;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.osgi.OsgiListener;
import org.motechproject.server.startup.ConfigFileListener;
import org.motechproject.server.startup.StartupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ConfigFileEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigFileEventHandler.class);
    private static final String MOTECH_SETTINGS_CONF = "motech-settings.conf";

    @MotechListener(subjects = ConfigFileListener.FILE_CHANGED_EVENT_SUBJECT)
    public void fileChanged(final MotechEvent event) {
        File changed = getFile(event);

        if (changed == null) {
            missingParameterError(event);
            return;
        }

        if (changed.getName().equalsIgnoreCase(MOTECH_SETTINGS_CONF)) {
            LOGGER.warn("Config file was changed. Restarting system...");
            OsgiListener.getOsgiService().stopExternalBundles();

            OsgiListener.startSystem();
        }
    }

    @MotechListener(subjects = ConfigFileListener.FILE_DELETED_EVENT_SUBJECT)
    public void fileDeleted(final MotechEvent event) {
        File deleted = getFile(event);

        if (deleted == null) {
            missingParameterError(event);
            return;
        }

        if (deleted.getName().equalsIgnoreCase(MOTECH_SETTINGS_CONF)) {
            LOGGER.warn("Config file was deleted. Stopping bundles...");
            OsgiListener.getOsgiService().stopExternalBundles();
            StartupManager.getInstance().stopMonitor();

            LOGGER.warn("Finding and launching Admin UI bundle to repair errors by user...");

            if (!OsgiListener.getOsgiService().startBundle(OsgiListener.ADMIN_BUNDLE)) {
                LOGGER.error("Admin UI bundle not found...");
                OsgiListener.getOsgiService().stop();
            }
        }
    }

    private void missingParameterError(final MotechEvent event) {
        LOGGER.error(String.format("Can not handle Event: %s. The event is invalid - missing the %s parameter",
                event.getSubject(), ConfigFileListener.FILE_URL_PARAM));
    }

    private File getFile(final MotechEvent event) {
        String path = (String) event.getParameters().get(ConfigFileListener.FILE_URL_PARAM);

        return path == null ? null : new File(path);
    }
}
