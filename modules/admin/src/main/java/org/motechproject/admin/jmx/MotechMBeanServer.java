package org.motechproject.admin.jmx;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.broker.jmx.TopicViewMBean;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * The MBean server providing access to ActiveMQ MBeans. Uses a JMX connection.
 */
@Component
public class MotechMBeanServer {

    public static final String DESTINATION = "Destination";

    private static final Object CONNECTION_MONITOR = new Object();

    private ConfigurationService configurationService;
    private MBeanServerConnection connection;
    private String jmxCurrentHost;

    @Autowired
    public MotechMBeanServer(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Retrieves the MBean view from the ActiveMQ broker.
     * @return a view into the broker MBeans.
     */
    public BrokerViewMBean getBrokerViewMBean() {
        String mBeanName = "org.apache.activemq:BrokerName=" + configurationService.getPlatformSettings().getJmxBroker() + ",Type=Broker";
        try {
            ObjectName activeMQ = new ObjectName(mBeanName);
            return MBeanServerInvocationHandler.newProxyInstance(openConnection(), activeMQ, BrokerViewMBean.class, true);
        } catch (MalformedObjectNameException ex) {
            throw new MotechException(ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves the topic names from the ActiveMQ broker.
     * @return an array of the topic names.
     */
    public ObjectName[] getTopics() {
        BrokerViewMBean brokerViewMBean = getBrokerViewMBean();
        return brokerViewMBean.getTopics();
    }

    /**
     * Retrieves the mbean view for the given ActiveMQ topic.
     * @param name the {@link ObjectName} representing the name of the topic for which the MBean view should be retrieved.
     * @return the {@link TopicViewMBean} allowing access to topic information.
     * @throws IOException when we were unable to connect using JMX.
     */
    public TopicViewMBean getTopicViewMBean(ObjectName name) throws IOException {
        return MBeanServerInvocationHandler.newProxyInstance(openConnection(), name, TopicViewMBean.class, true);
    }

    /**
     * Retrieves the queue names from the ActiveMQ broker.
     * @return an array of the queue names.
     */
    public ObjectName[] getQueues() {
        BrokerViewMBean brokerViewMBean = getBrokerViewMBean();
        return brokerViewMBean.getQueues();
    }

    /**
     * Retrieves the MBean view for the given ActiveMQ queue.
     * @param queueName the name of the queue for which the MBean view should be retrieved.
     * @return the {@link QueueViewMBean} allowing access to queue information.
     * @throws IOException IOException when we were unable to connect using JMX.
     */
    public QueueViewMBean getQueueViewMBean(String queueName) throws IOException {
        for (ObjectName objectName : getQueues()) {
            String destination = objectName.getKeyProperty(DESTINATION);
            if (isNotBlank(destination) && destination.equals(queueName)) {
                return getQueueViewMBean(objectName);
            }
        }
        return null;
    }

    /**
     * Retrieves the mbean view for the given ActiveMQ queue.
     * @param name the {@link ObjectName} representing the name of the queue for which the MBean view should be retrieved.
     * @return the {@link QueueViewMBean} allowing access to queue information.
     * @throws IOException when we were unable to connect using JMX.
     */
    public QueueViewMBean getQueueViewMBean(ObjectName name) throws IOException {
        return MBeanServerInvocationHandler.newProxyInstance(openConnection(), name, QueueViewMBean.class, true);
    }

    private MBeanServerConnection openConnection() {
        synchronized (CONNECTION_MONITOR) {
            String settingsURL = configurationService.getPlatformSettings().getJmxHost();
            if (connection == null || !settingsURL.equals(jmxCurrentHost)) {
                jmxCurrentHost = settingsURL;
                createConnection();
            }
            return this.connection;
        }
    }

    private void createConnection() {
        try {
            JMXConnector jmxc = JMXConnectorFactory.connect(new JMXServiceURL(getUrl()));
            connection = jmxc.getMBeanServerConnection();
        } catch (IOException ioEx) {
            throw new MotechException(String.format("JMX connection could not be acquired from url %s", getUrl()), ioEx);
        }
    }

    private String getUrl() {
        return "service:jmx:rmi:///jndi/rmi://" + jmxCurrentHost + ":1099/jmxrmi";
    }
}
