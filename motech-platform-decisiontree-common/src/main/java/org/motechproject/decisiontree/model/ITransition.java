package org.motechproject.decisiontree.model;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * Represents transition from one node to the other.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public interface ITransition {
    public Node getDestinationNode(String input);
}
