package org.motechproject.decisiontree.core.model;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.motechproject.decisiontree.core.FlowSession;

@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.CLASS, property = "@type")
public interface INodeOperation {
    void perform(String userInput, FlowSession session);
}
