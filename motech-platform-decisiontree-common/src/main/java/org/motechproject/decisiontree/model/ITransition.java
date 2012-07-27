package org.motechproject.decisiontree.model;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.motechproject.decisiontree.FlowSession;

import java.util.List;

/**
 * Represents transition from one node to the other.
 * Deprecated - Please use the abstract class BaseTransition instead
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
//TODO:Remove this interface
public interface ITransition {
    public Node getDestinationNode(String input, FlowSession session);
    public List<INodeOperation> getOperations();
    public ITransition addOperations(INodeOperation ... operations);


}
