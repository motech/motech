package org.motechproject.tasks.dto;

import java.util.ArrayList;
import java.util.List;

public class ChannelDto {

    private List<ActionEventDto> actionTaskEvents = new ArrayList<>();
    private String description;
    private String displayName;
    private String moduleName;
    private String moduleVersion;
    private boolean providesTriggers;

    public ChannelDto() {
    }

    public ChannelDto(List<ActionEventDto> actionTaskEvents, String description, String displayName, String moduleName,
                      String moduleVersion, boolean providesTriggers) {
        this.actionTaskEvents = actionTaskEvents;
        this.description = description;
        this.displayName = displayName;
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        this.providesTriggers = providesTriggers;
    }

    public List<ActionEventDto> getActionTaskEvents() {
        return actionTaskEvents;
    }

    public void setActionTaskEvents(List<ActionEventDto> actionTaskEvents) {
        this.actionTaskEvents = actionTaskEvents;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public void setModuleVersion(String moduleVersion) {
        this.moduleVersion = moduleVersion;
    }

    public boolean isProvidesTriggers() {
        return providesTriggers;
    }

    public void setProvidesTriggers(boolean providesTriggers) {
        this.providesTriggers = providesTriggers;
    }
}
