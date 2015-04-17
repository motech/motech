package org.motechproject.mds.web.domain;

import java.util.List;

/**
 * Transports MDS tabs.
 */
public class TabsDto {

    private List<String> tabs;

    public TabsDto(List<String> tabs) {
        this.tabs = tabs;
    }

    public List<String> getTabs() {
        return tabs;
    }

    public void setTabs(List<String> tabs) {
        this.tabs = tabs;
    }
}
