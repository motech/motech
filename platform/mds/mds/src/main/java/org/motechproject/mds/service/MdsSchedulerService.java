package org.motechproject.mds.service;

public interface MdsSchedulerService {

    void unscheduleRepeatingJob();

    void scheduleRepeatingJob(long interval);
}
