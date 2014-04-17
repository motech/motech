package org.motechproject.hub.repository;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.motechproject.hub.model.hibernate.HubSubscriberTransaction;
import org.springframework.beans.factory.annotation.Autowired;

public class SubscriberTransactionRepository implements BaseRepository {

	private static final String SEQUENCE = "hub.hub_subscriber_transaction_subscriber_transaction_id_seq";

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

	public SubscriberTransactionRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SubscriberTransactionRepository() {

	}

	@Override
	public Long getNextKey() {
		Query query = sessionFactory.getCurrentSession().createSQLQuery("select nextval('" + SEQUENCE + "')");
		Long key = Long.parseLong(query.uniqueResult().toString());
		return key;
	}

	public HubSubscriberTransaction load(Integer id) {
		return (HubSubscriberTransaction) getCurrentSession().load(HubSubscriberTransaction.class, id);
	}

	public void saveOrUpdate(HubSubscriberTransaction entity) {
		getCurrentSession().saveOrUpdate(entity);
	}

	@Override
	public void setAuditFields(Object entity) {
		// TODO Auto-generated method stub
		
	}

}
