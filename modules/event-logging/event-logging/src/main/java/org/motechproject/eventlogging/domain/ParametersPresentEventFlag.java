package org.motechproject.eventlogging.domain;

import org.motechproject.event.MotechEvent;

import java.util.Collections;
import java.util.Map;

public class ParametersPresentEventFlag implements EventFlag {

    private Map<String, String> keyValuePairsPresent;

    public ParametersPresentEventFlag() {
    }

    public ParametersPresentEventFlag(Map<String, String> keyValuePairsPresent) {
        if (keyValuePairsPresent == null) {
            this.keyValuePairsPresent = Collections.<String, String> emptyMap();
        } else {
            this.keyValuePairsPresent = keyValuePairsPresent;
        }
    }

    @Override
    public boolean passesFlags(MotechEvent eventToLog) {
        Map<String, Object> eventParameters = eventToLog.getParameters();

        for (Map.Entry<String, String> flag : keyValuePairsPresent.entrySet()) {
            if (eventParameters.containsKey(flag.getKey())
                    && !(eventParameters.get(flag.getKey()).equals(flag.getValue().toString()))) {
                return false;
            }
            if (!eventParameters.containsKey(flag.getKey())) {
                return false;
            }
        }
        return true;
    }

    public Map<String, String> getKeyValuePairsPresent() {
        return keyValuePairsPresent;
    }

    public void setKeyValuePairsPresent(Map<String, String> keyValuePairsPresent) {
        this.keyValuePairsPresent = keyValuePairsPresent;
    }

}
