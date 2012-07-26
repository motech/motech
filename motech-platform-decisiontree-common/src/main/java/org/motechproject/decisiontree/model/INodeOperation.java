package org.motechproject.decisiontree.model;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.motechproject.decisiontree.FlowSession;

@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.CLASS, property = "@type")
public interface INodeOperation {
    public void perform(String userInput, FlowSession session);
}
