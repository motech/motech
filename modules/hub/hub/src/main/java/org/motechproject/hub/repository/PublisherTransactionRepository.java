package org.motechproject.hub.repository;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.motechproject.hub.model.hibernate.HubPublisherTransaction;
import org.springframework.beans.factory.annotation.Autowired;

public class PublisherTransactionRepository implements BaseRepository {

		private static final String SEQUENCE = "hub.hub_publisher_transaction_publisher_transaction_id_seq";

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

		public PublisherTransactionRepository(SessionFactory sessionFactory) {
			this.sessionFactory = sessionFactory;
		}

		public PublisherTransactionRepository() {

		}

		@Override
		public Long getNextKey() {
			Query query = sessionFactory.getCurrentSession().createSQLQuery("select nextval('" + SEQUENCE + "')");
			Long key = Long.parseLong(query.uniqueResult().toString());
			return key;
		}

		public HubPublisherTransaction load(Integer id) {
			return (HubPublisherTransaction) getCurrentSession().load(HubPublisherTransaction.class, id);
		}

		public void saveOrUpdate(HubPublisherTransaction entity) {
			getCurrentSession().saveOrUpdate(entity);
		}

		@Override
		public void setAuditFields(Object entity) {
			// TODO Auto-generated method stub
			
		}

}
