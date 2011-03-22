package org.motechproject.context;

import org.motechproject.dao.PatientDao;
import org.motechproject.event.EventTypeRegistry;
import org.motechproject.server.event.EventListenerRegistry;
import org.motechproject.server.ruleengine.KnowledgeBaseManager;
import org.springframework.beans.factory.annotation.Autowired;

public class Context {
	
	@Autowired
	private EventListenerRegistry eventListenerRegistry;
	
	@Autowired
	private EventTypeRegistry eventTypeRegistry;
	
	@Autowired(required=false)
	private KnowledgeBaseManager knowledgeBaseManager;
	
	@Autowired(required=false)
	private PatientDao patientDao;

	public KnowledgeBaseManager getKnowledgeBaseManager() {
		return knowledgeBaseManager;
	}

	public void setKnowledgeBaseManager(KnowledgeBaseManager knowledgeBaseManager) {
		this.knowledgeBaseManager = knowledgeBaseManager;
	}

	public EventTypeRegistry getEventTypeRegistry() {
		return eventTypeRegistry;
	}

	public void setEventTypeRegistry(EventTypeRegistry eventTypeRegistry) {
		this.eventTypeRegistry = eventTypeRegistry;
	}

	public EventListenerRegistry getEventListenerRegistry() {
		return eventListenerRegistry;
	}

	public void setEventListenerRegistry(EventListenerRegistry eventListenerRegistry) {
		this.eventListenerRegistry = eventListenerRegistry;
	}

	public PatientDao getPatientDao() {
		return patientDao;
	}
	
	public void setPatientDao(PatientDao patientDao) {
		this.patientDao = patientDao;
	}
	
	public static Context getInstance(){
		return instance;
	}
	
	private static Context instance = new Context();
	
	private Context(){}

	
}
