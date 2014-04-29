package org.motechproject.hub.repository;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.motechproject.hub.model.hibernate.HubPublisherTransaction;
import org.motechproject.hub.model.hibernate.HubTopic;

/**
 * This is a tests class to test the PublisherTransactionRepository class
 * @author Anuranjan
 *
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class PublisherTransactionRepositoryTest {
	
	@Mock
	private SessionFactory sessionFactory;
	
	@Mock
	private Session session;
	
	@Mock
	private SQLQuery query;
	
	@Mock
	private Criteria criteria;

	@InjectMocks private PublisherTransactionRepository publisherTransactionRepo = new PublisherTransactionRepository(sessionFactory);
	
	private HubPublisherTransaction hubPublisherTransaction;
	
	private HubTopic hubTopic;
	
	private static final String SEQUENCE = "hub.hub_publisher_transaction_publisher_transaction_id_seq";
	
	@Before
	public void setUp() {
		hubPublisherTransaction = new HubPublisherTransaction();
		hubPublisherTransaction.setPublisherTransactionId(1);

		hubTopic = new HubTopic();
		hubTopic.setTopicId(1);
		hubTopic.setTopicUrl("topic_url");
		
		hubPublisherTransaction.setHubTopic(hubTopic);
		
		publisherTransactionRepo.setSessionFactory(sessionFactory);
		
		when(publisherTransactionRepo.getSessionFactory().getCurrentSession()).thenReturn(session);
		when(session.createSQLQuery("select nextval('" + SEQUENCE + "')")).thenReturn(query);
		when(query.uniqueResult()).thenReturn("1");
		when(session.load((Class<HubPublisherTransaction>) any(), anyInt())).thenReturn(hubPublisherTransaction);
		when(session.createCriteria((Class<HubPublisherTransaction>) any())).thenReturn(criteria);
		when(criteria.uniqueResult()).thenReturn(hubPublisherTransaction);
	}
	
	/**
	 * Tests the method to load the next primary key value
	 */
	@Test
	public void getNextKeyTest() {
		long retVal = publisherTransactionRepo.getNextKey();
		
		assertNotNull(retVal);
		assertEquals(retVal, 1);
		
		verify(sessionFactory).getCurrentSession();
		verify(session).createSQLQuery("select nextval('" + SEQUENCE + "')");
		verify(query).uniqueResult();
	}
	
	/**
	 * Tests the method to load the <code>HubPublisherTransaction</code> Object given the primary key id
	 */
	@Test
	public void loadTest() {
		HubPublisherTransaction retVal = publisherTransactionRepo.load(1);
		
		assertNotNull(retVal);
		assertEquals(1, retVal.getPublisherTransactionId());
		
		HubTopic hubTopic = retVal.getHubTopic();
		assertNotNull(hubTopic);
		assertEquals(1, hubTopic.getTopicId());
		assertEquals("topic_url", hubTopic.getTopicUrl());
		
		verify(sessionFactory).getCurrentSession();
		verify(session).load((Class<HubPublisherTransaction>) any(), anyInt());
	}
	
	/**
	 * Tests the method to create a new record / update an existing record for <code>HubPublisherTransaction</code>
	 */
	@Test
	public void saveOrUpdateTest() {
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				HubPublisherTransaction hubPublisherTransaction = (HubPublisherTransaction) args[0];
				
				assertNotNull(hubPublisherTransaction);
				assertEquals(1, hubPublisherTransaction.getPublisherTransactionId());
				
				HubTopic hubTopic = hubPublisherTransaction.getHubTopic();
				assertNotNull(hubTopic);
				assertEquals(1, hubTopic.getTopicId());
				assertEquals("topic_url", hubTopic.getTopicUrl());
				return null;
			}
		}).when(session).saveOrUpdate(hubPublisherTransaction);
		
		publisherTransactionRepo.saveOrUpdate(hubPublisherTransaction);
		
		verify(sessionFactory).getCurrentSession();
		verify(session).saveOrUpdate(hubPublisherTransaction);
	}
	
	/**
	 * Tests the method to set the audit fields for the entity <code>HubPublisherTransaction</code>
	 */
	@Test
	public void setAuditFields() {
		
		assertNotNull(hubPublisherTransaction);
		assertNull(hubPublisherTransaction.getCreatedBy());
		assertNull(hubPublisherTransaction.getCreateTime());
		assertNull(hubPublisherTransaction.getLastUpdated());
		assertNull(hubPublisherTransaction.getLastUpdatedBy());
		
		publisherTransactionRepo.setAuditFields(hubPublisherTransaction);
		
		assertNotNull(hubPublisherTransaction);
		assertNotNull(hubPublisherTransaction.getCreatedBy());
		assertNotNull(hubPublisherTransaction.getCreateTime());
		assertNotNull(hubPublisherTransaction.getLastUpdated());
		assertNotNull(hubPublisherTransaction.getLastUpdatedBy());
	}
	
}
