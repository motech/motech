package org.motechproject.server.service.ivr;


import org.motechproject.model.InitiateCallData;

/**
 *  Interface to IVR System
 *
 *
 */
public interface IVRService {

    /**
     * Sends an initiate call command to IVR. The call should be made to the phone specified in the given InitiateCallData
     *
     *
     * @param initiateCallData - data required by IVR phone system to start outbound call
     * @throws CallInitiationException if the call can not be initiated
     */
    public void initiateCall(InitiateCallData initiateCallData);


}
