package org.motechproject.ivr.kookoo.extensions;

import org.motechproject.decisiontree.model.Tree;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.springframework.stereotype.Component;

/**
 * Call flow controller exposes handlers for implementing IVR flows. Next call branching will be decided by
 * implementation of CallFlowController
 */
@Component
public interface CallFlowController {
    /**
     * Extension point for determining next URL for handling IVR Event in the call flow.
     * @param kooKooIVRContext context information.
     * @return
     */
    String urlFor(KooKooIVRContext kooKooIVRContext);

    /**
     * Get Next decision tree to serve base on current call status.
     * @param kooKooIVRContext
     * @return
     */
    String decisionTreeName(KooKooIVRContext kooKooIVRContext);

    /**
     * Lookup tree for given tree name. Generally from a registry.
     * @param treeName
     * @param kooKooIVRContext
     * @return Tree to serve
     */
    Tree getTree(String treeName, KooKooIVRContext kooKooIVRContext);

    /**
     * Mark tree as completed so that next call event will determine next tree.
     * @param treeName
     * @param kooKooIVRContext
     */
    void treeComplete(String treeName, KooKooIVRContext kooKooIVRContext);
}
