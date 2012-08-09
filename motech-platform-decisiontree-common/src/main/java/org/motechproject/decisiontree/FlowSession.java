package org.motechproject.decisiontree;

import org.motechproject.decisiontree.model.Node;

import java.io.Serializable;

public interface FlowSession {
    String getSessionId();
    String getLanguage();
    void setLanguage(String language);
    <T extends Serializable> void set(String key, T value);
    <T extends Serializable> T get(String key);

    void setCurrentNode(Node node);

    Node getCurrentNode();
}
