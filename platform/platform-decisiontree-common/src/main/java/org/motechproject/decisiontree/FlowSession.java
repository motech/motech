package org.motechproject.decisiontree;

import java.io.Serializable;

public interface FlowSession {
    String getSessionId();
    String getLanguage();
    void setLanguage(String language);
    <T extends Serializable> void set(String key, T value);
    <T extends Serializable> T get(String key);
}
