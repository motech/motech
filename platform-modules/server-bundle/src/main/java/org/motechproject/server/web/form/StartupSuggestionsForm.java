package org.motechproject.server.web.form;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StartupSuggestionsForm {
    private List<String> databaseUrls;
    private List<String> queueUrls;
    private List<String> schedulerUrls;

    public StartupSuggestionsForm() {
        databaseUrls = new ArrayList<>();
        queueUrls = new ArrayList<>();
        schedulerUrls = new ArrayList<>();
    }

    public List<String> getDatabaseUrls() {
        return databaseUrls;
    }

    public void addDatabaseSuggestion(final String suggestion) {
        boolean found = false;

        for (String url : databaseUrls) {
            if (url.equals(suggestion)) {
                found = true;
            }
        }

        if (!found && StringUtils.isNotBlank(suggestion)) {
            databaseUrls.add(suggestion);
        }
    }

    public List<String> getQueueUrls() {
        return queueUrls;
    }

    public void addQueueSuggestion(final String suggestion) {
        boolean found = false;

        for (String url : queueUrls) {
            if (url.equals(suggestion)) {
                found = true;
            }
        }

        if (!found && StringUtils.isNotBlank(suggestion)) {
            queueUrls.add(suggestion);
        }
    }

    public List<String> getSchedulerUrls() {
        return schedulerUrls;
    }

    public void addSchedulerSuggestion(final String suggestion) {
        boolean found = false;

        for (String url : schedulerUrls) {
            if (url.equals(suggestion)) {
                found = true;
            }
        }

        if (!found && StringUtils.isNotBlank(suggestion)) {
            schedulerUrls.add(suggestion);
        }
    }
}
