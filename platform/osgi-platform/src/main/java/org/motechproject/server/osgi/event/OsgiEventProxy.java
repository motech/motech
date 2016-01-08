package org.motechproject.server.osgi.event;


import java.util.Map;

/**
 * This service allows sending Motech events without having a direct dependency on the event. This is achieved
 * by sending OSGi events, which are then relayed as Motech events by the event module. This mechanism is used by
 * MDS in order to avoid a dependency on the event module. In normal use, using this service should be avoided, as
 * using the event system directly should be cleaner and more efficient.
 */
public interface OsgiEventProxy {

    String PROXY_EVENT_TOPIC = "org/motechproject/osgi/event/PROXY";
    String SUBJECT_PARAM = "subject";
    String PARAMETERS_PARAM = "parameters";
    String BROADCAST_PARAM = "broadcast";
    String PROXY_ON_RECEIVING_END_PARAM = "proxyOnReceivingEnd";

    /**
     * Calling this method will result in sending an OSGi event that will be then relayed by the event module
     * as a Motech Event through the event queue - only one Motech instance will receive the event.
     * @param subject the subject of the event
     */
    void sendEvent(String subject);

    /**
     * Calling this method will result in sending an OSGi event that will be then relayed by the event module
     * as a Motech Event through the event queue - only one Motech instance will receive the event.
     * @param subject the subject of the event
     * @param parameters the parameters map which will act as the payload of the event
     */
    void sendEvent(String subject, Map<String, Object> parameters);

    /**
     * Calling this method will result in sending an OSGi event that will be then relayed by the event module
     * as a Motech Event through the event topic - all Motech instances will receive the event.
     * Note that broadcast events can be relayed as OSGi events upon being received - if that's the case, their subject
     * must conform to the rules for OSGi event topic (i.e. not contain dots).
     * @param subject the subject of the event
     * @param proxyHandledEventInOSGi if true, the event will be also sent as an OSGi event upon being received by the event system
     */
    void broadcastEvent(String subject, boolean proxyHandledEventInOSGi);

    /**
     * Calling this method will result in sending an OSGi event that will be then relayed by the event module
     * as a Motech Event through the event topic - all Motech instances will receive the event.
     * Note that broadcast events can be relayed as OSGi events upon being received - if that's the case, their subject
     * must conform to the rules for OSGi event topic (i.e. not contain dots).
     * @param subject the subject of the event
     * @param parameters the parameters map which will act as the payload of the event
     * @param proxyHandledEventInOSGi if true, the event will be also sent as an OSGi event upon being received by the event system
     */
    void broadcastEvent(String subject, Map<String, Object> parameters, boolean proxyHandledEventInOSGi);
}
