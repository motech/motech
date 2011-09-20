package org.motechproject.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseAction {

    @Autowired
    protected IVRMessage messages;

    public static final String POUND_SYMBOL = "%23";

    protected String hangUpResponseWith(IVRRequest ivrRequest) {
        return new KookooIVRResponseBuilder().withHangUp().createWithDefaultLanguage(messages, ivrRequest.getSessionId());
    }

    protected String dtmfResponseWithWav(IVRRequest ivrRequest, String wavFile) {
        return new KookooIVRResponseBuilder().collectDtmf().withPlayAudios(wavFile).createWithDefaultLanguage(messages, ivrRequest.getSessionId() );
    }

    protected IVRSession getIVRSession(HttpServletRequest request) {
        return new IVRSession(request.getSession(false));
    }

    protected IVRSession createIVRSession(HttpServletRequest request) {
        return new IVRSession(request.getSession());
    }

    abstract public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response);
    public String getKey() {
        return StringUtils.EMPTY;
    }
}
