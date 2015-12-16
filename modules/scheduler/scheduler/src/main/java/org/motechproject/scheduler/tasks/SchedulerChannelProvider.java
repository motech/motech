package org.motechproject.scheduler.tasks;

import org.motechproject.commons.sql.util.Drivers;
import org.motechproject.scheduler.constants.SchedulerConstants;
import org.motechproject.tasks.domain.DynamicChannelProvider;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.TriggerRetrievalException;
import org.quartz.JobDataMap;
import org.quartz.utils.DBConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class SchedulerChannelProvider implements DynamicChannelProvider {

    private static final String DATA_SOURCE = "org.quartz.jobStore.dataSource";
    private static final String JOB_NAME = "JOB_NAME";
    private static final String JOB_DESCRIPTION = "DESCRIPTION";
    private static final String JOB_DATA = "JOB_DATA";
    private static final String QRTZ_JOB_DETAILS = "QRTZ_JOB_DETAILS";

    @Autowired
    private Properties sqlProperties;

    public List<TriggerEvent> getTriggers(int page, int pageSize) {
        try (Connection conn = DBConnectionManager.getInstance().getConnection(sqlProperties.getProperty(DATA_SOURCE));
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(buildGetTriggersQuery(page, pageSize));

            List<TriggerEvent> triggers = new ArrayList<>();
            while (rs.next()) {
                try (InputStream is = new ByteArrayInputStream(rs.getBytes(JOB_DATA));
                     ObjectInputStream ois = new ObjectInputStream(is)) {
                    JobDataMap dataMap = (JobDataMap) ois.readObject();

                    triggers.add(new TriggerEvent(
                            "Job: " + rs.getString(JOB_NAME),
                            rs.getString(JOB_NAME),
                            rs.getString(JOB_DESCRIPTION),
                            new ArrayList<>(),
                            dataMap.getString(SchedulerConstants.EVENT_TYPE_KEY_NAME)
                    ));
                }
            }

            return triggers;
        } catch (Exception e) {
            throw new TriggerRetrievalException("Couldn't retrieve triggers for Scheduler channel", e);
        }
    }

    @Override
    public TriggerEvent getTrigger(String subject) {
        try (Connection conn = DBConnectionManager.getInstance().getConnection(sqlProperties.getProperty(DATA_SOURCE));
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(buildGetTriggerQuery(subject));

            if (rs.next()) {
                try (InputStream is = new ByteArrayInputStream(rs.getBytes(JOB_DATA));
                     ObjectInputStream ois = new ObjectInputStream(is)) {

                    JobDataMap dataMap = (JobDataMap) ois.readObject();

                    return new TriggerEvent(
                            "Job: " + rs.getString(JOB_NAME),
                            rs.getString(JOB_NAME),
                            rs.getString(JOB_DESCRIPTION),
                            new ArrayList<>(),
                            dataMap.getString(SchedulerConstants.EVENT_TYPE_KEY_NAME)
                    );
                }
            }

            return null;
        } catch (Exception e) {
            throw new TriggerRetrievalException("Couldn't retrieve trigger for Scheduler channel", e);
        }
    }

    @Override
    public long countTriggers() {
        try (Connection conn = DBConnectionManager.getInstance().getConnection(sqlProperties.getProperty(DATA_SOURCE));
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(buildCountTriggersQuery());

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        } catch (Exception e) {
            throw new TriggerRetrievalException("Couldn't count triggers for Scheduler channel", e);
        }
    }

    @Override
    public boolean validateSubject(String subject) {
        //currently there is no way to validate if a trigger is still present without generating error when job has been
        //executed for the last time (and therefore is no longer present in the database)
        return subject != null;
    }

    private String buildGetTriggersQuery(int page, int pageSize) {

        boolean isPostgres = sqlProperties.getProperty("org.quartz.dataSource.motechDS.driver")
                .equals(Drivers.POSTGRESQL_DRIVER);

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append(enquoteIfPostgres(JOB_NAME, isPostgres));
        sb.append(", ");
        sb.append(enquoteIfPostgres(JOB_DESCRIPTION, isPostgres));
        sb.append(", ");
        sb.append(enquoteIfPostgres(JOB_DATA, isPostgres));
        sb.append(" FROM ");
        sb.append(enquoteIfPostgres(QRTZ_JOB_DETAILS, isPostgres));
        sb.append(" LIMIT ");
        sb.append(pageSize);
        sb.append(" OFFSET ");
        sb.append((page - 1) * pageSize);

        return sb.toString();
    }

    private String buildGetTriggerQuery(String subject) {

        boolean isPostgres = sqlProperties.getProperty("org.quartz.dataSource.motechDS.driver")
                .equals(Drivers.POSTGRESQL_DRIVER);

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append(enquoteIfPostgres(JOB_NAME, isPostgres));
        sb.append(", ");
        sb.append(enquoteIfPostgres(JOB_DESCRIPTION, isPostgres));
        sb.append(", ");
        sb.append(enquoteIfPostgres(JOB_DATA, isPostgres));
        sb.append(" FROM ");
        sb.append(enquoteIfPostgres(QRTZ_JOB_DETAILS, isPostgres));
        sb.append(" WHERE ");
        sb.append(enquoteIfPostgres(JOB_NAME, isPostgres));
        sb.append(" = \'");
        sb.append(subject);
        sb.append("\'");

        return sb.toString();
    }

    private String buildCountTriggersQuery() {

        boolean isPostgres = sqlProperties.getProperty("org.quartz.dataSource.motechDS.driver")
                .equals(Drivers.POSTGRESQL_DRIVER);

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT COUNT(*) FROM ");
        sb.append(enquoteIfPostgres(QRTZ_JOB_DETAILS, isPostgres));

        return sb.toString();
    }

    private String enquoteIfPostgres(String string, boolean isPostgres) {
        return isPostgres ? "\"" + string + "\"" : string;
    }
}
