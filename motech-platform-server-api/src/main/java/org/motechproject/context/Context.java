/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.context;

import org.ektorp.CouchDbInstance;
import org.motechproject.metrics.MetricsAgent;
import org.motechproject.server.event.EventListenerRegistry;
import org.motechproject.server.event.EventRelay;
import org.motechproject.server.gateway.MotechSchedulerGateway;
import org.motechproject.server.ruleengine.KnowledgeBaseManager;
import org.motechproject.server.service.ivr.IVRService;
import org.springframework.beans.factory.annotation.Autowired;

public class Context {
	
	@Autowired
	private EventListenerRegistry eventListenerRegistry;
	
	@Autowired(required=false)
	private KnowledgeBaseManager knowledgeBaseManager;
	
	@Autowired(required=false)
	private MotechSchedulerGateway motechSchedulerGateway;
	
	@Autowired(required=false)
	private CouchDbInstance couchDbInstance;

    @Autowired(required=false)
    private MetricsAgent metricsAgent;

    @Autowired(required=false)
    private IVRService ivrService;

    @Autowired(required=false)
    private EventRelay eventRelay;

    public EventRelay getEventRelay() {
        return eventRelay;
    }

    public void setEventRelay(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    public IVRService getIvrService()
    {
        return ivrService;
    }

    public void setIvrService(IVRService ivrService)
    {
        this.ivrService = ivrService;
    }

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

	public KnowledgeBaseManager getKnowledgeBaseManager() {
		return knowledgeBaseManager;
	}

	public void setKnowledgeBaseManager(KnowledgeBaseManager knowledgeBaseManager) {
		this.knowledgeBaseManager = knowledgeBaseManager;
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
