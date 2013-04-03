package org.motechproject.callflow.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Extra data stored as key value pair along with call event.
 */
public class CallEventCustomData implements Serializable{
    private static final long serialVersionUID = 5879864233460860947L;

    @JsonProperty
    private Map<String, String> data = new HashMap<>();

    public Serializable get(String key) {
        return data.get(key);
    }

    public void put(String key, String value) {
        data.put(key, value);
    }
}
