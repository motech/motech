package org.motechproject.commcare.domain;

import java.util.List;
import java.util.Map;

public class CommcareUsersJson {
    private Map<String, String> meta;
    private List<CommcareUser> objects;

    public List<CommcareUser> getObjects() {
        return this.objects;
    }

    public Map<String, String> getMeta() {
        return this.meta;
    }
}
