package org.motechproject.hub.repository;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.motechproject.hub.exception.HubErrors;
import org.motechproject.hub.exception.HubException;
import org.motechproject.hub.model.SubscriptionStatusLookup;
import org.motechproject.hub.model.hibernate.HubSubscription;
import org.motechproject.hub.model.hibernate.HubSubscriptionStatus;
import org.motechproject.hub.model.hibernate.HubTopic;
import org.springframework.http.HttpStatus;

/**
 * This is a tests class to test the SubscriptionRepository class
 * @author Anuranjan
 *
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionRepositoryTest {
	
	@Mock
	private SessionFactory sessionFactory;
	
	@Mock
	private Session session;
	
	@Mock
	private SQLQuery query;
	
	@Mock
	private Criteria criteria;

	@InjectMocks private SubscriptionRepository subscriptionRepo = new SubscriptionRepository(sessionFactory);
	
	private HubSubscription hubSubscription;
	
	private HubTopic hubTopic;
	
	private HubSubscriptionStatus subscriptionStatus;
	
	private List<HubSubscription> subscriptionList;
	
	private static final String SEQUENCE = "hub.hub_subscription_subscription_id_seq";
	
	@Before
	public void setUp() {
		
		hubTopic = new HubTopic();
		hubTopic.setTopicId(1);
		hubTopic.setTopicUrl("topic_url");
		
		subscriptionStatus = new HubSubscriptionStatus();
		subscriptionStatus.setSubscriptionStatusCode(SubscriptionStatusLookup.ACCEPTED.toString());
		
		hubSubscription = new HubSubscription();
		hubSubscription.setSubscriptionId(1);
		hubSubscription.setHubSubscriptionStatus(subscriptionStatus);
		hubSubscription.setHubTopic(hubTopic);
		
		subscriptionList = new ArrayList<HubSubscription>();
		subscriptionList.add(hubSubscription);
		
		subscriptionRepo.setSessionFactory(sessionFactory);
		
		when(subscriptionRepo.getSessionFactory().getCurrentSession()).thenReturn(session);
		when(session.createSQLQuery("select nextval('" + SEQUENCE + "')")).thenReturn(query);
		when(query.uniqueResult()).thenReturn("1");
		when(session.load((Class<HubSubscription>) any(), anyInt())).thenReturn(hubSubscription);
		when(session.createCriteria((Class<HubSubscription>) any())).thenReturn(criteria);
		when(criteria.createAlias(anyString(), anyString())).thenReturn(criteria);
		when(criteria.uniqueResult()).thenReturn(hubSubscription);
		when(criteria.list()).thenReturn(subscriptionList);
	}
	
	/**
	 * Tests the method to load the next primary key value
	 */
	@Test
	public void getNextKeyTest() {
		long retVal = subscriptionRepo.getNextKey();
		
		assertNotNull(retVal);
		assertEquals(retVal, 1);
		
		verify(sessionFactory).getCurrentSession();
		verify(session).createSQLQuery("select nextval('" + SEQUENCE + "')");
		verify(query).uniqueResult();
	}
	
	/**
	 * Tests the method to load the <code>HubSubscription</code> Object given the primary key id
	 */
	@Test
	public void loadTest() {
		HubSubscription retVal = subscriptionRepo.load(1);
		
		assertNotNull(retVal);
		assertEquals(1, retVal.getSubscriptionId());
		HubSubscriptionStatus subscriptionStatus = retVal.getHubSubscriptionStatus();
		assertNotNull(subscriptionStatus);
		assertEquals("accepted", subscriptionStatus.getSubscriptionStatusCode());
		HubTopic hubTopic = retVal.getHubTopic();
		assertNotNull(hubTopic);
		assertEquals(1, hubTopic.getTopicId());
		assertEquals("topic_url", hubTopic.getTopicUrl());
		
		verify(sessionFactory).getCurrentSession();
		verify(session).load((Class<HubSubscription>) any(), anyInt());
	}
	
	/**
	 * Tests the method to find the list of <code>HubSubscription</code>s provided the <code>topicUrl</code>
	 */
	@Test
	public void findByTopicUrlTest() {
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				Criterion criterion = (Criterion) args[0];
				assertNotNull(criterion);
				assertEquals("ht.topicUrl=topic_url", criterion.toString());
				return null;
			}
		}).when(criteria).add((Criterion) any());
		
		List<HubSubscription> retVal = subscriptionRepo.findByTopicUrl("topic_url");
		
		assertNotNull(retVal);
		assertEquals(1, retVal.size());
		HubSubscription subscription = retVal.get(0);
		assertNotNull(subscription);
		
		HubSubscriptionStatus subscriptionStatus = subscription.getHubSubscriptionStatus();
		assertNotNull(subscriptionStatus);
		assertEquals("accepted", subscriptionStatus.getSubscriptionStatusCode());
		HubTopic hubTopic = subscription.getHubTopic();
		assertNotNull(hubTopic);
		assertEquals(1, hubTopic.getTopicId());
		assertEquals("topic_url", hubTopic.getTopicUrl());
		
		verify(sessionFactory).getCurrentSession();
		verify(session).createCriteria((Class<HubSubscription>) any());
		verify(criteria).add((Criterion) any());
		verify(criteria).list();
	}
	
	/**
	 * Tests the method to find the list of <code>HubSubscription</code>s provided the <code>topicUrl</code>
	 */
	@Test
	public void findByCallbackUrlAndTopicUrlTest() {
		
		doAnswer(new Answer<Void>() {
			private int count = 1;
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				Criterion criterion = (Criterion) args[0];
				assertNotNull(criterion);
				if (count == 1) {
					assertEquals("callbackUrl=callback_url", criterion.toString());
				} else {
					assertEquals("ht.topicUrl=topic_url", criterion.toString());
				}
				count++;
				return null;
			}
		}).when(criteria).add((Criterion) any());
		
		HubSubscription retVal = subscriptionRepo.findByCallbackUrlAndTopicUrl("callback_url", "topic_url");
		
		assertNotNull(retVal);
		
		HubSubscriptionStatus subscriptionStatus = retVal.getHubSubscriptionStatus();
		assertNotNull(subscriptionStatus);
		assertEquals("accepted", subscriptionStatus.getSubscriptionStatusCode());
		HubTopic hubTopic = retVal.getHubTopic();
		assertNotNull(hubTopic);
		assertEquals(1, hubTopic.getTopicId());
		assertEquals("topic_url", hubTopic.getTopicUrl());
		
		verify(sessionFactory).getCurrentSession();
		verify(session).createCriteria((Class<HubSubscription>) any());
		verify(criteria, times(2)).add((Criterion) any());
		verify(criteria).uniqueResult();
	}
	
	/**
	 * Tests the method to create a new record / update an existing record for <code>HubSubscription</code>
	 */
	@Test
	public void saveOrUpdateTest() {
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				HubSubscription hubSubscription = (HubSubscription) args[0];
				assertNotNull(hubSubscription);
				assertEquals(1, hubSubscription.getSubscriptionId());
				HubSubscriptionStatus subscriptionStatus = hubSubscription.getHubSubscriptionStatus();
				assertNotNull(subscriptionStatus);
				assertEquals("accepted", subscriptionStatus.getSubscriptionStatusCode());
				HubTopic hubTopic = hubSubscription.getHubTopic();
				assertNotNull(hubTopic);
				assertEquals(1, hubTopic.getTopicId());
				assertEquals("topic_url", hubTopic.getTopicUrl());
				return null;
			}
		}).when(session).saveOrUpdate(hubSubscription);
		
		subscriptionRepo.saveOrUpdate(hubSubscription);
		
		verify(sessionFactory).getCurrentSession();
		verify(session).saveOrUpdate(hubSubscription);
	}
	
	/**
	 * Tests the method to delete an existing record for <code>HubSubscription</code>
	 * @throws HubException 
	 */
	@Test
	public void deleteTest() throws HubException {
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				HubSubscription hubSubscription = (HubSubscription) args[0];
				assertNotNull(hubSubscription);
				assertEquals(1, hubSubscription.getSubscriptionId());
				HubSubscriptionStatus subscriptionStatus = hubSubscription.getHubSubscriptionStatus();
				assertNotNull(subscriptionStatus);
				assertEquals("accepted", subscriptionStatus.getSubscriptionStatusCode());
				HubTopic hubTopic = hubSubscription.getHubTopic();
				assertNotNull(hubTopic);
				assertEquals(1, hubTopic.getTopicId());
				assertEquals("topic_url", hubTopic.getTopicUrl());
				return null;
			}
		}).when(session).delete(hubSubscription);
		
		subscriptionRepo.delete(hubSubscription);
		
		verify(sessionFactory, times(2)).getCurrentSession();
		verify(session).delete(hubSubscription);
	}
	
	/**
	 * Tests the method to delete a non-existing record for <code>HubSubscription</code>
	 * Exception occurs in this scenario
	 * @throws HubException 
	 */
	@Test
	public void deleteException() {

		when(criteria.uniqueResult()).thenReturn(null);
		
		try {
			subscriptionRepo.delete(hubSubscription);
		} catch (HubException e) {
			HubErrors he = e.getError();
			assertEquals(1002, he.getCode());
			assertEquals(HttpStatus.BAD_REQUEST, he.getHttpStatus());
			assertEquals("Subscription not found", he.getMessage());
		}
		
		verify(sessionFactory).getCurrentSession();
	}
	
	/**
	 * Tests the method to set the audit fields for the entity <code>hubSubscription</code>
	 */
	@Test
	public void setAuditFields() {
		
		assertNotNull(hubSubscription);
		assertNull(hubSubscription.getCreatedBy());
		assertNull(hubSubscription.getCreateTime());
		assertNull(hubSubscription.getLastUpdated());
		assertNull(hubSubscription.getLastUpdatedBy());
		
		subscriptionRepo.setAuditFields(hubSubscription);
		
		assertNotNull(hubSubscription);
		assertNotNull(hubSubscription.getCreatedBy());
		assertNotNull(hubSubscription.getCreateTime());
		assertNotNull(hubSubscription.getLastUpdated());
		assertNotNull(hubSubscription.getLastUpdatedBy());
	}
	
}
