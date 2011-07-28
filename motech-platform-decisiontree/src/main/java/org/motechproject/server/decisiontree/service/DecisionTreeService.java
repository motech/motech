package org.motechproject.server.decisiontree.service;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Tree;

/**
 *
 */
public interface DecisionTreeService {
    public Node getNode(String treeName, String transitionPath);
    public Node getNode(Tree tree, String currentPosition, String userInput);
}
