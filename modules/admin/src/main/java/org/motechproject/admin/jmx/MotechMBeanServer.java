package org.motechproject.admin.jmx;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.broker.jmx.TopicViewMBean;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.service.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * The MBean server providing access to ActiveMQ MBeans. Uses a JMX connection.
 */
@Component
public class MotechMBeanServer {

    private static final Object CONNECTION_MONITOR = new Object();

    private static final Logger LOGGER = LoggerFactory.getLogger(MotechMBeanServer.class);

    private ConfigurationService configurationService;
    private MBeanServerConnection connection;
    private String jmxCurrentHost;
    private String destinationProperty;
    private String mBeanName;

    @Autowired
    public MotechMBeanServer(ConfigurationService configurationService) {
        this.configurationService = configurationService;

        //Default properties values for ActiveMQ 5.8+
        this.destinationProperty = "destinationName";
        this.mBeanName = "org.apache.activemq:type=Broker,brokerName=" + configurationService.getPlatformSettings().getJmxBroker();
    }

    /**
     * Retrieves the MBean view from the ActiveMQ broker.
     * @return a view into the broker MBeans.
     */
    public BrokerViewMBean getBrokerViewMBean() {
        try {
            return getBrokerViewMBean(mBeanName);
        } catch (UndeclaredThrowableException utEx) {
            //ActiveMQ version is <5.8, set beanName and destination properties to pre-5.8 values
            mBeanName = "org.apache.activemq:BrokerName=" + configurationService.getPlatformSettings().getJmxBroker() + ",Type=Broker";
            destinationProperty = "Destination";
            return getBrokerViewMBean(mBeanName);
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
            String destination = objectName.getKeyProperty(destinationProperty);
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

    /**
     * Returns name of destination property, which can change depending on ActiveMQ version.
     * @return correct name of destination property for used ActiveMQ version
     */
    public String getDestinationProperty() {
        return destinationProperty;
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
            HashMap environment = new HashMap();
            String[]  credentials = new String[] {configurationService.getPlatformSettings().getJmxUsername(),
                    configurationService.getPlatformSettings().getJmxPassword()};
            environment.put (JMXConnector.CREDENTIALS, credentials);
            JMXConnector jmxc = JMXConnectorFactory.connect(new JMXServiceURL(getUrl()), environment);
            connection = jmxc.getMBeanServerConnection();
        } catch (IOException ioEx) {
            throw new MotechException(String.format("JMX connection could not be acquired from url %s", getUrl()), ioEx);
        }
    }

    private String getUrl() {
        return "service:jmx:rmi:///jndi/rmi://" + jmxCurrentHost + ":1099/jmxrmi";
    }

    private BrokerViewMBean getBrokerViewMBean(String mBeanName) {
        try {
            ObjectName activeMQ = new ObjectName(mBeanName);
            BrokerViewMBean brokerViewMBean = MBeanServerInvocationHandler.newProxyInstance(openConnection(), activeMQ, BrokerViewMBean.class, true);
            LOGGER.info("Retrieving BrokerViewMBean from Broker version: " + brokerViewMBean.getBrokerVersion());
            return brokerViewMBean;
        } catch (MalformedObjectNameException ex) {
            throw new MotechException(ex.getMessage(), ex);
        }
    }
}
