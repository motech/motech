package org.motechproject.decisiontree;

import java.io.Serializable;

public interface FlowSession {
    public String getSessionId();
    public <T extends Serializable> void set(String key, T value);
    public <T extends Serializable> T get(String key);
}
