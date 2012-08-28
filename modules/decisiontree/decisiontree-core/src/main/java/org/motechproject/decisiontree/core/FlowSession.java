package org.motechproject.decisiontree.core;

import org.motechproject.decisiontree.core.model.Node;

import java.io.Serializable;

public interface FlowSession {
    String getSessionId();
    String getLanguage();
    void setLanguage(String language);
    String getPhoneNumber();
    <T extends Serializable> T get(String key);
    <T extends Serializable> void set(String key, T value);
    Node getCurrentNode();
    void setCurrentNode(Node node);
}
