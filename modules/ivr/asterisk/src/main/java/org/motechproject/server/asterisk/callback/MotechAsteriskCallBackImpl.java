package org.motechproject.server.asterisk.callback;

import org.asteriskjava.live.AsteriskChannel;
import org.asteriskjava.live.Disposition;
import org.asteriskjava.live.LiveException;
import org.asteriskjava.live.OriginateCallback;
import org.motechproject.decisiontree.server.domain.CallDetailRecord;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.ivr.event.IVREventDelegate;
import org.motechproject.ivr.service.CallRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Motech specific implementation of the Asterisk-Java call-back interface
 * see org.asteriskjava.live.OriginateCallback.java for details
 */
@Component
public class MotechAsteriskCallBackImpl implements OriginateCallback {

    @Autowired
    private EventRelay eventRelay;

    private CallRequest callRequest;

    public MotechAsteriskCallBackImpl(){}

    public MotechAsteriskCallBackImpl(CallRequest callRequest) {
        this.callRequest = callRequest;
    }

    @Override
    public void onDialing(AsteriskChannel asteriskChannel) {
    }

    @Override
    public void onSuccess(AsteriskChannel asteriskChannel) {
        MotechEvent event = callRequest.getOnSuccessEvent();

        if (event != null) {
            org.asteriskjava.live.CallDetailRecord aCDR = asteriskChannel.getCallDetailRecord();
            CallDetailRecord cdr = new CallDetailRecord(aCDR.getStartDate(), aCDR.getEndDate(), aCDR.getAnswerDate(),
                    translateDisposition(aCDR.getDisposition()), aCDR.getDuration());

            Map<String, Object> parameters = event.getParameters();
            parameters.put(IVREventDelegate.CALL_DETAIL_RECORD_KEY, cdr);

            eventRelay.sendEventMessage(event);
        }
    }

    @Override
    public void onNoAnswer(AsteriskChannel asteriskChannel) {
        MotechEvent event = callRequest.getOnNoAnswerEvent();

        if (event != null) {
            org.asteriskjava.live.CallDetailRecord aCDR = asteriskChannel.getCallDetailRecord();
            CallDetailRecord cdr = new CallDetailRecord(aCDR.getStartDate(), aCDR.getEndDate(), aCDR.getAnswerDate(),
                    translateDisposition(aCDR.getDisposition()), aCDR.getDuration());

            Map<String, Object> parameters = event.getParameters();
            parameters.put(IVREventDelegate.CALL_DETAIL_RECORD_KEY, cdr);

            eventRelay.sendEventMessage(event);
        }

    }

    @Override
    public void onBusy(AsteriskChannel asteriskChannel) {
        MotechEvent event = callRequest.getOnBusyEvent();

        if (event != null) {
            org.asteriskjava.live.CallDetailRecord aCDR = asteriskChannel.getCallDetailRecord();
            CallDetailRecord cdr = new CallDetailRecord(aCDR.getStartDate(), aCDR.getEndDate(), aCDR.getAnswerDate(),
                    translateDisposition(aCDR.getDisposition()), aCDR.getDuration());

            Map<String, Object> parameters = event.getParameters();
            parameters.put(IVREventDelegate.CALL_DETAIL_RECORD_KEY, cdr);

            eventRelay.sendEventMessage(event);
        }

    }

    @Override
    public void onFailure(LiveException e) {
        MotechEvent event = callRequest.getOnFailureEvent();

        if (event != null) {
            CallDetailRecord cdr = new CallDetailRecord(CallDetailRecord.Disposition.FAILED, e.getMessage());

            Map<String, Object> parameters = event.getParameters();
            parameters.put(IVREventDelegate.CALL_DETAIL_RECORD_KEY, cdr);

            eventRelay.sendEventMessage(event);
        }

    }

    private CallDetailRecord.Disposition translateDisposition(Disposition disposition) {
        CallDetailRecord.Disposition ret = CallDetailRecord.Disposition.UNKNOWN;

        if (disposition == Disposition.BUSY) {
            ret = CallDetailRecord.Disposition.BUSY;
        }

        if (disposition == Disposition.ANSWERED) {
            ret = CallDetailRecord.Disposition.ANSWERED;
        }

        if (disposition == Disposition.NO_ANSWER) {
            ret = CallDetailRecord.Disposition.NO_ANSWER;
        }

        if (disposition == Disposition.FAILED) {
            ret = CallDetailRecord.Disposition.FAILED;
        }

        return ret;
    }
}
