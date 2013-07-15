package org.motechproject.osgi.web;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.osgi.framework.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleRegistrationData {

    private String moduleName;
    private String url;
    private String header;
    private boolean needsAttention;
    private String roleForAccess;
    private String criticalMessage;
    private String settingsURL;
    private Bundle bundle;

    private List<String> angularModules = new ArrayList<>();
    private Map<String, SubmenuInfo> subMenu = new HashMap<>();
    private Map<String, String> i18n = new HashMap<>();

    public ModuleRegistrationData() {
        this(null, (String)null);
    }

    public ModuleRegistrationData(String moduleName, String url, List<String> angularModules, Map<String, String> i18n, Header header) {
        this.moduleName = moduleName;
        this.url = url;
        this.angularModules = angularModules;
        this.i18n = i18n;
        this.header = header.asString();
    }

    public ModuleRegistrationData(String moduleName, String url) {
        this.moduleName = moduleName;
        this.url = url;
    }

    public ModuleRegistrationData(String moduleName, Map<String, String> i18n) {
        this.moduleName = moduleName;
        this.i18n = i18n;
    }

    @JsonIgnore
    public Bundle getBundle() {
        return bundle;
    }

    @JsonIgnore
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    @JsonIgnore
    public void addAngularModule(String moduleName) {
        angularModules.add(moduleName);
    }

    @JsonIgnore
    public void removeAngularModule(String moduleName) {
        angularModules.remove(moduleName);
    }

    @JsonIgnore
    public void addSubMenu(String url, String label) {
        subMenu.put(label, new SubmenuInfo(url));
    }

    @JsonIgnore
    public void addI18N(String fileName, String fileLocation) {
        i18n.put(fileName, fileLocation);
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getAngularModules() {
        return angularModules;
    }

    public Map<String, SubmenuInfo> getSubMenu() {
        return subMenu;
    }

    public void setSubMenu(Map<String, SubmenuInfo> subMenu) {
        this.subMenu = subMenu;
    }

    @JsonIgnore
    public Map<String, String> getI18n() {
        return i18n;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public boolean isNeedsAttention() {
        return needsAttention;
    }

    public void setNeedsAttention(boolean needsAttention) {
        this.needsAttention = needsAttention;
    }

    public String getCriticalMessage() {
        return criticalMessage;
    }

    public void setCriticalMessage(String criticalMessage) {
        this.criticalMessage = criticalMessage;
    }

    @JsonIgnore
    public String getAngularModulesStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < angularModules.size(); i++) {
            sb.append('\'').append(angularModules.get(i)).append('\'');
            if (i < angularModules.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("]");

        return sb.toString();
    }

    @JsonIgnore
    public void subMenuNeedsAttention(String submenu) {
        SubmenuInfo submenuInfo = subMenu.get(submenu);
        if (submenuInfo != null) {
            submenuInfo.setNeedsAttention(true);
        }
    }

    @JsonIgnore
    public void submenuBackToNormal(String submenu) {
        SubmenuInfo submenuInfo = subMenu.get(submenu);
        if (submenuInfo != null) {
            submenuInfo.setNeedsAttention(false);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ModuleRegistrationData that = (ModuleRegistrationData) o;

        if (header != null ? !header.equals(that.header) : that.header != null) {
            return false;
        }

        if (moduleName != null ? !moduleName.equals(that.moduleName) : that.moduleName != null) {
            return false;
        }

        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = moduleName != null ? moduleName.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (header != null ? header.hashCode() : 0);
        result = 31 * result + (angularModules != null ? angularModules.hashCode() : 0);
        return result;
    }

    @JsonIgnore
    public String getRoleForAccess() {
        return roleForAccess;
    }

    @JsonIgnore
    public void setRoleForAccess(String role) {
        this.roleForAccess = role;
    }

    @JsonIgnore
    public String getSettingsURL() {
        return settingsURL;
    }

    @JsonIgnore
    public void setSettingsURL(String settingsURL) {
        this.settingsURL = settingsURL;
    }
}
