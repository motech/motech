package org.motechproject.server.web.form;

import java.net.URI;

public class StartupForm {
    private String language;
    private String databaseUrl;
    private String queueUrl;
    private String schedulerUrl;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public String getDatabasePort() {
        return String.valueOf(URI.create(databaseUrl).getPort());
    }

    public String getDatabaseHost() {
        return URI.create(databaseUrl).getHost();
    }

    public void setDatabaseUrl(final String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getQueueUrl() {
        return queueUrl;
    }

    public void setQueueUrl(final String queueUrl) {
        this.queueUrl = queueUrl;
    }

    public String getSchedulerUrl() {
        return schedulerUrl;
    }

    public void setSchedulerUrl(final String schedulerUrl) {
        this.schedulerUrl = schedulerUrl;
    }
}
