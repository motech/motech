package org.motechproject.server.service.ivr;

public class IVRContext {
    private IVRRequest ivrRequest;
    private IVRSession ivrSession;

    public IVRContext(IVRRequest ivrRequest, IVRSession ivrSession) {
        this.ivrRequest = ivrRequest;
        this.ivrSession = ivrSession;
    }

    public IVRRequest ivrRequest() {
        return ivrRequest;
    }

    public IVRSession ivrSession() {
        return ivrSession;
    }
}
