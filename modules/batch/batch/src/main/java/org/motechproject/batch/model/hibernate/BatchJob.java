package org.motechproject.batch.model.hibernate;

// Generated Apr 11, 2014 10:49:43 AM by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonAutoDetect;

/**
 * BatchJob generated by hbm2java
 */
@Entity
@Table(name = "batch_job", schema = "batch")
public class BatchJob implements java.io.Serializable {

	private Long jobId;
	private BatchJobStatus batchJobStatus;
	private String jobName;
	private String cronExpression;
	private Date createTime;
	private Date lastUpdated;
	private String createdBy;
	private String lastUpdatedBy;
	private Set<BatchJobConfigurationHistory> batchJobConfigurationHistories = new HashSet<BatchJobConfigurationHistory>(
			0);
	private Set<BatchJobParameters> batchJobParameterses = new HashSet<BatchJobParameters>(
			0);

	public BatchJob() {
	}

	public BatchJob(Long jobId, BatchJobStatus batchJobStatus, String jobName) {
		this.jobId = jobId;
		this.batchJobStatus = batchJobStatus;
		this.jobName = jobName;
	}

	public BatchJob(Long jobId, BatchJobStatus batchJobStatus, String jobName,
			String cronExpression, Date createTime, Date lastUpdated,
			String createdBy, String lastUpdatedBy,
			Set<BatchJobConfigurationHistory> batchJobConfigurationHistories,
			Set<BatchJobParameters> batchJobParameterses) {
		this.jobId = jobId;
		this.batchJobStatus = batchJobStatus;
		this.jobName = jobName;
		this.cronExpression = cronExpression;
		this.createTime = createTime;
		this.lastUpdated = lastUpdated;
		this.createdBy = createdBy;
		this.lastUpdatedBy = lastUpdatedBy;
		this.batchJobConfigurationHistories = batchJobConfigurationHistories;
		this.batchJobParameterses = batchJobParameterses;
	}

	@Id
	@Column(name = "job_id", unique = true, nullable = false)
	public Long getJobId() {
		return this.jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	@ManyToOne
	@JoinColumn(name = "status_id", nullable = false)
	public BatchJobStatus getBatchJobStatus() {
		return this.batchJobStatus;
	}

	public void setBatchJobStatus(BatchJobStatus batchJobStatus) {
		this.batchJobStatus = batchJobStatus;
	}

	@Column(name = "job_name", nullable = false, length = 100)
	public String getJobName() {
		return this.jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@Column(name = "cron_expression", length = 100)
	public String getCronExpression() {
		return this.cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 29)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_updated", length = 29)
	public Date getLastUpdated() {
		return this.lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Column(name = "created_by", length = 15)
	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Column(name = "last_updated_by", length = 15)
	public String getLastUpdatedBy() {
		return this.lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "batchJob")
	public Set<BatchJobConfigurationHistory> getBatchJobConfigurationHistories() {
		return this.batchJobConfigurationHistories;
	}

	public void setBatchJobConfigurationHistories(
			Set<BatchJobConfigurationHistory> batchJobConfigurationHistories) {
		this.batchJobConfigurationHistories = batchJobConfigurationHistories;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "batchJob")
	public Set<BatchJobParameters> getBatchJobParameterses() {
		return this.batchJobParameterses;
	}

	public void setBatchJobParameterses(
			Set<BatchJobParameters> batchJobParameterses) {
		this.batchJobParameterses = batchJobParameterses;
	}

}
