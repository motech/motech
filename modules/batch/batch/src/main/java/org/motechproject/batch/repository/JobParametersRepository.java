package org.motechproject.batch.repository;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.motechproject.batch.exception.ApplicationErrors;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.model.hibernate.BatchJobParameters;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * get and update of jop parameters
 * @author Naveen
 *
 */
public class JobParametersRepository implements BaseRepository{
	
	private static final String SEQUENCE = "batch_job_parameters_job_parameters_id_seq";
	
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	//@Autowired
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	public JobParametersRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public JobParametersRepository() {

	}
	
	
	/**
	 * get list of parameters related to job
	 * @param jobName name of the job for which parameters is to be fetched
	 * @return list containing all the parameters of a job
	 */
	@SuppressWarnings("unchecked")
	public List<BatchJobParameters> getjobParametersList(String jobName) throws BatchException
	{
		Criteria criteria = getCurrentSession().createCriteria(BatchJobParameters.class).createAlias("batchJob", "batchJob");
		criteria.add(Restrictions.eq("batchJob.jobName", jobName));
		List<BatchJobParameters> jobParametersList = null;
		
		try{
			jobParametersList = criteria.list();
		}catch(HibernateException e){
			throw new BatchException(ApplicationErrors.DATABASE_OPERATION_FAILED, e.getMessage());
		}
		
			return jobParametersList;
	}
	
	@Override
	public Long getNextKey() {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
				"select nextval('" + SEQUENCE + "')");
		Long key = Long.parseLong(query.uniqueResult().toString());
		return key;
	}

	

	@Override
	public void setAuditFields(Object entity) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * save if not exist or update the job parameters for a particular job
	 * @param batchJobParms
	 */
	public void saveOrUpdate(BatchJobParameters batchJobParms) {
		//TODO try/catch
		getCurrentSession().saveOrUpdate(batchJobParms);
		
	}

}
