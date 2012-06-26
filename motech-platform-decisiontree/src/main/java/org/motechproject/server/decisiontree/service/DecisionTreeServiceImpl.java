package org.motechproject.server.decisiontree.service;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.decisiontree.repository.AllTrees;
import org.motechproject.server.decisiontree.TreeNodeLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DecisionTreeServiceImpl implements DecisionTreeService {
    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    AllTrees allTrees;
    TreeNodeLocator treeNodeLocator;

    @Autowired
    public DecisionTreeServiceImpl(AllTrees allTrees, TreeNodeLocator treeNodeLocator) {
        this.allTrees = allTrees;
        this.treeNodeLocator = treeNodeLocator;
    }

    @Override
    public Node getNode(String treeName, String path) {
        Node node = null;
        Tree tree = allTrees.findByName(treeName);
        logger.info("Looking for tree by name: " + treeName + ", found: " + tree);
        node = treeNodeLocator.findNode(tree, path);
        logger.info("Looking for node by path: " + path + ", found: " + node);

        return node;
    }
}
