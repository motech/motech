package org.motechproject.batch.repository;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.motechproject.batch.model.hibernate.BatchJobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
	
	@Autowired
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
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

	public void getActiveObject(String string) {
		Criteria criteria = null;
		criteria = getCurrentSession().createCriteria(BatchJobStatus.class);
		
		
		// TODO Auto-generated method stub
		
	}

}
