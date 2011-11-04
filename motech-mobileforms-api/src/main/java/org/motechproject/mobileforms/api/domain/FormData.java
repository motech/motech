package org.motechproject.mobileforms.api.domain;

import java.util.Map;

public class FormData {
    private Map<String, String> map;

    public FormData(Map<String, String> map) {
        this.map = map;
    }

    public Map data() {
        return map;
    }
}
