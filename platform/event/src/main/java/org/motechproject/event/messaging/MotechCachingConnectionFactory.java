package org.motechproject.event.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.jms.connection.CachingConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;

/**
 * Represents an extension of the CachingConnectionFactory that adds username and password support,
 * in case the JMS broker is secured.
 */
public class MotechCachingConnectionFactory extends CachingConnectionFactory {

    private String username;
    private String password;

    /**
     * Sets the username.
     *
     * @param username the name of an user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the password.
     *
     * @param password the password of ActiveMQ
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the brokerURL of the ActiveMQ only when the <code>TargetConnectionFactory</code>
     * is instanceof ActiveMQConnectionFactory.
     *
     * @param brokerURL the brokerURL of the ActiveMQ
     */
    public void setBrokerUrl(String brokerURL) {
        if (getTargetConnectionFactory() instanceof ActiveMQConnectionFactory) {
            ((ActiveMQConnectionFactory) getTargetConnectionFactory()).setBrokerURL(brokerURL);
        }
    }

    /**
     * Creates a connection with the username and password if both not blank,
     * otherwise without them.
     */
    @Override
    protected Connection doCreateConnection() throws JMSException {
        if (StringUtils.isBlank(username) && StringUtils.isBlank(password)) {
            return getTargetConnectionFactory().createConnection();
        } else {
            return getTargetConnectionFactory().createConnection(username, password);
        }
    }
}
