package org.motechproject.commons.api.model;

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
        for (Map.Entry<String,Object> entry: data.entrySet()) {
            this.addData(entry.getKey(), entry.getValue());
        }
        return (T) this;
    }
}
