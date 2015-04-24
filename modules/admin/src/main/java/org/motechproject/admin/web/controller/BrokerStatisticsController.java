package org.motechproject.admin.web.controller;

import org.motechproject.admin.domain.QueueMessage;
import org.motechproject.admin.domain.TopicMBean;
import org.motechproject.commons.api.Tenant;
import org.motechproject.admin.domain.QueueMBean;
import org.motechproject.admin.jmx.MBeanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

/**
 * Responsible for the queue statistics view in the Admin UI.
 * Uses {@link org.motechproject.admin.jmx.MBeanService} for retrieving queue data.
 */
@Controller
public class BrokerStatisticsController {

    @Autowired
    private MBeanService mBeanService;

    @Autowired
    @Qualifier("currentTenant")
    private Tenant tenant;

    /**
     * Returns the topic statistics for the tenant.
     * @return a list {@link org.motechproject.admin.domain.TopicMBean} with the statistics, one for each topic
     */
    @RequestMapping(value = "/topics")
    @ResponseBody
    public List<TopicMBean> topics() {
        return mBeanService.getTopicStatistics(tenant.getId());
    }

    /**
     * Returns the queue statistics for the tenant.
     * @return a list {@link org.motechproject.admin.domain.QueueMBean} with the statistics, one for each queue
     */
    @RequestMapping(value = "/queues")
    @ResponseBody
    public List<QueueMBean> queues() {
        return mBeanService.getQueueStatistics(tenant.getId());
    }

    /**
     * Returns a list of messages for a given queue.
     * @param queueName the name of the queue
     * @return a list of {@link org.motechproject.admin.domain.QueueMessage} objects describing messages from the queue
     */
    @RequestMapping(value = "/queues/browse")
    @ResponseBody
    public List<QueueMessage> browseQueueMessages(@RequestParam(required = true) String queueName) {
        if (tenant.canHaveQueue(queueName)) {
            return mBeanService.getQueueMessages(queueName);
        }
        return Collections.emptyList();
    }
}
