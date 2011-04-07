package org.motechproject.server.service.ivr;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: 4/7/11
 * Time: 3:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IVREventDelegate
{
    public final static String CALL_DETAIL_RECORD_KEY = "CallDetailRecord";

    public void onSuccess(CallDetailRecord cdr);

    public void onNoAnswer(CallDetailRecord cdr);

    public void onBusy(CallDetailRecord cdr);

    public void onFailure(CallDetailRecord cdr);
}
