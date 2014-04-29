package org.motechproject.batch.repository;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.motechproject.batch.exception.ApplicationErrors;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.model.hibernate.BatchJob;
import org.motechproject.batch.model.hibernate.BatchJobExecutionParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * performs database query related to batch jobs and batch jobs history
 * @author Naveen
 *
 */
@SuppressWarnings("unchecked")
@Repository
public class JobRepository implements BaseRepository {
	
	private static final String SEQUENCE = "batch.batch_job_job_id_seq";
	private SessionFactory sessionFactory;
	
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Autowired
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public JobRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * get list of the scheduled jobs from database
	 * @return list of <code>BatchJob</code>
	 * @throws BatchException 
	 */
	public List<BatchJob> getListOfJobs() throws BatchException {
		Criteria criteria=null;
		List<BatchJob> batchJobsList = null;
		try {
			criteria = getCurrentSession().createCriteria(BatchJob.class);
			
			batchJobsList = (List<BatchJob>)criteria.list();
		}
		catch(HibernateException e){
			throw new BatchException(ApplicationErrors.DATABASE_OPERATION_FAILED, e.getMessage());
		}
		
		
		return batchJobsList;
	}
	
	
	/**
	 * get history of execution of particular job as list
	 * @param jobName name of the job for which execution history is to be retrieved
	 * @return list of execution parameters
	 */

	public List<BatchJobExecutionParams> getJobExecutionHistory(String jobName)throws BatchException {
		Criteria criteria = getCurrentSession().createCriteria(BatchJobExecutionParams.class)
							.createAlias("batchJobExecution", "batchJobExecution")
							.createAlias("batchJobExecution.batchJobInstance", "batchJobInstance");
		criteria.add(Restrictions.eq("batchJobInstance.jobName",jobName));
		
		List<BatchJobExecutionParams> batchJobExecutionParamList = null;
		try{
			batchJobExecutionParamList = criteria.list();
		}
		catch(HibernateException e){
			throw new BatchException(ApplicationErrors.DATABASE_OPERATION_FAILED, e.getMessage());
		}
		
		return batchJobExecutionParamList;
	}

	
	@Override
	public Long getNextKey() {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
				"select nextval('" + SEQUENCE + "')");
		//TODO try catch
		Long key = Long.parseLong(query.uniqueResult().toString());
		return key;
	}

	@Override
	public void setAuditFields(Object entity) {
		
	}

	/**
	 * Update an existing job or schedule a new job
	 * @param batchJob name of the job to be updated or scheduled
	 */
	public void saveOrUpdate(BatchJob batchJob) {
		
		getCurrentSession().saveOrUpdate(batchJob);
		
		
	}

	/**
	 * return batch job object
	 * @param jobName name of the job for which <code>BatchJob</code> to be returned
	 * @return <code>BatchJob</code>
	 * @throws BatchException 
	 */
	public BatchJob getBatchJob(String jobName) throws BatchException {
		Criteria criteria = null;
		BatchJob batchJob = null;
		
		try{
			criteria = getCurrentSession().createCriteria(BatchJob.class);
			criteria.add(Restrictions.eq("jobName", jobName));
			batchJob = (BatchJob) criteria.uniqueResult();
		}catch(HibernateException e){
			throw new BatchException(ApplicationErrors.DATABASE_OPERATION_FAILED , e.getMessage());
		}
		
		return batchJob;
	}

	public boolean checkBatchJob(String jobName) throws BatchException {
		Criteria criteria = null;
		BatchJob batchJob = null;
		
		try{
			criteria = getCurrentSession().createCriteria(BatchJob.class);
			criteria.add(Restrictions.eq("jobName", jobName));
			batchJob = (BatchJob) criteria.uniqueResult();
		}catch(HibernateException e){
			throw new BatchException(ApplicationErrors.DATABASE_OPERATION_FAILED , e.getMessage());
		}
		
		if(batchJob == null)
			return false;
		else
			return true;
	}
}
