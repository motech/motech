package org.motechproject.osgi.web;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.osgi.web.service.UIFrameworkService;
import org.osgi.framework.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Object used to registered a module withing the Motech UI system. Represents a module and is used
 * for building the common user interface. All modules that wish to register within the UI system must
 * either expose this class as a spring bean in their application context or manually register it through the
 * {@link UIFrameworkService} OSGi service.
 *
 * @see UIFrameworkService
 */
public class ModuleRegistrationData {

    private static final String DEFAULT_DOCS_URL = "http://grameenfoundation.org/";
    private static final String DOC_URL = "Bundle-DocURL";

    private String moduleName;
    private String url;
    private boolean needsAttention;
    private String criticalMessage;
    private String defaultURL;
    private String settingsURL;
    private Bundle bundle;
    private String resourcePath;
    private String restDocsPath;

    private List<String> roleForAccess = new ArrayList<>();
    private List<String> angularModules = new ArrayList<>();
    private Map<String, SubmenuInfo> subMenu = new TreeMap<>();
    private Map<String, String> i18n = new HashMap<>();
    private Map<String, List<String>> tabAccessMap = new LinkedHashMap<>();

    public ModuleRegistrationData() {
        this(null, null, null, null);
    }

    /**
     * @param moduleName the name of the module
     * @param url the url under which this module can be accessed
     */
    public ModuleRegistrationData(String moduleName, String url) {
        this(moduleName, url, null, null);
    }

    /**
     * Constructor for modules that just want to register i18n files (i.e. for tasks).
     * @param moduleName the name of the module
     * @param i18n a map, where the keys are the names of the i18n files and values are their locations (HTTP locations)
     */
    public ModuleRegistrationData(String moduleName, Map<String, String> i18n) {
        this(moduleName, null, null, i18n);
    }

    /**
     * Constructor for modules that want to register their own panels in the UI.
     * @param moduleName the name of the module
     * @param url the url under which this module can be accessed
     * @param angularModules the list of angular modules that should be loaded on the UI for this module
     * @param i18n a map, where the keys are the names of the i18n files and values are their locations (HTTP locations)
     */
    public ModuleRegistrationData(String moduleName, String url, List<String> angularModules, Map<String, String> i18n) {
        this.moduleName = moduleName;
        this.url = url;

        if (null != angularModules) {
            this.angularModules.addAll(angularModules);
        }

        if (null != i18n) {
            this.i18n.putAll(i18n);
        }
    }

    /**
     * @return the underlying OSGi bundle for this module
     */
    @JsonIgnore
    public Bundle getBundle() {
        return bundle;
    }

    /**
     * @param bundle the underlying OSGi bundle for this module
     */
    @JsonIgnore
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    /**
     * Adds an AngularJS module that should be loaded with this module.
     * @param moduleName the name of the angular module that should be loaded for this module on the UI
     */
    @JsonIgnore
    public void addAngularModule(String moduleName) {
        angularModules.add(moduleName);
    }

    /**
     * Removes an AngularJS module from the list of modules should be loaded with this module.
     * @param moduleName the name of the angular module that should no longer be loaded for this module on the UI
     */
    @JsonIgnore
    public void removeAngularModule(String moduleName) {
        angularModules.remove(moduleName);
    }

    /**
     * Adds a submenu to this module. Submenu is a link on the left side of the UI.
     * @param url the url to which the link will redirect to
     * @param label the label that will be displayed on the UI
     */
    @JsonIgnore
    public void addSubMenu(String url, String label) {
        subMenu.put(label, new SubmenuInfo(url));
    }

    /**
     * Adds a submenu to this module. Submenu is a link on the left side of the UI.
     * @param url the url to which the link will redirect to
     * @param label the label that will be displayed on the UI
     * @param roleForAccess the permission required to view this sub menu (will be hidden if the user doesn't have the permission)
     */
    @JsonIgnore
    public void addSubMenu(String url, String label, String roleForAccess) {
        SubmenuInfo submenuInfo = new SubmenuInfo(url);
        submenuInfo.setRoleForAccess(roleForAccess);
        subMenu.put(label, submenuInfo);
    }

    /**
     * Adds i18n messages file entry for this module. Messages from this file will be loaded on the UI.
     * @param fileName the name of the file
     * @param fileLocation the location of the file (HTTP location, ../mymodule/messages for example)
     */
    @JsonIgnore
    public void addI18N(String fileName, String fileLocation) {
        i18n.put(fileName, fileLocation);
    }

    /**
     * @return the name of the module represented by this object
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * @param moduleName the name of the module represented by this object
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * @return the url for accessing this module
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url for accessing this module
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the list of angular modules that should be loaded on the UI for this module
     */
    public List<String> getAngularModules() {
        return angularModules;
    }

    /**
     * Returns the list of sub-menus for this module.
     * @return a map where the keys are the names of the sub-menus and values are their representations
     */
    public Map<String, SubmenuInfo> getSubMenu() {
        return subMenu;
    }

    /**
     * Sets the list of sub-menus for this module.
     * @param subMenu a map where the keys are the names of the sub-menus and values are their representations
     */
    public void setSubMenu(Map<String, SubmenuInfo> subMenu) {
        this.subMenu = subMenu;
    }

    /**
     * Returns the list of i18n message files for this module.
     * @return a map, where the keys are the names of the i18n files and values are their locations (HTTP locations)
     */
    @JsonIgnore
    public Map<String, String> getI18n() {
        return i18n;
    }

    /**
     * Returns the map of available tabs for specified permissions for this module.
     * @return a map, where the keys are tabs names and values are lists of permissions for which the tab is available
     */
    @JsonIgnore
    public Map<String, List<String>> getTabAccessMap() { return tabAccessMap; }

    /**
     * Sets the map available tabs for specified permissions for this module.
     * @param tabAccessMap a map, where the keys are tabs names and values are lists of permissions for which the tab is available
     */
    @JsonIgnore
    public void setTabAccessMap(Map<String, List<String>> tabAccessMap) { this.tabAccessMap = tabAccessMap; }

    /**
     * Checks whether this module needs attention - meaning it requires a UI notification pointing to it.
     * @return true if the module needs attention, false otherwise
     */
    public boolean isNeedsAttention() {
        return needsAttention;
    }

    /**
     * Sets whether this module needs attention - meaning it requires a UI notification pointing to it.
     * @param needsAttention true if the module needs attention, false otherwise
     */
    public void setNeedsAttention(boolean needsAttention) {
        this.needsAttention = needsAttention;
    }

    /**
     * Returns a critical message for this module. That message should be displayed on the UI if the module needs attention.
     * @return the critical message that should be communicated to the user
     */
    public String getCriticalMessage() {
        return criticalMessage;
    }
    /**
     * Sets a critical message for this module. That message should be displayed on the UI if the module needs attention.
     * @param criticalMessage  the critical message that should be communicated to the user
     */
    public void setCriticalMessage(String criticalMessage) {
        this.criticalMessage = criticalMessage;
    }

    /**
     * Returns the list of angular modules that should be loaded for this module in a format
     * that can be used in Javascript.
     * @return the list of Angular modules in javascript format
     */
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

    /**
     * Marks a sub-menu of this module as needing attention - meaning it requires a UI notification pointing to it.
     * @param submenu the sub-menu to mark as requiring attention
     */
    @JsonIgnore
    public void subMenuNeedsAttention(String submenu) {
        SubmenuInfo submenuInfo = subMenu.get(submenu);
        if (submenuInfo != null) {
            submenuInfo.setNeedsAttention(true);
        }
    }

    /**
     * Marks a sub-menu of this module as not needing attention anymore - meaning no special UI notification should point to it.
     * @param submenu the sub-menu to mark as requiring attention
     */
    @JsonIgnore
    public void submenuBackToNormal(String submenu) {
        SubmenuInfo submenuInfo = subMenu.get(submenu);
        if (submenuInfo != null) {
            submenuInfo.setNeedsAttention(false);
        }
    }

    /**
     * Returns the permission names required to access this module - used to hide its menus on the UI.
     * This module should be exposed only if the user has at least one of these permissions.
     * @return the permissions required for accessing the module
     */
    @JsonIgnore
    public List<String> getRoleForAccess() {
        return roleForAccess;
    }

    /**
     * Sets a permission name required to access this module - used to hide its menus on the UI.
     * This module should be exposed only if the user has this permissions.
     * @param role  the permission required for accessing the module
     */
    @JsonIgnore
    public void setRoleForAccess(String role) {
        this.roleForAccess.add(role);
    }

    /**
     * Sets the permission names required to access this module - used to hide its menus on the UI.
     * This module should be exposed only if the user has at least one of these permissions.
     * @param roles  the permissions required for accessing the module
     */
    @JsonIgnore
    public void setRoleForAccess(List<String> roles) {
        this.roleForAccess = roles;
    }

    /**
     * Returns the settings url for this module. This is used for linking to the custom settings page
     * for a module in the admin UI instead of generating a UI for the settings.
     * @return the path to the custom setting page for this module
     */
    @JsonIgnore
    public String getSettingsURL() {
        return settingsURL;
    }

    /**
     * Sets the settings url for this module. This is used for linking to the custom settings page
     * for a module in the admin UI instead of generating a UI for the settings.
     * @param settingsURL the path to the custom setting page for this module
     */
    @JsonIgnore
    public void setSettingsURL(String settingsURL) {
        this.settingsURL = settingsURL;
    }

    /**
      * @return the default url for this module. Usually points to one of its sub-views.
     */
    @JsonIgnore
    public String getDefaultURL() {
        return defaultURL;
    }

    /**
     * @param defaultURL  the default url for this module. Usually points to one of its sub-views.
     */
    @JsonIgnore
    public void setDefaultURL(String defaultURL) {
        this.defaultURL = defaultURL;
    }

    /**
     * @return the path of the module's static resources
     */
    @JsonIgnore
    public String getResourcePath() {
        return resourcePath;
    }

    /**
     * @param resourcePath the path of the module's static resources
     */
    @JsonIgnore
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    /**
     * Returns the path to the REST API specification for this module. This file will be used
     * for generating a Swagger UI for the API.
     * @return the path to REST API specification for this module
     */
    @JsonIgnore
    public String getRestDocsPath() {
        return restDocsPath;
    }

    /**
     * Sets the path to the REST API specification for this module. This file will be used
     * for generating a Swagger UI for the API.
     * @param restDocsPath the path to REST API specification for this module
     */
    @JsonIgnore
    public void setRestDocsPath(String restDocsPath) {
        this.restDocsPath = restDocsPath;
    }

    /**
     * Returns the documentation url for this module. A small link at the bottom of the screen will point
     * to this specific documentation, the admin module will also link to it. The link can be external.
     * @return the url of the documentation
     */
    @JsonIgnore
    public String getDocumentationUrl() {
        String documentationUrl = getBundle().getHeaders().get(DOC_URL);
        return DEFAULT_DOCS_URL.equals(documentationUrl) ? null : documentationUrl;
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

        return Objects.equals(moduleName, that.moduleName) && Objects.equals(url, that.url) &&
                Objects.equals(angularModules, that.angularModules);
    }

    @Override
    public int hashCode() {
        int result = moduleName != null ? moduleName.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (angularModules != null ? angularModules.hashCode() : 0);
        return result;
    }
}
