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
import org.motechproject.batch.model.hibernate.BatchJobParameters;
import org.motechproject.batch.model.hibernate.BatchJobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Contains database operations on BatchJobStatus
 * @author Naveen
 *
 */
@Repository
public class JobStatusRepository implements BaseRepository{
	
	private static final String SEQUENCE = "batch.batch_job_status_job_status_id_seq";
	private SessionFactory sessionFactory;
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public static String getSequence() {
		return SEQUENCE;
	}
	
	//@Autowired
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	
	public JobStatusRepository() {
		
	}
	
	public JobStatusRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
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

	public void saveOrUpdate(BatchJobStatus batchJobStatus) {
		getCurrentSession().saveOrUpdate(batchJobStatus);
		
		// TODO Auto-generated method stub
		
	}

	/**
	 * get the job status object which is in passed <code>parameter</code> state
	 * @param status parameter
	 * @return <code>BatchJobStatus</code>
	 * @throws BatchException 
	 */
	public BatchJobStatus getActiveObject(String status) throws BatchException {
		Criteria criteria = null;
		criteria = getCurrentSession().createCriteria(BatchJobStatus.class);
		criteria.add(Restrictions.eq("jobStatusCode", status));
		BatchJobStatus batchJobStatus = null;
		try{
			batchJobStatus = (BatchJobStatus)criteria.uniqueResult();
		}
		catch(HibernateException e){
			throw new BatchException(ApplicationErrors.DATABASE_OPERATION_FAILED,e.getMessage());
		}
		
		return batchJobStatus;
		
		
	}

}
