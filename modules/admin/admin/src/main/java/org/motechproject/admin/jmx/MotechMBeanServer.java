package org.motechproject.admin.jmx;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.motechproject.commons.api.MotechException;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public static final String JMX_URL = "jmx.url";

    public static final String M_BEAN_NAME = "org.apache.activemq:BrokerName=localhost,Type=Broker";
    private static final Object CONNECTION_MONITOR = new Object();
    private MBeanServerConnection connection;

    private SettingsFacade settingsFacade;


    @Autowired
    public MotechMBeanServer(@Qualifier("jmxSettings") SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    /**
     * Retrieves the mbean view from the activemq broker.
     * @return a view into the broker MBeans.
     */
    public BrokerViewMBean getBrokerViewMBean() {
        try {
            ObjectName activeMQ = new ObjectName(M_BEAN_NAME);
            return MBeanServerInvocationHandler.newProxyInstance(openConnection(), activeMQ, BrokerViewMBean.class, true);
        } catch (MalformedObjectNameException ex) {
            throw new MotechException(ex.getMessage(), ex);
        }
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
            if (connection == null) {
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
        return settingsFacade.getProperty(JMX_URL);
    }
}
