package org.motechproject.decisiontree.core;

import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.decisiontree.core.repository.AllTrees;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Override
    public List<Tree> getDecisionTrees() {
        return allTrees.getAll();
    }

    @Override
    public Tree getDecisionTree(String treeId) {
        return allTrees.get(treeId);
    }

    @Override
    public void saveDecisionTree(final Tree tree) {
        allTrees.addOrReplace(tree);
    }

    @Override
    public void deleteDecisionTree(final String treeId) {
        Tree tree = allTrees.get(treeId);
        logger.info(String.format("Removing tree with name: %s and id: %s", tree.getName(), tree.getId()));
        allTrees.remove(tree);
    }
}
