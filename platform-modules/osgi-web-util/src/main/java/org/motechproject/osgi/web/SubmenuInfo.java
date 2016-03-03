package org.motechproject.osgi.web;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to encapsulate information about sub-menu links to be shown on UI.
 * This represents a link shown on the left side of the UI.
 */
public class SubmenuInfo {

    private String url;
    private boolean needsAttention;
    private String criticalMessage;

    private List<String> roleForAccess = new ArrayList<>();

    public SubmenuInfo() {
    }

    /**
     * Constructs an instance for a given url.
     * @param url the url this links to
     */
    public SubmenuInfo(String url) {
        this.url = url;
    }

    /**
     * @return true if this sub-menu should be marked as requiring attention on the UI, false otherwise
     */
    public boolean isNeedsAttention() {
        return needsAttention;
    }

    /**
     * @param needsAttention true if this sub-menu should be marked as requiring attention on the UI, false otherwise
     */
    public void setNeedsAttention(boolean needsAttention) {
        this.needsAttention = needsAttention;
    }

    /**
     * @return the url this links to
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url this links to
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns the critical message for this sub-menu, if it has been set.
     * @return the critical message for this link
     */
    public String getCriticalMessage() {
        return criticalMessage;
    }

    /**
     * Sets the critical message for this sub-menu, it will displayed on the UI as a tooltip.
     * @param criticalMessage the critical message for this link
     */
    public void setCriticalMessage(String criticalMessage) {
        this.criticalMessage = criticalMessage;
    }

    /**
     * Returns a list of permissions required to access this sub-menu item. It is required for the user to have at least permission from this, not all.
     * This link will be hidden for users without the permissions.
     * @return the list of permissions for access
     */
    public List<String> getRoleForAccess() {
        return roleForAccess;
    }

    public void setRoleForAccess(String roleForAccess) {
        this.roleForAccess.add(roleForAccess);
    }

    /**
     * Sets the list of permissions required to access this sub-menu item. It is required for the user to have at least permission from this, not all.
     * This link will be hidden for users without the permissions.
     * @param roleForAccess the list of permissions for access
     */
    public void setRoleForAccess(List<String> roleForAccess) {
        this.roleForAccess = roleForAccess;
    }
}
