package org.motechproject.ivr.service.impl;

import org.motechproject.ivr.domain.CallSessionRecord;
import org.motechproject.ivr.repository.AllCallSessionRecords;
import org.motechproject.ivr.service.IVRSessionManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IVRSessionManagementServiceImpl implements IVRSessionManagementService {

    private AllCallSessionRecords allCallSessionRecords;

    @Autowired
    public IVRSessionManagementServiceImpl(AllCallSessionRecords allCallSessionRecords) {
        this.allCallSessionRecords = allCallSessionRecords;
    }

    @Override
    public CallSessionRecord getCallSession(String sessionId) {
        return allCallSessionRecords.findOrCreate(sessionId);
    }

    @Override
    public void updateCallSession(CallSessionRecord callSessionRecord) {
        allCallSessionRecords.update(callSessionRecord);
    }

    @Override
    public void removeCallSession(String sessionId) {
        CallSessionRecord callSessionRecord = allCallSessionRecords.findBySessionId(sessionId);
        if(callSessionRecord != null)
            allCallSessionRecords.remove(callSessionRecord);
    }

    @Override
    public boolean isValidSession(String sessionId) {
        return allCallSessionRecords.findBySessionId(sessionId) != null;
    }
}
