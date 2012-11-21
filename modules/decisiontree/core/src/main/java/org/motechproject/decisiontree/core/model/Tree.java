package org.motechproject.decisiontree.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

/**
 * Represents a decision tree.
 */
public class Tree extends MotechBaseDataObject {

    private static final long serialVersionUID = 1L;

    @TypeDiscriminator
    private String name;
    private String description;
    private ITransition rootTransition;

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

    /**
     * @deprecated will be removed in 0.13 see {@link #getRootTransition()}
     * @return
     */
    @Deprecated
    @JsonIgnore
    public Node getRootNode() {
        return rootTransition.getDestinationNode(null, null);
    }

    /**
     * @deprecated will be removed in 0.13 see {@link #setRootTransition(ITransition)}
     * @param rootNode
     * @return
     */
    @Deprecated
    @JsonIgnore
    public Tree setRootNode(Node rootNode) {
        this.rootTransition = new Transition().setDestinationNode(rootNode);
        return this;
    }

    /**
     * Set Transition or custom transition implementation.
     * @param transition
     * @since 0.12
     * @return
     */
    public Tree setRootTransition(ITransition transition) {
        this.rootTransition = transition;
        return this;
    }

    /**
     * get root transition of tree that determines root node of tree.
     * @return
     * @since 0.12
     */
    public ITransition getRootTransition() {
        return rootTransition;
    }

    @Override
    public String toString() {
        return "Tree{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", rootTransition=" + rootTransition +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tree tree = (Tree) o;

        if (description != null ? !description.equals(tree.description) : tree.description != null) {
            return false;
        }
        if (name != null ? !name.equals(tree.name) : tree.name != null) {
            return false;
        }
        if (rootTransition != null ? !rootTransition.equals(tree.rootTransition) : tree.rootTransition != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (rootTransition != null ? rootTransition.hashCode() : 0);
        return result;
    }
}
