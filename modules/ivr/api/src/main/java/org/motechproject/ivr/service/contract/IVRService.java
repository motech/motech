package org.motechproject.ivr.service.contract;


import org.motechproject.ivr.service.contract.CallRequest;

/**
 * Interface to IVR Dial out call. Originates call as per given call request.
 * See implementation module for more configuration information.
 */
public interface IVRService {

    String EXTERNAL_ID = "external_id";
    String CALL_TYPE = "call_type";

    /**
     * Sends an initiate call command to IVR. The call should be made to the phone specified in the given CallRequest
     *
     * @param callRequest - data required by IVR phone system to start outbound call
     */
    void initiateCall(CallRequest callRequest);

}
