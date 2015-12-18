package org.motechproject.scheduler.tasks;

import org.motechproject.scheduler.service.MotechSchedulerDatabaseService;
import org.motechproject.tasks.domain.DynamicChannelProvider;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.TriggerRetrievalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
public class SchedulerChannelProvider implements DynamicChannelProvider {

    private MotechSchedulerDatabaseService motechSchedulerDatabaseService;

    @Autowired
    public SchedulerChannelProvider(MotechSchedulerDatabaseService motechSchedulerDatabaseService) {
        this.motechSchedulerDatabaseService = motechSchedulerDatabaseService;
    }

    public List<TriggerEvent> getTriggers(int page, int pageSize) {
        try {
            return motechSchedulerDatabaseService.getTriggers(page, pageSize);
        } catch (SQLException|IOException|ClassNotFoundException e) {
            throw new TriggerRetrievalException("Couldn't retrieve triggers for Scheduler channel", e);
        }
    }

    @Override
    public TriggerEvent getTrigger(String subject) {
        try {
            return motechSchedulerDatabaseService.getTrigger(subject);
        } catch (SQLException|IOException|ClassNotFoundException e) {
            throw new TriggerRetrievalException("Couldn't retrieve trigger for Scheduler channel", e);
        }
    }

    @Override
    public long countTriggers() {
        try {
            return motechSchedulerDatabaseService.countTriggers();
        } catch (SQLException e) {
            throw new TriggerRetrievalException("Couldn't count triggers for Scheduler channel", e);
        }
    }

    @Override
    public boolean validateSubject(String subject) {
        //currently there is no way to validate if a trigger is still present without generating error when job has been
        //executed for the last time (and therefore is no longer present in the database)
        return subject != null;
    }
}
