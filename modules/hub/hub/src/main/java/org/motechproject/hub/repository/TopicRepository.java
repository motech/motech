package org.motechproject.hub.repository;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.motechproject.hub.model.hibernate.HubTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TopicRepository implements BaseRepository {

	private static final String SEQUENCE = "hub.hub_topic_topic_id_seq";

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

	public TopicRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public TopicRepository() {

	}

	@Override
	public Long getNextKey() {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
				"select nextval('" + SEQUENCE + "')");
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

	@Override
	public void setAuditFields(Object entity) {
		// TODO Auto-generated method stub
	}

}
