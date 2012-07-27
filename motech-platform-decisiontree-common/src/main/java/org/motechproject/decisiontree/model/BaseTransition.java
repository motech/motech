package org.motechproject.decisiontree.model;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseTransition implements ITransition
{
    @JsonProperty
    private List<INodeOperation> operations = new ArrayList<>();

    /**
     * Gets the tree operations for this node
     * @return tree operations of the node
     * @see INodeOperation
     */
    @JsonIgnore
    public List<INodeOperation> getOperations() {
        return this.operations;
    }

    public BaseTransition addOperations(INodeOperation ... operations) {
        Collections.addAll(this.operations, operations);
        return this;
    }
}
