package org.motechproject.commcare.domain;

import java.util.List;
import java.util.Map;

public class CommcareFixturesJson {
    private Map<String, String> meta;
    private List<CommcareFixture> objects;

    public List<CommcareFixture> getObjects() {
        return this.objects;
    }

    public Map<String, String> getMeta() {
        return this.meta;
    }
}
