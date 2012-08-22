package org.motechproject.decisiontree.model;

/**
 * Associates a node with its path on the decision tree.
 */
public class NodeInfo {
    private String path;
    private Node node;

    /**
     * Creates an instance of NodeInfo that stores the node details and the path of the node in the decision tree
     * @param path Path of the node in the decision tree
     * @param node the node instance, whose path information is specified by path parameter.
     */
    public NodeInfo(String path, Node node) {
        this.path = path;
        this.node = node;
    }

    /**
     * Returns the path of the node in the decision tree
     * @return path
     */
    public String path() {
        return path;
    }

    /**
     * Returns the node
     * @return node instance
     */
    public Node node() {
        return node;
    }
}
