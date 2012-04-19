package org.motechproject.scheduletracking.api.domain.json;

import java.util.ArrayList;
import java.util.List;

public class AlertRecord {
    private String window;
    private List<String> offset = new ArrayList<String>();
    private List<String> interval = new ArrayList<String>();
    private String count;
    private boolean floating;

    public List<String> offset() {
        return offset;
    }

    public List<String> interval() {
        return interval;
    }

    public String window() {
        return window;
    }

    public String count() {
        return count;
    }

    public boolean isFloating() {
        return floating;
    }
}
