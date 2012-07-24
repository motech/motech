package org.motechproject.decisiontree;

import java.io.Serializable;

public interface FlowSession {
    String getSessionId();
    <T extends Serializable> void set(String key, T value);
    <T extends Serializable> T get(String key);
}
