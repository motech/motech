package org.motechproject.hub.repository;

import java.util.Date;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.motechproject.hub.model.hibernate.HubSubscriberTransaction;
import org.motechproject.hub.util.HubUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class SubscriberTransactionRepository {

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

	public Long getNextKey() {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select nextval('" + SEQUENCE + "')");
		Long key = Long.parseLong(query.uniqueResult().toString());
		return key;
	}

	public HubSubscriberTransaction load(Integer id) {
		return (HubSubscriberTransaction) getCurrentSession().load(HubSubscriberTransaction.class, id);
	}

	public void saveOrUpdate(HubSubscriberTransaction entity) {
		getCurrentSession().saveOrUpdate(entity);
	}

	public void setAuditFields(HubSubscriberTransaction entity) {
		String host = HubUtils.getNetworkHostName();
		Date dateTime = HubUtils.getCurrentDateTime();
		entity.setCreatedBy(host);
		entity.setCreateTime(dateTime);
		setAuditFieldsForUpdate(entity, host, dateTime);
	}

	public void setAuditFieldsForUpdate(HubSubscriberTransaction entity, String host, Date dateTime) {
		entity.setLastUpdatedBy(host);
		entity.setLastUpdated(dateTime);
	}

}
