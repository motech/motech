package org.motechproject.ivr.event;


import org.motechproject.ivr.calllog.domain.CallRecord;

public interface IVREventDelegate {

    String CALL_DETAIL_RECORD_KEY = "CallDetailRecord";

    void onSuccess(CallRecord cdr);

    void onNoAnswer(CallRecord cdr);

    void onBusy(CallRecord cdr);

    void onFailure(CallRecord cdr);
}
