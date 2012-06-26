package org.motechproject.ivr.kookoo.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.NodeInfo;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.domain.DecisionTreeBasedResponseBuilder;
import org.motechproject.ivr.kookoo.extensions.CallFlowController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.domain.IVRMessage;
import org.motechproject.ivr.service.IVRSessionManagementService;
import org.motechproject.server.decisiontree.TreeNodeLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(AllIVRURLs.DECISION_TREE_URL)
public class DecisionTreeBasedIVRController extends SafeIVRController {
    private CallFlowController callFlowController;

    @Autowired
    TreeNodeLocator treeNodeLocator;

    @Autowired
    public DecisionTreeBasedIVRController(CallFlowController callFlowController, IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, StandardResponseController standardResponseController, IVRSessionManagementService ivrSessionManagementService) {
        super(ivrMessage, callDetailRecordsService, standardResponseController, ivrSessionManagementService);
        this.callFlowController = callFlowController;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        String currentTreeName = kooKooIVRContext.treeName();
        String userInput = kooKooIVRContext.userInput();
        String currentPosition = kooKooIVRContext.currentDecisionTreePath();
        Tree tree = callFlowController.getTree(currentTreeName, kooKooIVRContext);

        NodeInfo nodeInfo = nextNodeInfo(tree, currentPosition, userInput);
        boolean retryOnIncorrectUserAction = StringUtils.isNotEmpty(currentPosition) && StringUtils.isEmpty(userInput);
        if (nodeInfo.node() == null) {
            nodeInfo = currentPosition(currentPosition, tree);
            retryOnIncorrectUserAction = true;
        }
        if (!retryOnIncorrectUserAction) {
            List<ITreeCommand> treeCommands = nodeInfo.node().getTreeCommands();
            for (ITreeCommand command : treeCommands) {
                command.execute(kooKooIVRContext);
            }
        }
        kooKooIVRContext.currentDecisionTreePath(nodeInfo.path());

        DecisionTreeBasedResponseBuilder decisionTreeBasedResponseBuilder = new DecisionTreeBasedResponseBuilder();
        KookooIVRResponseBuilder ivrResponseBuilder = decisionTreeBasedResponseBuilder.ivrResponse(nodeInfo.node(), kooKooIVRContext, new KookooIVRResponseBuilder(), retryOnIncorrectUserAction);
        if (!ivrResponseBuilder.isCollectDTMF()){
            kooKooIVRContext.currentDecisionTreePath("");
            callFlowController.treeComplete(currentTreeName, kooKooIVRContext);
        }
        return ivrResponseBuilder.withSid(kooKooIVRContext.callId()).language(kooKooIVRContext.preferredLanguage());
    }

    private NodeInfo nextNodeInfo(Tree tree, String currentPosition, String userInput) {
        String transitionInput = (userInput == null ? "" : userInput);
        final String currentPositionPath = currentPosition == null ? "" : currentPosition;
        String path = String.format("%s/%s", currentPositionPath, transitionInput);
        Node node = treeNodeLocator.findNode(tree, path);
        return new NodeInfo(path, node);
    }

    private NodeInfo currentPosition(String currentPosition, Tree tree) {
        final String path = currentPosition == null ? "" : currentPosition;
        Node node = treeNodeLocator.findNode(tree, path);
        return new NodeInfo(path, node);
    }

    void setTreeNodeLocator(TreeNodeLocator treeNodeLocator) {
        this.treeNodeLocator = treeNodeLocator;
    }
}