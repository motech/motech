package org.motechproject.tasks.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Manipulation {
    private String originalKey;
    private String eventKey;
    private List<String> manipulation;

    public Manipulation(String originalKey) {
        this.originalKey = originalKey;
        this.eventKey = getKeyName(originalKey);
        this.manipulation = getManipulation(originalKey);
    }

    public String getOriginalKey() {
        return originalKey;
    }

    public String getEventKey() {
        return eventKey;
    }

    public List<String> getManipulation() {
        return manipulation;
    }

    private String getKeyName(String key) {
        List<String> splitKey = Arrays.asList(key.split("\\?"));
        return splitKey.get(0);
    }

    private List<String> getManipulation(String key) {
        List<String> man = new ArrayList<>(Arrays.asList(key.split("\\?")));
        man.remove(0);
        return man;
    }
}
