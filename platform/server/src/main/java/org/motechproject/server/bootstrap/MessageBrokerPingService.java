package org.motechproject.server.bootstrap;


public interface MessageBrokerPingService {

    boolean pingBroker(String queueUrl);
}
