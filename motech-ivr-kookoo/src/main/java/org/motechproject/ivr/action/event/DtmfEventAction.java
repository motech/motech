package org.motechproject.ivr.action.event;

import org.motechproject.ivr.action.AuthenticateAction;
import org.motechproject.ivr.action.IvrAction;
import org.motechproject.ivr.action.TreeChooser;
import org.motechproject.ivr.eventlogging.EventLogConstants;
import org.motechproject.server.decisiontree.DecisionTreeBasedResponseBuilder;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class DtmfEventAction extends BaseEventAction {
    private AuthenticateAction authenticateAction;
    private TreeChooser treeChooser;
    private DecisionTreeBasedResponseBuilder responseBuilder;
 
    @Autowired
    public DtmfEventAction(AuthenticateAction authenticateAction,
                           TreeChooser treeChooser, DecisionTreeBasedResponseBuilder ivrResponseBuilder) {
        this.authenticateAction = authenticateAction;
        this.treeChooser = treeChooser;
        this.responseBuilder = ivrResponseBuilder;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        addEventLogData(EventLogConstants.DTMF_DATA, ivrRequest.getData());
        if (ivrSession.isAuthentication()) {
            return authenticateAction.handle(ivrRequest, request, response);
        } else {
            return new IvrAction(treeChooser, messages, responseBuilder).handle(ivrRequest, ivrSession);
        }
    } 
}
