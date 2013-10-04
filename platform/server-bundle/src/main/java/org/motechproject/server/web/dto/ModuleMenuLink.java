package org.motechproject.server.web.dto;

import java.io.Serializable;

/**
 * A class representing a link displayed in the left hand nav menu.
 */
public class ModuleMenuLink implements Serializable {

    private static final long serialVersionUID = -1240307996359165542L;

    private String name;
    private String moduleName;
    private String url;
    private boolean needsAttention;

    public ModuleMenuLink(String name, String moduleName, String url, boolean needsAttention) {
        this.name = name;
        this.moduleName = moduleName;
        this.url = url;
        this.needsAttention = needsAttention;
    }

    /**
     * Text that is displayed as this link.
     * @return link text content.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Name of the module to which this link belongs to. Required for building the url.
     * @return the module to which this link belongs to.
     */
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Url that is appended at the end of this links. In most cases an anchor to the partial loaded
     * by AngularJS.
     * @return url to be appended.
     */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Specifies whether this link should be marked on the UI with a warning sign.
     * @return true if this module needs attention, false otherwise.
     */
    public boolean isNeedsAttention() {
        return needsAttention;
    }

    public void setNeedsAttention(boolean needsAttention) {
        this.needsAttention = needsAttention;
    }
}
