package org.motechproject.decisiontree.server.service;

import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.decisiontree.core.repository.AllTrees;
import org.motechproject.decisiontree.core.TreeNodeLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DecisionTreeServiceImpl implements DecisionTreeService {
    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    private AllTrees allTrees;
    private TreeNodeLocator treeNodeLocator;

    @Autowired
    public DecisionTreeServiceImpl(AllTrees allTrees, TreeNodeLocator treeNodeLocator) {
        this.allTrees = allTrees;
        this.treeNodeLocator = treeNodeLocator;
    }

    @Override
    public Node getNode(String treeName, String path, FlowSession session) {
        Node node = null;
        Tree tree = allTrees.findByName(treeName);
        logger.info("Looking for tree by name: " + treeName + ", found: " + tree);
        node = treeNodeLocator.findNode(tree, path, session);
        logger.info("Looking for node by path: " + path + ", found: " + node.getPrompts());

        return node;
    }

    @Override
    public Node getRootNode(String treeName, FlowSession session) {
        Node node = null;
        Tree tree = allTrees.findByName(treeName);
        logger.info("Looking for tree by name: " + treeName + ", found: " + tree);
        node = treeNodeLocator.findRootNode(tree, session);
        logger.info("Looking for node by path: " + ", found: " + node.getPrompts());

        return node;
    }
}
