package org.motechproject.osgi.web;

/****
 * Class to encapsulate information about submenu Links to be shown on  UI
 */
public class SubmenuInfo {

    private String url;
    private boolean needsAttention;
    private String criticalMessage;
    private String roleForAccess;

    public boolean isNeedsAttention() {
        return needsAttention;
    }

    public void setNeedsAttention(boolean needsAttention) {
        this.needsAttention = needsAttention;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SubmenuInfo(String url) {
        this.url = url;
    }

    public String getCriticalMessage() {
        return criticalMessage;
    }

    public void setCriticalMessage(String criticalMessage) {
        this.criticalMessage = criticalMessage;
    }

    public String getRoleForAccess() {
        return roleForAccess;
    }

    public void setRoleForAccess(String roleForAccess) {
        this.roleForAccess = roleForAccess;
    }
}
