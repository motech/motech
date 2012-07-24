package org.motechproject.decisiontree.model;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.motechproject.decisiontree.FlowSession;

/**
 * Represents transition from one node to the other.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public interface ITransition {
    Node getDestinationNode(String input, FlowSession session);
}
