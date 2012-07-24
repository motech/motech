package org.motechproject.ivr.event;

import org.motechproject.ivr.model.CallDetailRecord;

public interface IVREventDelegate {

    String CALL_DETAIL_RECORD_KEY = "CallDetailRecord";

    void onSuccess(CallDetailRecord cdr);

    void onNoAnswer(CallDetailRecord cdr);

    void onBusy(CallDetailRecord cdr);

    void onFailure(CallDetailRecord cdr);
}
