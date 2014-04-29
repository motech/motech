package org.motechproject.batch.repository;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.model.BatchJobList;
import org.motechproject.batch.model.hibernate.BatchJob;
import org.motechproject.batch.model.hibernate.BatchJobExecutionParams;
@Ignore
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class JobRepositoryTest {
	
	private String jobName = "testJob";
	@Mock
	private SessionFactory sessionFactory;
	
	@Mock
	private Session session;
	
	@Mock
	private Criteria criteria;
	
	@Mock
	private SQLQuery query;
    
	@InjectMocks private JobRepository jobRepo = new JobRepository(sessionFactory);
	
	private static final String SEQUENCE = "hub.hub_subscription_subscription_id_seq";
	
	@Before
	public void setUp() {
		List<BatchJobExecutionParams> batchExParamsList = new ArrayList<BatchJobExecutionParams>();
		BatchJobExecutionParams batchExParams = new BatchJobExecutionParams();
		batchExParams.setKeyName("testKey");
		batchExParams.setStringVal("testVal");
		batchExParamsList.add(batchExParams);
		
		
		
		//TODO remove below comment
		//when(jobRepo.getSessionFactory().getCurrentSession()).thenReturn(session);
		when(session.createCriteria((Class<BatchJob>) any())).thenReturn(criteria);
		when(session.createCriteria((Class<BatchJobExecutionParams>) any())).thenReturn(criteria);
		
		when(criteria.list()).thenReturn(batchExParamsList);
		//when(criteria.createAlias(anyString(), anyString()).createAlias(anyString(), anyString()).createAlias(anyString(), anyString())).thenReturn(criteria);
		when(criteria.createAlias(anyString(), anyString())).thenReturn(criteria);
		when(criteria.createAlias("batchJobExecution.batchJobInstance","batchJobInstance")).thenReturn(criteria);
		when(session.createSQLQuery("select nextval('" + SEQUENCE + "')")).thenReturn(query);
		when(query.uniqueResult()).thenReturn("1");
		when(session.createSQLQuery("select nextval('" + SEQUENCE + "')")).thenReturn(query);
	
	}
	@Test
	public void getListOfJobsTest() throws BatchException
		{
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				Criterion criterion = (Criterion) args[0];
				assertNotNull(criterion);
				return null;
			}
		}).when(criteria).add((Criterion) any());
		
		List<BatchJob> batchJobList = new ArrayList<BatchJob>();
		BatchJob batchJob = new BatchJob();
		batchJob.setJobId(1l);
		batchJob.setJobName(jobName);
		batchJobList.add(batchJob);
		when(criteria.list()).thenReturn(batchJobList);
		List<BatchJob> listBatchJob = jobRepo.getListOfJobs();
		assertNotNull(listBatchJob);
		assertEquals(1l, (long)listBatchJob.get(0).getJobId());
		assertEquals("testJob", listBatchJob.get(0).getJobName());
		verify(sessionFactory).getCurrentSession();
		verify(session).createCriteria((Class<BatchJob>) any());
		verify(criteria).list();
		
		}
	
	
	@Test
	public void getJobExecutionHistoryTest() throws BatchException
	{
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				
				assertNotNull(args);
				assertEquals(1, args.length);
				Criterion criterion = (Criterion) args[0];
				assertNotNull(criterion);
				assertEquals("batchJobInstance.jobName=testJob", criterion.toString());
				return null;
			}
		}).when(criteria).add((Criterion) any());
		
		List<BatchJobExecutionParams> listBatchJobExecutionParams = jobRepo.getJobExecutionHistory(jobName);
		assertNotNull(listBatchJobExecutionParams);
		assertEquals("testKey", listBatchJobExecutionParams.get(0).getKeyName());
		assertEquals("testVal", listBatchJobExecutionParams.get(0).getStringVal());
		verify(sessionFactory).getCurrentSession();
		verify(session).createCriteria((Class<BatchJobExecutionParams>) any());
		verify(criteria).list();
	}
	
	
	@Test
	public void getNextKeyTest() {
		long retVal = jobRepo.getNextKey();
		
		assertNotNull(retVal);
		assertEquals(retVal, 1);
		
		verify(sessionFactory).getCurrentSession();
		verify(session).createSQLQuery("select nextval('" + SEQUENCE + "')");
		verify(query).uniqueResult();
	}
}
