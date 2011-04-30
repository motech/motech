package org.motechproject.server.tama.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.runtime.StatelessKnowledgeSession;
import org.motechproject.context.Context;
import org.motechproject.server.ruleengine.KnowledgeBaseManager;
import org.motechproject.server.tama.service.DecisionTreeLookupService;
import org.motechproject.tama.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Drools based implementation of DecisionTreeLookupService
 * 
 * @author Ricky
 */
public class DroolsBasedDecisionTreeLookupService implements DecisionTreeLookupService{
	
	private Logger logger = LoggerFactory.getLogger((this.getClass()));

	private String ruleId;
	
	private KnowledgeBaseManager knowledgeBaseManager = Context.getInstance().getKnowledgeBaseManager();

	@Override
	public String findTreeNameByPatient(Patient patient){
		String treeName = null;
		KnowledgeBase knowledgeBase = knowledgeBaseManager.getKnowledgeBase(ruleId);
		if (knowledgeBase != null) {
			StatelessKnowledgeSession ksession = knowledgeBase.newStatelessKnowledgeSession();
	        Map<String, String> resultMap = new HashMap<String, String>();
	        ksession.setGlobal("resultMap", resultMap);
			ksession.execute(patient);
			treeName = resultMap.get("treeName");
		} 
		logger.debug("Patient[" + patient.getId() + "] - Decision Tree[" + treeName + "]");
		return treeName;
		
	}
	
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	
	public void setKnowledgeBaseManager(KnowledgeBaseManager knowledgeBaseManager) {
		this.knowledgeBaseManager = knowledgeBaseManager;
	}

}
