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
import org.motechproject.hub.model.DistributionStatusLookup;
import org.motechproject.hub.model.hibernate.HubDistributionError;
import org.motechproject.hub.model.hibernate.HubDistributionStatus;
import org.motechproject.hub.model.hibernate.HubTopic;

/**
 * This is a tests class to test the DistributionStatusRepository class
 * @author Anuranjan
 *
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class DistributionStatusRepositoryTest {
	
	@Mock
	private SessionFactory sessionFactory;
	
	@Mock
	private Session session;
	
	@Mock
	private SQLQuery query;
	
	@Mock
	private Criteria criteria;

	@InjectMocks private DistributionStatusRepository distributionStatusRepo = new DistributionStatusRepository(sessionFactory);
	
	private HubDistributionStatus hubDistributionStatus;
	
	@Before
	public void setUp() {
		hubDistributionStatus = new HubDistributionStatus();
		hubDistributionStatus.setDistributionStatusCode(DistributionStatusLookup.FAILURE.toString());
		
		distributionStatusRepo.setSessionFactory(sessionFactory);
		
		//when(distributionStatusRepo.getSessionFactory().getCurrentSession()).thenReturn(session);
		when(query.uniqueResult()).thenReturn("1");
		when(session.load((Class<HubDistributionStatus>) any(), anyInt())).thenReturn(hubDistributionStatus);
		when(session.createCriteria((Class<HubDistributionError>) any())).thenReturn(criteria);
		when(criteria.uniqueResult()).thenReturn(hubDistributionStatus);
	}
	
	/**
	 * Tests the method to load the <code>HubDistributionStatus</code> object given the primary key id
	 */
	@Test
	public void loadTest() {
		HubDistributionStatus retVal = distributionStatusRepo.load(1);
		
		assertNotNull(retVal);
		assertEquals("failure", retVal.getDistributionStatusCode());
		
		verify(sessionFactory).getCurrentSession();
		verify(session).load((Class<HubDistributionError>) any(), anyInt());
	}
	
	/**
	 * Tests the method to fetch the <code>HubDistributionStatus</code> object given the status code
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
				assertEquals("distributionStatusCode=failure", criterion.toString());
				return null;
			}
		}).when(criteria).add((Criterion) any());
		
		HubDistributionStatus retVal = distributionStatusRepo.findByStatus("failure");
		
		assertNotNull(retVal);
		assertEquals("failure", retVal.getDistributionStatusCode());
		
		verify(sessionFactory).getCurrentSession();
		verify(session).createCriteria((Class<HubTopic>) any());
		verify(criteria).add((Criterion) any());
		verify(criteria).uniqueResult();
	}
}