package org.motechproject.server.decisiontree.service;

import org.motechproject.decisiontree.model.Node;

/**
 *
 */
public interface DecisionTreeService {

    public Node getNode(String treeId, String patientId);

    public Node getNode(String treeId, String nodeId, String transitionKey);
}
