package org.motechproject.decisiontree.core;

import org.motechproject.decisiontree.core.model.ITransition;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Locates a tree node by transitionPath.
 */
@Component
public class TreeNodeLocator {
    public static final String PATH_DELIMITER = "/";
    public static final String ANY_KEY = "?";

    @Autowired
    private ApplicationContext applicationContext;
    public static final String NO_INPUT = "";

    public TreeNodeLocator() {
    }

    public Node findNode(Tree tree, String path, FlowSession session) {
        if (tree == null || path == null) {
            throw new IllegalArgumentException(String.format("tree: %s path: %s", tree, path));
        }
        Node node = tree.getRootTransition().getDestinationNode(null, session);
        if (node != null) {
            String[] keys = path.split(PATH_DELIMITER);
            for (String key : keys) {
                if (key.isEmpty()) {
                    continue;
                }
                ITransition transition = node.getTransitions().get(key);
                if (transition == null) {
                    transition = node.getTransitions().get(ANY_KEY);
                }
                if (transition == null) {
                    return null;
                }
                autowire(transition); //TODO : autowiring in 2 places, see - DecistionTreeController

                node = transition.getDestinationNode(key, session);
                autowire(node);
                if (node == null) {
                    return null;
                }
            }
        }
        return node;
    }

    public Node findRootNode(Tree tree, FlowSession session) {
        if (tree == null ) {
            throw new IllegalArgumentException(String.format("tree: %s", tree));
        }
        ITransition rootTransition = rootTransition(tree);
        return rootNode(session, rootTransition);
    }

    private Node rootNode(FlowSession session, ITransition rootTransition) {
        Node destinationNode = rootTransition.getDestinationNode(null, session);
        return destinationNode;
    }

    private ITransition rootTransition(Tree tree) {
        ITransition rootTransition = tree.getRootTransition();
        autowire(rootTransition);
        return rootTransition;
    }

    private void autowire(Object rootTransition) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(rootTransition);
    }
}
