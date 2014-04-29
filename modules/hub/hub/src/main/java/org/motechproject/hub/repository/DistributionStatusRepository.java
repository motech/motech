package org.motechproject.hub.repository;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.motechproject.hub.model.hibernate.HubDistributionStatus;
import org.motechproject.hub.model.hibernate.HubTopic;
import org.springframework.beans.factory.annotation.Autowired;

public class DistributionStatusRepository {

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

	public DistributionStatusRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public HubDistributionStatus load(Integer id) {
		return (HubDistributionStatus) getCurrentSession().load(HubDistributionStatus.class, id);
	}

	public HubDistributionStatus findByStatus(String status) {
		Criteria criteria = getCurrentSession().createCriteria(HubTopic.class);
		criteria.add(Restrictions.eq("distributionStatusCode", status));
		return (HubDistributionStatus) criteria.uniqueResult();
	}

}
