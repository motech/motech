package org.motechproject.decisiontree.model;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

/**
 *
 */
public class Tree extends MotechAuditableDataObject {

    private static final long serialVersionUID = 1L;

    @TypeDiscriminator
    private String name;
    private String description;
    private Node rootNode;
    
    public static class Builder {
    	private Tree obj;
		public Builder() {
			obj = new Tree();
		}
	    public Builder setName(String name) {
	        obj.name = name;
	        return this;
	    }
	    public Builder setDescription(String description) {
	        obj.description = description;
	        return this;
	    }
	    public Builder setRootNode(Node rootNode) {
	        obj.rootNode = rootNode;
	        return this;
	    }
    	public Tree build() {
    		return obj;
    	}
    }

    public static Builder newBuilder() {
    	return new Builder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
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
}
