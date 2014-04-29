package org.motechproject.hub.repository;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.motechproject.hub.model.hibernate.HubSubscriptionStatus;
import org.springframework.beans.factory.annotation.Autowired;

public class SubscriptionStatusRepository {

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

	public SubscriptionStatusRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public HubSubscriptionStatus load(Integer id) {
		return (HubSubscriptionStatus) getCurrentSession().load(HubSubscriptionStatus.class, id);
	}

	public HubSubscriptionStatus findByStatus(String status) {
		Criteria criteria = getCurrentSession().createCriteria(HubSubscriptionStatus.class);
		criteria.add(Restrictions.eq("subscriptionStatusCode", status));
		return (HubSubscriptionStatus) criteria.uniqueResult();
	}

}
