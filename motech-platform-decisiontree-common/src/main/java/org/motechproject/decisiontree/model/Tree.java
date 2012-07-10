package org.motechproject.decisiontree.model;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.server.decisiontree.TreeNodeLocator;

/**
 * Represents a decision tree.
 */
public class Tree extends MotechBaseDataObject {

    private static final long serialVersionUID = 1L;

    @TypeDiscriminator
    private String name;
    private String description;
    private Node rootNode;

    public String getName() {
        return name;
    }

    public Tree setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Tree setDescription(String description) {
        this.description = description;
        return this;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public Tree setRootNode(Node rootNode) {
        this.rootNode = rootNode;
        return this;
    }

    @Override
    public String toString() {
        return "Tree{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", rootNode=" + rootNode +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tree tree = (Tree) o;

        if (description != null ? !description.equals(tree.description) : tree.description != null) return false;
        if (name != null ? !name.equals(tree.name) : tree.name != null) return false;
        if (rootNode != null ? !rootNode.equals(tree.rootNode) : tree.rootNode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (rootNode != null ? rootNode.hashCode() : 0);
        return result;
    }

    public NodeInfo nextNodeInfo(String currentPosition, String transitionInput) {
        String currentPositionNotNull = currentPosition == null ? "" : currentPosition;
        String transitionInputNotNull = transitionInput == null ? "" : transitionInput;

        String path = String.format("%s/%s", currentPositionNotNull, transitionInputNotNull);
        Node node = new TreeNodeLocator().findNode(this, path);
        return new NodeInfo(path, node);
    }
}
