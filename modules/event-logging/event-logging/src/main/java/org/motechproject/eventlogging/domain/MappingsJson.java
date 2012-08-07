package org.motechproject.eventlogging.domain;

import java.util.List;
import java.util.Map;

public class MappingsJson {

    private List<String> subjects;
    private List<Map<String, String>> mappings;
    private List<String> excludes;
    private List<String> includes;
    private List<ParametersPresentEventFlag> flags;

    public List<ParametersPresentEventFlag> getFlags() {
        return flags;
    }

    public void setFlags(List<ParametersPresentEventFlag> flags) {
        this.flags = flags;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public List<Map<String, String>> getMappings() {
        return mappings;
    }

    public void setMappings(List<Map<String, String>> mappings) {
        this.mappings = mappings;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

}
