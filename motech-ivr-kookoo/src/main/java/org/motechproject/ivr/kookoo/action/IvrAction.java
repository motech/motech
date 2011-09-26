package org.motechproject.ivr.kookoo.action;

import org.apache.commons.lang.StringUtils;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.NodeInfo;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.server.decisiontree.DecisionTreeBasedResponseBuilder;
import org.motechproject.server.service.ivr.*;

import java.util.List;

public class IvrAction {

    private TreeChooser treeChooser;
    private IVRMessage ivrMessage;
    private DecisionTreeBasedResponseBuilder responseBuilder;

    public IvrAction(TreeChooser treeChooser, IVRMessage ivrMessage, DecisionTreeBasedResponseBuilder decisionTreeBasedResponseBuilder) {
        this.treeChooser = treeChooser;
        this.ivrMessage = ivrMessage;
        this.responseBuilder = decisionTreeBasedResponseBuilder;
    }

    public String handle(IVRRequest ivrRequest, IVRSession ivrSession) {
        IVRContext ivrContext = new IVRContext(ivrRequest, ivrSession);

        Tree tree = treeChooser.getTree(ivrContext);
        String userInput = StringUtils.remove(ivrRequest.getData(), BaseAction.POUND_SYMBOL);
        String currentPosition =  ivrSession.currentDecisionTreePath();

        NodeInfo nodeInfo = tree.nextNodeInfo(currentPosition, userInput);
        boolean retryOnIncorrectUserAction = StringUtils.isNotEmpty(currentPosition) && StringUtils.isEmpty(userInput);
        if (nodeInfo.node() == null) {
            nodeInfo = tree.currentNodeInfo(currentPosition);
            retryOnIncorrectUserAction = true;


        } else if (!retryOnIncorrectUserAction){
            List<ITreeCommand> treeCommands = nodeInfo.node().getTreeCommands();
            for(ITreeCommand command : treeCommands) {
                command.execute(ivrContext);
            }
        }

        ivrSession.currentDecisionTreePath(nodeInfo.path());
        IVRResponseBuilder ivrResponseBuilder = responseBuilder.ivrResponse(nodeInfo.node(), ivrContext, new KookooIVRResponseBuilder(), retryOnIncorrectUserAction);
        return ivrResponseBuilder.create(ivrMessage, ivrRequest.getSid(), ivrSession.getPreferredLanguageCode());
    }
}
