package org.motechproject.hub.repository;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.motechproject.hub.model.hibernate.HubTopic;
import org.motechproject.hub.util.HubUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TopicRepository {

	private static final String SEQUENCE = "hub.hub_topic_topic_id_seq";

	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public TopicRepository() {
		
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	//@Autowired
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	public TopicRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Long getNextKey() {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery("select nextval('" + SEQUENCE + "')");
		Long key = Long.parseLong(query.uniqueResult().toString());
		return key;
	}

	public HubTopic load(Integer id) {
		return (HubTopic) getCurrentSession().load(HubTopic.class, id);
	}

	public HubTopic findByTopicUrl(String topicUrl) {
		Criteria criteria = getCurrentSession().createCriteria(HubTopic.class);
		criteria.add(Restrictions.eq("topicUrl", topicUrl));
		return (HubTopic) criteria.uniqueResult();
	}

	public void saveOrUpdate(HubTopic entity) {
		getCurrentSession().saveOrUpdate(entity);
	}
	
	public void delete(HubTopic entity) {
		getCurrentSession().delete(entity);
	}

	public void setAuditFields(HubTopic entity) {
		String host = HubUtils.getNetworkHostName();
		Date dateTime = HubUtils.getCurrentDateTime();
		entity.setCreatedBy(host);
		entity.setCreateTime(dateTime);
		setAuditFieldsForUpdate(entity, host, dateTime);
	}

	public void setAuditFieldsForUpdate(HubTopic entity, String host, Date dateTime) {
		entity.setLastUpdatedBy(host);
		entity.setLastUpdated(dateTime);
	}

}
