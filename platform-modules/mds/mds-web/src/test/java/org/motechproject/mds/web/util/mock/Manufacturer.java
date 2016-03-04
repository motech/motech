package org.motechproject.mds.web.util.mock;

import java.util.List;

public class Manufacturer {

    private String name;
    private List<Factory> factories;
    private SafetyPolicy safetyPolicy;

    public Manufacturer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SafetyPolicy getSafetyPolicy() {
        return safetyPolicy;
    }

    public void setSafetyPolicy(SafetyPolicy safetyPolicy) {
        this.safetyPolicy = safetyPolicy;
    }

    public List<Factory> getFactories() {
        return factories;
    }

    public void setFactories(List<Factory> factories) {
        this.factories = factories;
    }
}
