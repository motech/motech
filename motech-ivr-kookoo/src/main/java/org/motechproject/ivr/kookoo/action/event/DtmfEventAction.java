package org.motechproject.ivr.kookoo.action.event;

import org.motechproject.ivr.kookoo.action.AuthenticateAction;
import org.motechproject.ivr.kookoo.action.IvrAction;
import org.motechproject.ivr.kookoo.action.TreeChooser;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.server.decisiontree.DecisionTreeBasedResponseBuilder;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class DtmfEventAction extends BaseEventAction {

    @Autowired
    private AuthenticateAction authenticateAction;

    @Autowired
    private TreeChooser treeChooser;

    @Autowired
    private DecisionTreeBasedResponseBuilder responseBuilder;

    public DtmfEventAction() {
    }

    public DtmfEventAction(AuthenticateAction authenticateAction) {
        this.authenticateAction = authenticateAction;
    }

    @Override
    public String createResponse(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        return new IvrAction(treeChooser, messages, responseBuilder).handle(ivrRequest, ivrSession);
    }

    @Override
    public String handle(String callId, IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        if (ivrSession.isAuthentication()) {
            return authenticateAction.handle(callId, ivrRequest, request, response);
        } else {
            return super.handle(callId, ivrRequest, request, response);
        }
    }

    @Override
    protected Map<String, String> callEventData(final IVRRequest ivrRequest) {
        return new HashMap<String, String>() {
            {
                put(CallEventConstants.DTMF_DATA, ivrRequest.getData());
            }
        };
    }
}
