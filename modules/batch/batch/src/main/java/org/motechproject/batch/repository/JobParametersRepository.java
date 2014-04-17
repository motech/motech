package org.motechproject.batch.repository;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.motechproject.batch.model.hibernate.BatchJobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class JobParametersRepository implements BaseRepository{
	
	private static final String SEQUENCE = "batch_job_parameters_job_parameters_id_seq";
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

	public JobParametersRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public JobParametersRepository() {

	}
	
	@SuppressWarnings("unchecked")
	public List<BatchJobParameters> getjobParametersList(String jobName)
	{
		Criteria criteria = getCurrentSession().createCriteria(BatchJobParameters.class).createAlias("batchJob", "batchJob");
		criteria.add(Restrictions.eq("batchJob.jobName", jobName));
		
		return (List<BatchJobParameters>) criteria.list();
	}
	
	@Override
	public Long getNextKey() {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public void setAuditFields(Object entity) {
		// TODO Auto-generated method stub
		
	}

}
