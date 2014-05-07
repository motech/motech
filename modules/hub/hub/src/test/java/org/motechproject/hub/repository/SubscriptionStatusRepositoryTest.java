package org.motechproject.hub.repository;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.motechproject.hub.model.SubscriptionStatusLookup;
import org.motechproject.hub.model.hibernate.HubDistributionError;
import org.motechproject.hub.model.hibernate.HubSubscriptionStatus;
import org.motechproject.hub.model.hibernate.HubTopic;

/**
 * This is a tests class to test the SubscriptionStatusRepository class
 * @author Anuranjan
 *
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionStatusRepositoryTest {
	
	@Mock
	private SessionFactory sessionFactory;
	
	@Mock
	private Session session;
	
	@Mock
	private SQLQuery query;
	
	@Mock
	private Criteria criteria;

	@InjectMocks private SubscriptionStatusRepository subscriptionStatusRepo = new SubscriptionStatusRepository(sessionFactory);
	
	private HubSubscriptionStatus hubSubscriptionStatus;
	
	@Before
	public void setUp() {
		hubSubscriptionStatus = new HubSubscriptionStatus();
		hubSubscriptionStatus.setSubscriptionStatusCode(SubscriptionStatusLookup.INTENT_FAILED.toString());
		
		subscriptionStatusRepo.setSessionFactory(sessionFactory);
		
		//when(subscriptionStatusRepo.getSessionFactory().getCurrentSession()).thenReturn(session);
		when(query.uniqueResult()).thenReturn("1");
		when(session.load((Class<HubSubscriptionStatus>) any(), anyInt())).thenReturn(hubSubscriptionStatus);
		when(session.createCriteria((Class<HubDistributionError>) any())).thenReturn(criteria);
		when(criteria.uniqueResult()).thenReturn(hubSubscriptionStatus);
	}
	
	/**
	 * Tests the method to load the <code>HubSubscriptionStatus</code> object given the primary key id
	 */
	@Test
	public void loadTest() {
		HubSubscriptionStatus retVal = subscriptionStatusRepo.load(1);
		
		assertNotNull(retVal);
		assertEquals("intent_failed", retVal.getSubscriptionStatusCode());
		
		verify(sessionFactory).getCurrentSession();
		verify(session).load((Class<HubDistributionError>) any(), anyInt());
	}
	
	/**
	 * Tests the method to fetch the <code>HubSubscriptionStatus</code> object given the status code
	 */
	@Test
	public void findByStatusTest() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				Criterion criterion = (Criterion) args[0];
				assertNotNull(criterion);
				assertEquals("subscriptionStatusCode=intent_failed", criterion.toString());
				return null;
			}
		}).when(criteria).add((Criterion) any());
		
		HubSubscriptionStatus retVal = subscriptionStatusRepo.findByStatus("intent_failed");
		
		assertNotNull(retVal);
		assertEquals("intent_failed", retVal.getSubscriptionStatusCode());
		
		verify(sessionFactory).getCurrentSession();
		verify(session).createCriteria((Class<HubTopic>) any());
		verify(criteria).add((Criterion) any());
		verify(criteria).uniqueResult();
	}
}