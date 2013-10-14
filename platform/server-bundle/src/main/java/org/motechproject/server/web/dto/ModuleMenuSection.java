package org.motechproject.server.web.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a section in the left hand nav menu. Apart from its name, it also contains a list
 * links to be displayed in this section. The {@link #isNeedsAttention()} flag specifies whether this
 * section should be given a warning icon on the UI.
 */
public class ModuleMenuSection implements Serializable {

    private static final long serialVersionUID = 7942707161676048960L;

    private String name;
    private List<ModuleMenuLink> links = new ArrayList<>();
    private boolean needsAttention;

    public ModuleMenuSection(String name, boolean needsAttention) {
        this.needsAttention = needsAttention;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ModuleMenuLink> getLinks() {
        return links;
    }

    public void setLinks(List<ModuleMenuLink> links) {
        this.links = links;
    }

    public boolean isNeedsAttention() {
        return needsAttention;
    }

    public void setNeedsAttention(boolean needsAttention) {
        this.needsAttention = needsAttention;
    }

    public void addLink(ModuleMenuLink link) {
        links.add(link);
    }
}
