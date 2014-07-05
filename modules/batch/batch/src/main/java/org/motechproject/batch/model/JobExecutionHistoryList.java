package org.motechproject.batch.model;

import java.util.List;

import org.motechproject.batch.mds.BatchJobExecution;

public class JobExecutionHistoryList {

    private List<BatchJobExecution> jobExecutionHistoryList;

    // Setters and Getters for the fields

    public List<BatchJobExecution> getJobExecutionHistoryList() {
        return jobExecutionHistoryList;
    }

    public void setJobExecutionHistoryList(
            List<BatchJobExecution> jobExecutionHistoryList) {
        this.jobExecutionHistoryList = jobExecutionHistoryList;
    }
}
