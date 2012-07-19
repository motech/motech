package org.motechproject.decisiontree.service.impl;

import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.domain.FlowSessionRecord;
import org.motechproject.decisiontree.service.FlowSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("flowSessionService")
public class FlowSessionServiceImpl implements FlowSessionService {

    private AllFlowSessionRecords allFlowSessionRecords;

    @Autowired
    public FlowSessionServiceImpl(AllFlowSessionRecords allFlowSessionRecords) {
        this.allFlowSessionRecords = allFlowSessionRecords;
    }

    @Override
    public FlowSession getSession(String sessionId) {
        return allFlowSessionRecords.findOrCreate(sessionId);
    }

    @Override
    public void updateSession(FlowSession flowSession) {
        allFlowSessionRecords.update((FlowSessionRecord) flowSession);
    }

    @Override
    public void removeCallSession(String sessionId) {
        FlowSessionRecord flowSessionRecord = allFlowSessionRecords.findBySessionId(sessionId);
        if(flowSessionRecord != null)
            allFlowSessionRecords.remove(flowSessionRecord);
    }

    @Override
    public boolean isValidSession(String sessionId) {
        return allFlowSessionRecords.findBySessionId(sessionId) != null;
    }

    @Override
    public FlowSession updateSessionId(String sessionId, String newSessionId) {
        FlowSessionRecord flowSession = allFlowSessionRecords.findOrCreate(sessionId);
        flowSession.setSessionId(newSessionId);
        allFlowSessionRecords.update(flowSession);
        return flowSession;
    }
}
