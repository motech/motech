package org.motechproject.hub.repository;

import java.util.Date;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.motechproject.hub.model.hibernate.HubPublisherTransaction;
import org.motechproject.hub.util.HubUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class PublisherTransactionRepository {

	private static final String SEQUENCE = "hub.hub_publisher_transaction_publisher_transaction_id_seq";

	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public PublisherTransactionRepository() {
		
	}
	//@Autowired
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public PublisherTransactionRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Long getNextKey() {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select nextval('" + SEQUENCE + "')");
		Long key = Long.parseLong(query.uniqueResult().toString());
		return key;
	}
	
	public HubPublisherTransaction load(Integer id) {
		return (HubPublisherTransaction) getCurrentSession().load(HubPublisherTransaction.class, id);
	}

	public void saveOrUpdate(HubPublisherTransaction entity) {
		getCurrentSession().saveOrUpdate(entity);
	}

	public void setAuditFields(HubPublisherTransaction entity) {
		String host = HubUtils.getNetworkHostName();
		Date dateTime = HubUtils.getCurrentDateTime();
		entity.setCreatedBy(host);
		entity.setCreateTime(dateTime);
		setAuditFieldsForUpdate(entity, host, dateTime);
	}

	public void setAuditFieldsForUpdate(HubPublisherTransaction entity, String host, Date dateTime) {
		entity.setLastUpdatedBy(host);
		entity.setLastUpdated(dateTime);
	}

}
