package org.motechproject.batch.model;

import java.util.List;

public class JobExecutionHistoryListDTO {

   private List<JobExecutionHistoryDTO> jobExecutionHistoryList;

    // Setters and Getters for the fields
    public List<JobExecutionHistoryDTO> getJobExecutionHistoryList() {
        return jobExecutionHistoryList;
    }

    public void setJobExecutionHistoryList(
            List<JobExecutionHistoryDTO> jobExecutionHistoryList) {
        this.jobExecutionHistoryList = jobExecutionHistoryList;
    }

}
