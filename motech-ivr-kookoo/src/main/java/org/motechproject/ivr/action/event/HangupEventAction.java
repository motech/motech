package org.motechproject.ivr.action.event;

import org.motechproject.server.service.ivr.IVRRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class HangupEventAction extends BaseEventAction {
    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public void postHandle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        getIVRSession(request).close();
        return;
    }
}
