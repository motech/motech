package org.motechproject.ivr.kookoo.extensions;

import org.motechproject.decisiontree.model.Tree;
import org.motechproject.ivr.kookoo.KooKooIVRContext;

public class CallFlowControllerImpl implements CallFlowController {
    @Override
    public String urlFor(KooKooIVRContext kooKooIVRContext) {
        return null;
    }

    @Override
    public String decisionTreeName(KooKooIVRContext kooKooIVRContext) {
        return null;
    }

    @Override
    public Tree getTree(String treeName, KooKooIVRContext kooKooIVRContext) {
        return null;
    }

    @Override
    public void treeComplete(String treeName, KooKooIVRContext kooKooIVRContext) {
    }
}
