package org.motechproject.admin.jmx;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.motechproject.commons.api.MotechException;
import org.motechproject.server.config.SettingsFacade;
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

@Component
public class MotechMBeanServer {
    public static final String DESTINATION = "Destination";
    public static final String JMX_URL = "jmx.url";

    public static final String M_BEAN_NAME = "org.apache.activemq:BrokerName=localhost,Type=Broker";
    private static final Object CONNECTION_MONITOR = new Object();
    private MBeanServerConnection connection;

    private SettingsFacade settingsFacade;


    @Autowired
    public MotechMBeanServer(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }


    public BrokerViewMBean getBrokerViewMBean() {
        try {
            ObjectName activeMQ = new ObjectName(M_BEAN_NAME);
            return MBeanServerInvocationHandler.newProxyInstance(openConnection(), activeMQ, BrokerViewMBean.class, true);
        } catch (MalformedObjectNameException ex) {
            throw new MotechException(ex.getMessage(), ex);
        }
    }

    public ObjectName[] getQueues() {
        BrokerViewMBean brokerViewMBean = getBrokerViewMBean();
        return brokerViewMBean.getQueues();
    }

    public QueueViewMBean getQueueViewMBean(String queueName) throws IOException {
        for (ObjectName objectName : getQueues()) {
            String destination = objectName.getKeyProperty(DESTINATION);
            if (isNotBlank(destination) && destination.equals(queueName)) {
                return getQueueViewMBean(objectName);
            }
        }
        return null;
    }

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
