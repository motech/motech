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
import org.motechproject.hub.model.DistributionStatusLookup;
import org.motechproject.hub.model.SubscriptionStatusLookup;
import org.motechproject.hub.model.hibernate.HubDistributionStatus;
import org.motechproject.hub.model.hibernate.HubSubscriberTransaction;
import org.motechproject.hub.model.hibernate.HubSubscription;
import org.motechproject.hub.model.hibernate.HubSubscriptionStatus;
import org.motechproject.hub.model.hibernate.HubTopic;

/**
 * This is a tests class to test the SubscriberTransactionRepository class
 * @author Anuranjan
 *
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class SubscriberTransactionRepositoryTest {
	
	@Mock
	private SessionFactory sessionFactory;
	
	@Mock
	private Session session;
	
	@Mock
	private SQLQuery query;
	
	@Mock
	private Criteria criteria;

	@InjectMocks private SubscriberTransactionRepository subscriberTransactionRepo = new SubscriberTransactionRepository(sessionFactory);
	
	private HubSubscriberTransaction hubSubscriberTransaction;
	
	private HubDistributionStatus hubDistributionStatus;
	
	private HubSubscription hubSubscription;
	
	private HubTopic hubTopic;
	
	private HubSubscriptionStatus subscriptionStatus;
	
	private static final String SEQUENCE = "hub.hub_subscriber_transaction_subscriber_transaction_id_seq";
	
	@Before
	public void setUp() {
		hubSubscriberTransaction = new HubSubscriberTransaction();
		hubSubscriberTransaction.setSubscriberTransactionId(1);
		hubSubscriberTransaction.setContent("content");
		hubSubscriberTransaction.setContentType("application/xml");

		hubDistributionStatus = new HubDistributionStatus();
		hubDistributionStatus.setDistributionStatusCode(DistributionStatusLookup.SUCCESS.toString());
		hubSubscriberTransaction.setHubDistributionStatus(hubDistributionStatus);
		
		hubTopic = new HubTopic();
		hubTopic.setTopicId(1);
		hubTopic.setTopicUrl("topic_url");
		
		subscriptionStatus = new HubSubscriptionStatus();
		subscriptionStatus.setSubscriptionStatusCode(SubscriptionStatusLookup.ACCEPTED.toString());
		
		hubSubscription = new HubSubscription();
		hubSubscription.setSubscriptionId(1);
		hubSubscription.setHubSubscriptionStatus(subscriptionStatus);
		hubSubscription.setHubTopic(hubTopic);
		hubSubscriberTransaction.setHubSubscription(hubSubscription);
		
		subscriberTransactionRepo.setSessionFactory(sessionFactory);
		
		//when(subscriberTransactionRepo.getSessionFactory().getCurrentSession()).thenReturn(session);
		when(session.createSQLQuery("select nextval('" + SEQUENCE + "')")).thenReturn(query);
		when(query.uniqueResult()).thenReturn("1");
		when(session.load((Class<HubSubscriberTransaction>) any(), anyInt())).thenReturn(hubSubscriberTransaction);
		when(session.createCriteria((Class<HubSubscriberTransaction>) any())).thenReturn(criteria);
		when(criteria.uniqueResult()).thenReturn(hubSubscriberTransaction);
	}
	
	/**
	 * Tests the method to load the next primary key value
	 */
	@Test
	public void getNextKeyTest() {
		long retVal = subscriberTransactionRepo.getNextKey();
		
		assertNotNull(retVal);
		assertEquals(retVal, 1);
		
		verify(sessionFactory).getCurrentSession();
		verify(session).createSQLQuery("select nextval('" + SEQUENCE + "')");
		verify(query).uniqueResult();
	}
	
	/**
	 * Tests the method to load the <code>HubSubscriberTransaction</code> Object given the primary key id
	 */
	@Test
	public void loadTest() {
		HubSubscriberTransaction retVal = subscriberTransactionRepo.load(1);
		
		assertNotNull(retVal);
		assertEquals(1, retVal.getSubscriberTransactionId());
		assertEquals("content", retVal.getContent());
		assertEquals("application/xml", retVal.getContentType());
		
		HubDistributionStatus hubDistributionStatus = retVal.getHubDistributionStatus();
		assertNotNull(hubDistributionStatus);
		assertEquals("success", hubDistributionStatus.getDistributionStatusCode());
		
		HubSubscription hubSubscription = retVal.getHubSubscription();
		assertNotNull(hubSubscription);
		assertEquals(1, hubSubscription.getSubscriptionId());
		
		HubSubscriptionStatus hubSubscriptionStatus = hubSubscription.getHubSubscriptionStatus();
		assertNotNull(hubSubscriptionStatus);
		assertEquals("accepted", hubSubscriptionStatus.getSubscriptionStatusCode());
		
		HubTopic hubTopic = hubSubscription.getHubTopic();
		assertNotNull(hubTopic);
		assertEquals(1, hubTopic.getTopicId());
		assertEquals("topic_url", hubTopic.getTopicUrl());
		
		verify(sessionFactory).getCurrentSession();
		verify(session).load((Class<HubSubscriberTransaction>) any(), anyInt());
	}
	
	/**
	 * Tests the method to create a new record / update an existing record for <code>HubSubscriberTransaction</code>
	 */
	@Test
	public void saveOrUpdateTest() {
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				HubSubscriberTransaction hubSubscriberTransaction = (HubSubscriberTransaction) args[0];
				
				assertNotNull(hubSubscriberTransaction);
				assertEquals(1, hubSubscriberTransaction.getSubscriberTransactionId());
				assertEquals("content", hubSubscriberTransaction.getContent());
				assertEquals("application/xml", hubSubscriberTransaction.getContentType());
				
				HubDistributionStatus hubDistributionStatus = hubSubscriberTransaction.getHubDistributionStatus();
				assertNotNull(hubDistributionStatus);
				assertEquals("success", hubDistributionStatus.getDistributionStatusCode());
				
				HubSubscription hubSubscription = hubSubscriberTransaction.getHubSubscription();
				assertNotNull(hubSubscription);
				assertEquals(1, hubSubscription.getSubscriptionId());
				
				HubSubscriptionStatus hubSubscriptionStatus = hubSubscription.getHubSubscriptionStatus();
				assertNotNull(hubSubscriptionStatus);
				assertEquals("accepted", hubSubscriptionStatus.getSubscriptionStatusCode());
				
				HubTopic hubTopic = hubSubscription.getHubTopic();
				assertNotNull(hubTopic);
				assertEquals(1, hubTopic.getTopicId());
				assertEquals("topic_url", hubTopic.getTopicUrl());
				return null;
			}
		}).when(session).saveOrUpdate(hubSubscriberTransaction);
		
		subscriberTransactionRepo.saveOrUpdate(hubSubscriberTransaction);
		
		verify(sessionFactory).getCurrentSession();
		verify(session).saveOrUpdate(hubSubscriberTransaction);
	}
	
	/**
	 * Tests the method to set audit fields for the entity <code>HubSubscriberTransaction</code>
	 */
	@Test
	public void setAuditFields() {
		
		assertNotNull(hubSubscriberTransaction);
		assertNull(hubSubscriberTransaction.getCreatedBy());
		assertNull(hubSubscriberTransaction.getCreateTime());
		assertNull(hubSubscriberTransaction.getLastUpdated());
		assertNull(hubSubscriberTransaction.getLastUpdatedBy());
		
		subscriberTransactionRepo.setAuditFields(hubSubscriberTransaction);
		
		assertNotNull(hubSubscriberTransaction);
		assertNotNull(hubSubscriberTransaction.getCreatedBy());
		assertNotNull(hubSubscriberTransaction.getCreateTime());
		assertNotNull(hubSubscriberTransaction.getLastUpdated());
		assertNotNull(hubSubscriberTransaction.getLastUpdatedBy());
	}
	
}
