package org.motechproject.ivr.service;


/**
 * Interface to IVR Dial out call. Originates call as per given call request.
 * See implementation module for more configuration information.
 */
public interface IVRService {

    public static final String EXTERNAL_ID = "external_id";
    public static final String CALL_TYPE = "call_type";

    /**
     * Sends an initiate call command to IVR. The call should be made to the phone specified in the given CallRequest
     *
     * @param callRequest - data required by IVR phone system to start outbound call
     * @throws org.motechproject.ivr.domain.CallInitiationException
     *          if the call can not be initiated
     */
    public void initiateCall(CallRequest callRequest);

}
