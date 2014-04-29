package org.motechproject.hub.repository;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.motechproject.hub.exception.ApplicationErrors;
import org.motechproject.hub.exception.HubException;
import org.motechproject.hub.model.hibernate.HubSubscription;
import org.motechproject.hub.util.HubUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SubscriptionRepository {

	private static final String SEQUENCE = "hub.hub_subscription_subscription_id_seq";

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

	public SubscriptionRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Long getNextKey() {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select nextval('" + SEQUENCE + "')");
		Long key = Long.parseLong(query.uniqueResult().toString());
		return key;
	}
	
	public HubSubscription load(Integer id) {
		return (HubSubscription) getCurrentSession().load(HubSubscription.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<HubSubscription> findByTopicUrl(String topicUrl) {
		Criteria criteria = getCurrentSession().createCriteria(HubSubscription.class).createAlias("hubTopic", "ht");
		criteria.add(Restrictions.eq("ht.topicUrl", topicUrl));
		return (List<HubSubscription>) criteria.list();
	}

	public HubSubscription findByCallbackUrlAndTopicUrl(String callbackUrl, String topic) {
		Criteria criteria = getCurrentSession().createCriteria(HubSubscription.class).createAlias("hubTopic", "ht");
		criteria.add(Restrictions.eq("callbackUrl", callbackUrl));
		criteria.add(Restrictions.eq("ht.topicUrl", topic));
		return (HubSubscription) criteria.uniqueResult();
	}

	public void saveOrUpdate(HubSubscription entity) {
		getCurrentSession().saveOrUpdate(entity);
	}
	
	public void delete(HubSubscription hubSubscription) throws HubException {
		Criteria criteria = getCurrentSession().createCriteria(HubSubscription.class).createAlias("hubTopic", "ht");
		criteria.add(Restrictions.eq("callbackUrl", hubSubscription.getCallbackUrl()));
		criteria.add(Restrictions.eq("ht.topicUrl", hubSubscription.getHubTopic().getTopicUrl()));
		HubSubscription entity = (HubSubscription) criteria.uniqueResult();
		if (entity == null) {
			throw new HubException(ApplicationErrors.SUBSCRIPTION_NOT_FOUND);
		}
		getCurrentSession().delete(entity);
	}

	public void setAuditFields(HubSubscription entity) {
		String host = HubUtils.getNetworkHostName();
		Date dateTime = HubUtils.getCurrentDateTime();
		entity.setCreatedBy(host);
		entity.setCreateTime(dateTime);
		setAuditFieldsForUpdate(entity, host, dateTime);
	}

	public void setAuditFieldsForUpdate(HubSubscription entity, String host, Date dateTime) {
		entity.setLastUpdatedBy(host);
		entity.setLastUpdated(dateTime);
	}

}
