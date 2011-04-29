package org.motechproject.server.decisiontree.service;

import org.motechproject.decisiontree.model.Node;

/**
 *
 */
public interface DecisionTreeService {

    public Node getNode(String treeId, String nodeId);

    public Node getNode(String treeId, String nodeId, String transitionName);
}
