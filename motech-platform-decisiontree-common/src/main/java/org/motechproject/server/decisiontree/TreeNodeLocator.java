package org.motechproject.server.decisiontree;

import org.motechproject.decisiontree.model.ITransition;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.decisiontree.model.Tree;

/**
 * Locates a tree node by transitionPath.
 */
public class TreeNodeLocator {
    public static final String PATH_DELIMITER = "/";

    public Node findNode(Tree tree, String path) {
        if (tree == null || path == null) {
            throw new IllegalArgumentException(String.format("tree: %s path: %s", tree, path));
        }
        Node node = tree.getRootNode();
        if (node != null) {
            String[] keys = path.split(PATH_DELIMITER);
            for (String key : keys) {
                if (key.isEmpty()) continue;
                ITransition transition = node.getTransitions().get(key);
                if (transition == null) return null;
                node = transition.getDestinationNode(key);
                if (node == null) return null;
            }
        }
        return node;
    }
}
