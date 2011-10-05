package org.motechproject.server.service.ivr;

public interface PostTreeCallContinuation {
    void continueCall(IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder);
}