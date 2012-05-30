package org.motechproject.model;

import java.util.HashMap;
import java.util.Map;

public abstract class ExtensibleDataObject<T> {

    private Map<String, Object> data = new HashMap<String, Object>();

    public Map<String, Object> getData() {
        return data;
    }

    public T addData(String key, Object value) {
        this.data.put(key, value);
        return (T) this;
    }

    public T addData(Map<String, Object> data) {
        for (String key : data.keySet()) {
            this.addData(key, data.get(key));
        }
        return (T) this;
    }
}
