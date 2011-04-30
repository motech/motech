package org.motechproject.decisiontree.model;


import java.util.List;

/**
 *
 */
public class Transition {

    private String name;
    private Node destinationNode;
    private List<Action> actions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Node getDestinationNode() {
        return destinationNode;
    }

    public void setDestinationNode(Node destinationNode) {
        this.destinationNode = destinationNode;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public String toString() {
        return "Transition{" +
                "name='" + name + '\'' +
                ", destinationNode=" + destinationNode +
                ", actions=" + actions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transition that = (Transition) o;

        if (actions != null ? !actions.equals(that.actions) : that.actions != null) return false;
        if (destinationNode != null ? !destinationNode.equals(that.destinationNode) : that.destinationNode != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (destinationNode != null ? destinationNode.hashCode() : 0);
        result = 31 * result + (actions != null ? actions.hashCode() : 0);
        return result;
    }
}
