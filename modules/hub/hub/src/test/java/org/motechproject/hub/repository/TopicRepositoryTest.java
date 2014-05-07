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
import org.hibernate.criterion.Criterion;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.motechproject.hub.model.hibernate.HubTopic;

/**
 * This is a tests class to test the TopicRepository class
 * @author Anuranjan
 *
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class TopicRepositoryTest {
	
	@Mock
	private SessionFactory sessionFactory;
	
	@Mock
	private Session session;
	
	@Mock
	private SQLQuery query;
	
	@Mock
	private Criteria criteria;

	@InjectMocks private TopicRepository topicRepo = new TopicRepository(sessionFactory);
	
	private HubTopic hubTopic;
	
	private static final String SEQUENCE = "hub.hub_topic_topic_id_seq";
	
	@Before
	public void setUp() {
		hubTopic = new HubTopic();
		hubTopic.setTopicId(1);
		hubTopic.setTopicUrl("topic_url");
		
		topicRepo.setSessionFactory(sessionFactory);
		
		//when(topicRepo.getSessionFactory().getCurrentSession()).thenReturn(session);
		when(session.createSQLQuery("select nextval('" + SEQUENCE + "')")).thenReturn(query);
		when(query.uniqueResult()).thenReturn("1");
		when(session.load((Class<HubTopic>) any(), anyInt())).thenReturn(hubTopic);
		when(session.createCriteria((Class<HubTopic>) any())).thenReturn(criteria);
		when(criteria.uniqueResult()).thenReturn(hubTopic);
	}
	
	/**
	 * Tests the method to load the next primary key value
	 */
	@Test
	public void getNextKeyTest() {
		long retVal = topicRepo.getNextKey();
		
		assertNotNull(retVal);
		assertEquals(retVal, 1);
		
		verify(sessionFactory).getCurrentSession();
		verify(session).createSQLQuery("select nextval('" + SEQUENCE + "')");
		verify(query).uniqueResult();
	}
	
	/**
	 * Tests the method to load the <code>HubTopic</code> Object given the primary key id
	 */
	@Test
	public void loadTest() {
		HubTopic retVal = topicRepo.load(1);
		
		assertNotNull(retVal);
		assertEquals(1, retVal.getTopicId());
		assertEquals("topic_url", retVal.getTopicUrl());
		
		verify(sessionFactory).getCurrentSession();
		verify(session).load((Class<HubTopic>) any(), anyInt());
	}
	
	/**
	 * Tests the method to find a <code>HubTopic</code> provided the <code>topicUrl</code>
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
				assertEquals("topicUrl=topic_url", criterion.toString());
				return null;
			}
		}).when(criteria).add((Criterion) any());
		
		HubTopic retVal = topicRepo.findByTopicUrl("topic_url");
		
		assertNotNull(retVal);
		assertEquals(1, retVal.getTopicId());
		assertEquals("topic_url", retVal.getTopicUrl());
		
		verify(sessionFactory).getCurrentSession();
		verify(session).createCriteria((Class<HubTopic>) any());
		verify(criteria).add((Criterion) any());
		verify(criteria).uniqueResult();
	}
	
	/**
	 * Tests the method to create a new record / update an existing record for <code>HubTopic</code>
	 */
	@Test
	public void saveOrUpdateTest() {
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				HubTopic hubTopic = (HubTopic) args[0];
				assertNotNull(hubTopic);
				assertEquals(1, hubTopic.getTopicId());
				assertEquals("topic_url", hubTopic.getTopicUrl());
				return null;
			}
		}).when(session).saveOrUpdate(hubTopic);
		
		topicRepo.saveOrUpdate(hubTopic);
		
		verify(sessionFactory).getCurrentSession();
		verify(session).saveOrUpdate(hubTopic);
	}
	
	/**
	 * Tests the method to delete an existing record for <code>HubTopic</code>
	 */
	@Test
	public void deleteTest() {
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				HubTopic hubTopic = (HubTopic) args[0];
				assertNotNull(hubTopic);
				assertEquals(1, hubTopic.getTopicId());
				assertEquals("topic_url", hubTopic.getTopicUrl());
				return null;
			}
		}).when(session).delete(hubTopic);
		
		topicRepo.delete(hubTopic);
		
		verify(sessionFactory).getCurrentSession();
		verify(session).delete(hubTopic);
	}
	
	/**
	 * Tests the method to set the audit fields for the entity <code>HubTopic</code>
	 */
	@Test
	public void setAuditFields() {
		
		assertNotNull(hubTopic);
		assertNull(hubTopic.getCreatedBy());
		assertNull(hubTopic.getCreateTime());
		assertNull(hubTopic.getLastUpdated());
		assertNull(hubTopic.getLastUpdatedBy());
		
		topicRepo.setAuditFields(hubTopic);
		
		assertNotNull(hubTopic);
		assertNotNull(hubTopic.getCreatedBy());
		assertNotNull(hubTopic.getCreateTime());
		assertNotNull(hubTopic.getLastUpdated());
		assertNotNull(hubTopic.getLastUpdatedBy());
	}
	
}
