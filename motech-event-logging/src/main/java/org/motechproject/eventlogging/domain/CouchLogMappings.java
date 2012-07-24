package org.motechproject.eventlogging.domain;

import java.util.ArrayList;
import java.util.List;

public class CouchLogMappings {

    private List<KeyValue> mappings;
    private List<String> exclusions;
    private List<String> inclusions;

    public CouchLogMappings(List<KeyValue> mappings, List<String> exclusions,
            List<String> inclusions) {
        if (mappings == null) {
            this.mappings = new ArrayList<KeyValue>();
        } else {
            this.mappings = mappings;
        }
        if (exclusions == null) {
            this.exclusions = new ArrayList<String>();
        } else {
            this.exclusions = exclusions;
        }
        if (inclusions == null) {
            this.inclusions = new ArrayList<String>();
        } else {
            this.inclusions = inclusions;
        }
    }

    public List<String> getInclusions() {
        return inclusions;
    }

    public void setInclusions(List<String> inclusions) {
        this.inclusions = inclusions;
    }

    public List<KeyValue> getMappings() {
        return mappings;
    }

    public void setMappings(List<KeyValue> mappings) {
        this.mappings = mappings;
    }

    public List<String> getExclusions() {
        return exclusions;
    }

    public void setExclusions(List<String> exclusions) {
        this.exclusions = exclusions;
    }
}
