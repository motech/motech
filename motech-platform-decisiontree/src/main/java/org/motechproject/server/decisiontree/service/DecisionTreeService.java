package org.motechproject.server.decisiontree.service;

import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.Node;

/**
 * \defgroup decisionTree Decision Tree
 */

/**
 * \ingroup decisionTree
 * Provides mechanism to navigate through a decision tree.
 */
public interface DecisionTreeService {
    /**
     * Fetches the node from a decision tree based on the transition path.
     *
     *
     * @param treeName       Name of the decision tree on which the node needs to be located
     * @param transitionPath The traversal path on the tree, starting from root (/)
     * @param session
     * @return Node, if found. Otherwise, null.
     */
    Node getNode(String treeName, String transitionPath, FlowSession session);
}
