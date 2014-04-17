package org.motechproject.batch.model;

import java.util.Date;

import org.motechproject.batch.model.hibernate.BatchJobStatus;

public class BatchJobDTO {
	private long jobId;
	private String jobName;
	private String cronExpression;
	private Date createTime;
	private Date lastUpdated;
	private String createdBy;
	private String lastUpdatedBy;

	
	
	private BatchJobStatus batchJobStatus;
	public long getJobId() {
		return jobId;
	}
	public void setJobId(long jobId) {
		this.jobId = jobId;
	}
	public BatchJobStatus getBatchJobStatus() {
		return batchJobStatus;
	}
	public void setBatchJobStatus(BatchJobStatus batchJobStatus) {
		this.batchJobStatus = batchJobStatus;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	
}
