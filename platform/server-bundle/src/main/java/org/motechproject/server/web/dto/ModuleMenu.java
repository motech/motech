package org.motechproject.server.web.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the left hand side menu. Contains a list of sections to display.
 */
public class ModuleMenu implements Serializable {

    private static final long serialVersionUID = -388532207391274070L;

    private List<ModuleMenuSection> sections = new ArrayList<>();

    public List<ModuleMenuSection> getSections() {
        return sections;
    }

    public void setSections(List<ModuleMenuSection> sections) {
        this.sections = sections;
    }

    public void addMenuSection(ModuleMenuSection section) {
        sections.add(section);
    }
}
