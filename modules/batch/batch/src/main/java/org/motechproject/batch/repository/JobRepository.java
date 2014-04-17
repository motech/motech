package org.motechproject.batch.repository;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.motechproject.batch.model.hibernate.BatchJob;
import org.motechproject.batch.model.hibernate.BatchJobExecutionParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

	public List<BatchJob> getListOfJobs() {
		// TODO Auto-generated method stub
		Criteria criteria=null;
		try {
		 criteria = getCurrentSession().createCriteria(BatchJob.class);
		} catch (HibernateException e) {
			System.out.println(e);
		}
		
		
		return (List<BatchJob>)criteria.list();
	}
	

	public List<BatchJobExecutionParams> getJobExecutionHistory(String jobName) {
		// TODO Auto-generated method stub
		Session session = getCurrentSession();
		List<BatchJobExecutionParams> paramsList = session.createQuery("from BatchJobExecutionParams parameter where parameter.batchJobExecution.jobName='"+jobName+"'").list(); 
		return paramsList;
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
		// TODO Set the audit fields for batchJob
		
	}

	public void saveOrUpdate(BatchJob batchJob) {
		getCurrentSession().saveOrUpdate(batchJob);
		// TODO Auto-generated method stub
		
	}
}
