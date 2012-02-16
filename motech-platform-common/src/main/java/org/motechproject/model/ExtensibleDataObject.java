package org.motechproject.model;

import java.util.HashMap;
import java.util.Map;

public abstract class ExtensibleDataObject extends MotechBaseDataObject {
    private Map<String, Object> data = new HashMap<String, Object>();

    public Map<String, Object> getData() {
        return data;
    }

    public void addData(String key, Object value) {
        data.put(key, value);
    }
}
