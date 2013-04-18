package org.motechproject.admin.web;


import org.hamcrest.text.StringContains;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.domain.QueueMBean;
import org.motechproject.admin.domain.Tenant;
import org.motechproject.admin.jmx.MBeanService;
import org.motechproject.admin.web.controller.QueueMessage;
import org.motechproject.admin.web.controller.QueueStatisticsController;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.request.MockMvcRequestBuilders;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:testApplicationAdmin.xml")
public class QueueStatisticsControllerTest {


    MockMvc mockMvc;

    @InjectMocks
    QueueStatisticsController queueStatisticsController = new QueueStatisticsController();

    @Mock
    MBeanService mBeanService;

    @Mock
    Tenant tenant;

    @Before
    public void before() {
        initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(queueStatisticsController).build();
        given(tenant.canHaveQueue(anyString())).willReturn(true);
    }

    @Test
    public void shouldReturnAllQueueInformation() throws Exception {
        given(mBeanService.getQueueStatistics(anyString())).willReturn(Arrays.asList(new QueueMBean("queue-1"), new QueueMBean("queue-2")));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/queues"))
                .andExpect(status().isOk())
                .andExpect(content().type(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new StringContains("\"destination\":\"queue-1\"")))
                .andExpect(content().string(new StringContains("\"destination\":\"queue-2\"")));
    }

    @Test
    public void shouldReturnMessageInformationGivenQueueName() throws Exception {
        given(mBeanService.getMessages("foo")).willReturn(Arrays.asList(new QueueMessage("123", false, new Date())));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/queues/browse?queueName=foo"))
                .andExpect(status().isOk())
                .andExpect(content().type(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new StringContains("\"messageId\":\"123")))
                .andExpect(content().string(new StringContains("\"redelivered\":false")));
    }

    @Test
    public void shouldReturnBadRequestCodeIfQueueNameNotProvided() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/queues/browse"))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void shouldReturnEmptyArrayIfQueueDoesNotBelongToTenant() throws Exception {
        given(tenant.canHaveQueue(anyString())).willReturn(false);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/queues/browse?queueName=bar"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }
}
