package org.motechproject.ivr.service;


import org.motechproject.ivr.domain.CallSessionRecord;

/**
 * Interface to IVR Dial out call. Originates call as per given call request.
 * See implementation module for more configuration information.
 */
public interface IVRSessionManagementService {

    public CallSessionRecord getCallSession(String sessionId);

    public void updateCallSession(CallSessionRecord callSessionRecord);

    public void removeCallSession(String sessionId);

    public boolean isValidSession(String sessionId);
}
