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
import org.motechproject.hub.model.SubscriptionStatusLookup;
import org.motechproject.hub.model.hibernate.HubDistributionError;
import org.motechproject.hub.model.hibernate.HubSubscription;
import org.motechproject.hub.model.hibernate.HubSubscriptionStatus;
import org.motechproject.hub.model.hibernate.HubTopic;

/**
 * This is a tests class to test the DistributionErrorRepository class
 * @author Anuranjan
 *
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class DistributionErrorRepositoryTest {
	
	@Mock
	private SessionFactory sessionFactory;
	
	@Mock
	private Session session;
	
	@Mock
	private SQLQuery query;
	
	@Mock
	private Criteria criteria;

	@InjectMocks private DistributionErrorRepository distributionErrorRepo = new DistributionErrorRepository(sessionFactory);
	
	private HubDistributionError hubDistributionError;
	
	private HubSubscription hubSubscription;
	
	private HubTopic hubTopic;
	
	private HubSubscriptionStatus subscriptionStatus;
	
	private static final String SEQUENCE = "hub.hub_distribution_error_distribution_error_id_seq";
	
	@Before
	public void setUp() {
		hubDistributionError = new HubDistributionError();
		hubDistributionError.setDistributionErrorId(1);
		
		hubTopic = new HubTopic();
		hubTopic.setTopicId(1);
		hubTopic.setTopicUrl("topic_url");
		
		subscriptionStatus = new HubSubscriptionStatus();
		subscriptionStatus.setSubscriptionStatusCode(SubscriptionStatusLookup.ACCEPTED.toString());
		
		hubSubscription = new HubSubscription();
		hubSubscription.setSubscriptionId(1);
		hubSubscription.setHubSubscriptionStatus(subscriptionStatus);
		hubSubscription.setHubTopic(hubTopic);
		
		hubDistributionError.setHubSubscription(hubSubscription);
		
		distributionErrorRepo.setSessionFactory(sessionFactory);
		
		when(distributionErrorRepo.getSessionFactory().getCurrentSession()).thenReturn(session);
		when(session.createSQLQuery("select nextval('" + SEQUENCE + "')")).thenReturn(query);
		when(query.uniqueResult()).thenReturn("1");
		when(session.load((Class<HubDistributionError>) any(), anyInt())).thenReturn(hubDistributionError);
		when(session.createCriteria((Class<HubDistributionError>) any())).thenReturn(criteria);
		when(criteria.uniqueResult()).thenReturn(hubDistributionError);
	}
	
	/**
	 * Tests the method to load the next primary key value
	 */
	@Test
	public void getNextKeyTest() {
		long retVal = distributionErrorRepo.getNextKey();
		
		assertNotNull(retVal);
		assertEquals(retVal, 1);
		
		verify(sessionFactory).getCurrentSession();
		verify(session).createSQLQuery("select nextval('" + SEQUENCE + "')");
		verify(query).uniqueResult();
	}
	
	/**
	 * Tests the method to load the <code>HubDistributionError</code> Object given the primary key id
	 */
	@Test
	public void loadTest() {
		HubDistributionError retVal = distributionErrorRepo.load(1);
		
		assertNotNull(retVal);
		assertEquals(1, retVal.getDistributionErrorId());
		
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
		verify(session).load((Class<HubDistributionError>) any(), anyInt());
	}
	
	/**
	 * Tests the method to create a new record / update an existing record for <code>HubDistributionError</code>
	 */
	@Test
	public void saveOrUpdateTest() {
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				HubDistributionError hubDistributionError = (HubDistributionError) args[0];
				
				assertNotNull(hubDistributionError);
				assertEquals(1, hubDistributionError.getDistributionErrorId());
				
				HubSubscription hubSubscription = hubDistributionError.getHubSubscription();
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
		}).when(session).saveOrUpdate(hubDistributionError);
		
		distributionErrorRepo.saveOrUpdate(hubDistributionError);
		
		verify(sessionFactory).getCurrentSession();
		verify(session).saveOrUpdate(hubDistributionError);
	}
	
	/**
	 * Tests the method to set the audit fields for the entity <code>DistributionError</code>
	 */
	@Test
	public void setAuditFields() {
		
		assertNotNull(hubDistributionError);
		assertNull(hubDistributionError.getCreatedBy());
		assertNull(hubDistributionError.getCreateTime());
		assertNull(hubDistributionError.getLastUpdated());
		assertNull(hubDistributionError.getLastUpdatedBy());
		
		distributionErrorRepo.setAuditFields(hubDistributionError);
		
		assertNotNull(hubDistributionError);
		assertNotNull(hubDistributionError.getCreatedBy());
		assertNotNull(hubDistributionError.getCreateTime());
		assertNotNull(hubDistributionError.getLastUpdated());
		assertNotNull(hubDistributionError.getLastUpdatedBy());
	}
	
}
