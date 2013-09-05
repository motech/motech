package org.motechproject.event;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.jms.connection.CachingConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;

/**
 * The <code>MotechCachingConnectionFactory</code> is used to created connection to ActiveMQ.
 */
public class MotechCachingConnectionFactory extends CachingConnectionFactory {

    private String username;
    private String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBrokerUrl(String brokerURL) {
        if (getTargetConnectionFactory() instanceof ActiveMQConnectionFactory) {
            ((ActiveMQConnectionFactory) getTargetConnectionFactory()).setBrokerURL(brokerURL);
        }
    }

    @Override
    protected Connection doCreateConnection() throws JMSException {
        if (StringUtils.isBlank(username) && StringUtils.isBlank(password)) {
            return getTargetConnectionFactory().createConnection();
        } else {
            return getTargetConnectionFactory().createConnection(username, password);
        }
    }
}
