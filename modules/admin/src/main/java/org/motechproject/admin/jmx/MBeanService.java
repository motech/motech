package org.motechproject.admin.jmx;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.broker.jmx.TopicViewMBean;
import org.motechproject.admin.domain.QueueMBean;
import org.motechproject.admin.domain.QueueMessage;
import org.motechproject.admin.domain.TopicMBean;
import org.motechproject.admin.security.SecurityConstants;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.date.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This service is responsible for retrieving JMS information through JMX. Uses an mbean server to retrieve
 * the information.
 *
 * @see MotechMBeanServer
 */
@Service
public class MBeanService {

    public static final String JMS_MESSAGE_ID = "JMSMessageID";
    public static final String JMS_REDELIVERED = "JMSRedelivered";
    public static final String JMS_TIMESTAMP = "JMSTimestamp";
    public static final String DESTINATION = "Destination";

    @Autowired
    private MotechMBeanServer mBeanServer;

    /**
     * Returns topic statistics for the given tenant's JMS topics. To be counted as a tenant's topic,
     * its name must start with the tenants id.
     *
     * @param tenantId the Id of the tenant. Statistics will be retrieved for the topics belonging to this tenant.
     * @return {@link List} of {@link TopicMBean}. One for each topic belonging to the given tenant.
     */
    @PreAuthorize(SecurityConstants.MANAGE_ACTIVEMQ)
    public List<TopicMBean> getTopicStatistics(String tenantId) {
        try {
            List<TopicMBean> topics = new ArrayList<>();
            for (ObjectName name : mBeanServer.getTopics()) {
                String destination = name.getKeyProperty(DESTINATION);
                if (destination.startsWith(tenantId)) {
                    TopicViewMBean topicView = mBeanServer.getTopicViewMBean(name);

                    TopicMBean topic = new TopicMBean(destination);
                    topic.setEnqueueCount(topicView.getEnqueueCount());
                    topic.setDequeueCount(topicView.getDequeueCount());
                    topic.setExpiredCount(topicView.getExpiredCount());
                    topic.setConsumerCount(topicView.getConsumerCount());
                    topics.add(topic);
                }
            }
            return topics;
        } catch (IOException ex) {
            throw new MotechException("Could not access MBeans ", ex);
        }
    }

    /**
     * Returns queue statistics for the given tenant's JMS queues. To be counted as a tenant's queue,
     * its name must start with the tenants id.
     *
     * @param tenantId the Id of the tenant. Statistics will be retrieved for the queues belonging to this tenant.
     * @return {@link List} of {@link QueueMBean}. One for each queue belonging to the given tenant.
     */
    @PreAuthorize(SecurityConstants.MANAGE_ACTIVEMQ)
    public List<QueueMBean> getQueueStatistics(String tenantId) {
        try {
            List<QueueMBean> queues = new ArrayList<>();
            for (ObjectName name : mBeanServer.getQueues()) {
                String destination = name.getKeyProperty(DESTINATION);
                if (destination.startsWith(tenantId)) {
                    QueueViewMBean queueView = mBeanServer.getQueueViewMBean(name);

                    QueueMBean queue = new QueueMBean(destination);
                    queue.setEnqueueCount(queueView.getEnqueueCount());
                    queue.setDequeueCount(queueView.getDequeueCount());
                    queue.setExpiredCount(queueView.getExpiredCount());
                    queue.setConsumerCount(queueView.getConsumerCount());
                    queue.setQueueSize(queueView.getQueueSize());
                    queues.add(queue);
                }
            }
            return queues;
        } catch (IOException ex) {
            throw new MotechException("Could not access MBeans ", ex);
        }
    }


    /**
     * Retrieves a list of messages for the given JMS queue.
     *
     * @param queueName The name of the queue for which messages should be retrieved.
     * @return {@link List} of messages for the given queue.
     */
    @PreAuthorize(SecurityConstants.MANAGE_ACTIVEMQ)
    public List<QueueMessage> getQueueMessages(String queueName) {
        try {
            ArrayList<QueueMessage> queueMessages = new ArrayList<>();
            QueueViewMBean queueViewMBean = mBeanServer.getQueueViewMBean(queueName);
            for (CompositeData compositeData : queueViewMBean.browse()) {
                if (compositeData != null) {
                    String messageId = (String) compositeData.get(JMS_MESSAGE_ID);
                    Boolean redelivered = (Boolean) compositeData.get(JMS_REDELIVERED);
                    Date timestamp = (Date) compositeData.get(JMS_TIMESTAMP);
                    queueMessages.add(new QueueMessage(messageId, redelivered, DateUtil.newDateTime(timestamp)));
                }
            }
            return queueMessages;
        } catch (OpenDataException openDataException) {
            throw new MotechException(String.format("Could not Browse MBean for queue %s", queueName), openDataException);
        } catch (IOException ioException) {
            throw new MotechException(String.format("Could not access MBean for queue %s", queueName), ioException);
        }
    }
}
