package org.motechproject.scheduler.tasks;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.motechproject.commons.sql.util.Drivers;
import org.motechproject.scheduler.constants.SchedulerConstants;
import org.motechproject.tasks.service.DynamicChannelProvider;
import org.motechproject.tasks.domain.mds.channel.EventParameter;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;
import org.motechproject.tasks.exception.TriggerRetrievalException;
import org.quartz.JobDataMap;
import org.quartz.utils.DBConnectionManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Scheduler module implementation of the {@link DynamicChannelProvider} interface.
 */
public class SchedulerChannelProvider implements DynamicChannelProvider {

    private static final String DB_DRIVER = "org.quartz.dataSource.motechDS.driver";
    private static final String DATA_SOURCE = "org.quartz.jobStore.dataSource";
    private static final String QRTZ_JOB_DETAILS = "QRTZ_JOB_DETAILS";
    private static final String JOB_DESCRIPTION = "DESCRIPTION";
    private static final String JOB_DATA = "JOB_DATA";
    private static final String JOB_NAME = "JOB_NAME";

    private Properties sqlProperties;

    public SchedulerChannelProvider(Properties sqlProperties) {
        this.sqlProperties = sqlProperties;
    }

    @Override
    public List<TriggerEvent> getTriggers(int page, int pageSize) {

        try (Connection conn = DBConnectionManager.getInstance().getConnection(sqlProperties.getProperty(DATA_SOURCE));
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(buildGetTriggersQuery(page, pageSize));

            List<TriggerEvent> triggers = new ArrayList<>();
            while (rs.next()) {
                try (InputStream is = new ByteArrayInputStream(rs.getBytes(JOB_DATA));
                     ObjectInputStream ois = new ObjectInputStream(is)) {
                    JobDataMap dataMap = (JobDataMap) ois.readObject();

                    List<EventParameter> parameters = new ArrayList<>();
                    parameters.add(new EventParameter("scheduler.jobId", MotechSchedulerService.JOB_ID_KEY));

                    triggers.add(new TriggerEvent(
                            "Job: " + rs.getString(JOB_NAME),
                            rs.getString(JOB_NAME),
                            rs.getString(JOB_DESCRIPTION),
                            parameters,
                            dataMap.getString(SchedulerConstants.EVENT_TYPE_KEY_NAME)
                    ));
                }
            }

            return triggers;
        } catch (SQLException|IOException|ClassNotFoundException e) {
            throw new TriggerRetrievalException("Couldn't retrieve triggers for Scheduler channel", e);
        }
    }

    @Override
    public TriggerEvent getTrigger(TaskTriggerInformation info) {
        try (Connection conn = DBConnectionManager.getInstance().getConnection(sqlProperties.getProperty(DATA_SOURCE));
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(buildGetTriggerQuery(info.getSubject()));

            if (rs.next()) {
                try (InputStream is = new ByteArrayInputStream(rs.getBytes(JOB_DATA));
                     ObjectInputStream ois = new ObjectInputStream(is)) {
                    JobDataMap dataMap = (JobDataMap) ois.readObject();

                    List<EventParameter> parameters = new ArrayList<>();
                    parameters.add(new EventParameter("scheduler.jobId", MotechSchedulerService.JOB_ID_KEY));

                    return new TriggerEvent(
                            "Job: " + rs.getString(JOB_NAME),
                            rs.getString(JOB_NAME),
                            rs.getString(JOB_DESCRIPTION),
                            parameters,
                            dataMap.getString(SchedulerConstants.EVENT_TYPE_KEY_NAME)
                    );
                }
            }

            return null;
        } catch (SQLException|IOException|ClassNotFoundException e) {
            throw new TriggerRetrievalException("Couldn't retrieve triggers for Scheduler channel", e);
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

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof SchedulerChannelProvider)) {
            return false;
        }

        SchedulerChannelProvider other = (SchedulerChannelProvider) o;

        return ObjectUtils.equals(sqlProperties, other.sqlProperties);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(sqlProperties)
                .toHashCode();
    }

    private String buildGetTriggersQuery(int page, int pageSize) {

        boolean isPostgres = sqlProperties.getProperty(DB_DRIVER)
                .equals(Drivers.POSTGRESQL_DRIVER);

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append(getCorrectName(JOB_NAME, isPostgres));
        sb.append(", ");
        sb.append(getCorrectName(JOB_DESCRIPTION, isPostgres));
        sb.append(", ");
        sb.append(getCorrectName(JOB_DATA, isPostgres));
        sb.append(" FROM ");
        sb.append(getCorrectName(QRTZ_JOB_DETAILS, isPostgres));
        sb.append(" LIMIT ");
        sb.append(pageSize);
        sb.append(" OFFSET ");
        sb.append((page - 1) * pageSize);

        return sb.toString();
    }

    private String buildGetTriggerQuery(String subject) {

        boolean isPostgres = sqlProperties.getProperty(DB_DRIVER)
                .equals(Drivers.POSTGRESQL_DRIVER);

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append(getCorrectName(JOB_NAME, isPostgres));
        sb.append(", ");
        sb.append(getCorrectName(JOB_DESCRIPTION, isPostgres));
        sb.append(", ");
        sb.append(getCorrectName(JOB_DATA, isPostgres));
        sb.append(" FROM ");
        sb.append(getCorrectName(QRTZ_JOB_DETAILS, isPostgres));
        sb.append(" WHERE ");
        sb.append(getCorrectName(JOB_NAME, isPostgres));
        sb.append(" = \'");
        sb.append(subject);
        sb.append("\'");

        return sb.toString();
    }

    private String buildCountTriggersQuery() {

        boolean isPostgres = sqlProperties.getProperty(DB_DRIVER)
                .equals(Drivers.POSTGRESQL_DRIVER);

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT COUNT(*) FROM ");
        sb.append(getCorrectName(QRTZ_JOB_DETAILS, isPostgres));

        return sb.toString();
    }

    private String getCorrectName(String string, boolean isPostgres) {
        return isPostgres ? "\"" + string.toLowerCase() + "\"" : string;
    }
}
