package org.motechproject.context;

import org.ektorp.CouchDbInstance;
import org.motechproject.metrics.MetricsAgent;
import org.motechproject.scheduler.gateway.MotechSchedulerGateway;
import org.motechproject.server.event.EventListenerRegistry;
import org.springframework.beans.factory.annotation.Autowired;

public class Context {

    @Autowired
    private EventListenerRegistry eventListenerRegistry;

    @Autowired(required=false)
    private MotechSchedulerGateway motechSchedulerGateway;

    @Autowired(required = false)
    private CouchDbInstance couchDbInstance;

    @Autowired(required=false)
    private MetricsAgent metricsAgent;

    public MetricsAgent getMetricsAgent() {
        return metricsAgent;
    }

    public void setMetricsAgent(MetricsAgent metricsAgent) {
        this.metricsAgent = metricsAgent;
    }

    public CouchDbInstance getCouchDbInstance(){
        return couchDbInstance;
    }

    public void setCouchDbInstance(CouchDbInstance couchDbInstance) {
        this.couchDbInstance = couchDbInstance;
    }

    public MotechSchedulerGateway getMotechSchedulerGateway() {
        return motechSchedulerGateway;
    }

    public void setMotechSchedulerGateway(
            MotechSchedulerGateway motechSchedulerGateway) {
        this.motechSchedulerGateway = motechSchedulerGateway;
    }


    public EventListenerRegistry getEventListenerRegistry() {
        return eventListenerRegistry;
    }

    public void setEventListenerRegistry(EventListenerRegistry eventListenerRegistry) {
        this.eventListenerRegistry = eventListenerRegistry;
    }

    public static Context getInstance(){
        return instance;
    }

    private static Context instance = new Context();

    private Context(){}


}
