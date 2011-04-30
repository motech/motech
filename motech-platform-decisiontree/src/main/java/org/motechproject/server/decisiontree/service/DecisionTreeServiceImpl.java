package org.motechproject.server.decisiontree.service;

import org.motechproject.decisiontree.dao.TreeDao;
import org.motechproject.decisiontree.model.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class DecisionTreeServiceImpl implements DecisionTreeService {

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    @Autowired
    TreeDao treeDao;


     //TODO - implement
    @Override
    public Node getNode(String treeId, String patientId) {

        //get the decision tree root node by tree ID


        //Interim implementation
        Node node = new Node();


        return node;
    }

     //TODO - implement
    @Override
    public Node getNode(String treeId, String nodeId, String transitionKey) {

        //Interim implementation
        Node node = new Node();


        return node;
    }
}
