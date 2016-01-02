package org.motechproject.server.bootstrap;

/**
 * Created by atish on 13/7/15.
 */
public interface MessageBrokerPingService {

     boolean pingBroker(String queueUrl);
}
