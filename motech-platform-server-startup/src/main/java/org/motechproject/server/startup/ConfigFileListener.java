package org.motechproject.server.startup;

import org.apache.commons.vfs.FileChangeEvent;
import org.apache.commons.vfs.FileListener;
import org.apache.commons.vfs.FileSystemException;
import org.joda.time.DateTime;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.scheduler.gateway.MotechSchedulerGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class ConfigFileListener implements FileListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigFileListener.class);
    private static final String BASE_SUBJECT = "org.motechproject.server.startup.";

    public static final String FILE_DELETED_EVENT_SUBJECT = BASE_SUBJECT + "fileDeleted";
    public static final String FILE_CHANGED_EVENT_SUBJECT = BASE_SUBJECT + "fileChanged";

    public static final String FILE_URL_PARAM = "fileURL";

    private MotechSchedulerGateway schedulerGateway;

    @Autowired
    public ConfigFileListener(final MotechSchedulerGateway schedulerGateway) {
        this.schedulerGateway = schedulerGateway;
    }

    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) throws Exception {
    }

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) throws Exception {
        LOGGER.error("Config file was deleted. Need new config file...");
        scheduleJob(FILE_DELETED_EVENT_SUBJECT, fileChangeEvent);
    }

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) throws Exception {
        LOGGER.warn("Config file was changed...");
        scheduleJob(FILE_CHANGED_EVENT_SUBJECT, fileChangeEvent);
    }

    private void scheduleJob(final String subject, final FileChangeEvent fileChangeEvent) {
        try {
            MotechEvent motechEvent = createMotechEvent(subject, fileChangeEvent);
            RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, DateTime.now().plusSeconds(5).toDate());
            schedulerGateway.scheduleRunOnceJob(runOnceSchedulableJob);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private MotechEvent createMotechEvent(final String subject, final FileChangeEvent fileChangeEvent) throws FileSystemException {
        Map<String, Object> param = new HashMap<>();
        param.put(FILE_URL_PARAM, fileChangeEvent.getFile().getURL().getPath());

        return new MotechEvent(subject, param);
    }
}
