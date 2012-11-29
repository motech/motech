package org.motechproject.decisiontree.core;

import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.decisiontree.core.repository.AllTrees;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.String.format;

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
        Node node = treeNodeLocator.findNode(findTreeByName(treeName), path, session);
        logger.info(format("Looking for node by path: %s, found: %s", path, node.getPrompts()));

        return node;
    }

    @Override
    public Node getRootNode(String treeName, FlowSession session) {
        Node node = treeNodeLocator.findRootNode(findTreeByName(treeName), session);
        logger.info(format("Looking for node by path: , found: %s", node.getPrompts()));

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
        logger.info(format("Removing tree with name: %s and id: %s", tree.getName(), tree.getId()));
        allTrees.remove(tree);
    }

    private Tree findTreeByName(String treeName) {
        Tree tree = allTrees.findByName(treeName);
        logger.info(format("Looking for tree by name: %s, found: %s", treeName, tree));
        return tree;
    }
}
