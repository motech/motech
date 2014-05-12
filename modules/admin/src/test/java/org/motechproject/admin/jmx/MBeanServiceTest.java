package org.motechproject.admin.jmx;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.admin.domain.QueueMBean;

import javax.management.ObjectName;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

public class MBeanServiceTest {

    @InjectMocks
    MBeanService mBeanService = new MBeanService();

    @Mock
    MotechMBeanServer mBeanServer;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnQueuesForGivenTenant() throws IOException {

        ObjectName fooQueue = mock(ObjectName.class);
        ObjectName barQueue = mock(ObjectName.class);
        ObjectName[] queues = {fooQueue, barQueue};

        QueueViewMBean queueViewMBean = mock(QueueViewMBean.class);
        given(mBeanServer.getQueueViewMBean(any(ObjectName.class))).willReturn(queueViewMBean);

        given(mBeanServer.getQueues()).willReturn(queues);
        given(fooQueue.getKeyProperty(MotechMBeanServer.DESTINATION)).willReturn("foo_queue");
        given(barQueue.getKeyProperty(MotechMBeanServer.DESTINATION)).willReturn("bar_queue");

        String tenantId = "foo";
        List<QueueMBean> queueStatistics = mBeanService.getQueueStatistics(tenantId);
        assertThat(queueStatistics.size(), Is.is(1));
        assertThat(queueStatistics.get(0).getDestination(), Is.is("foo_queue"));
    }

}
