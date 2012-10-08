package org.motechproject.server.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleRegistrationData {

    private String moduleName;
    private String url;
    private String header;

    private List<String> angularModules = new ArrayList<>();
    private Map<String, String> subMenu = new HashMap<>();
    private Map<String, String> i18n = new HashMap<>();

    public ModuleRegistrationData() {
        this(null, null);
    }

    public ModuleRegistrationData(String moduleName, String url) {
        this.moduleName = moduleName;
        this.url = url;
    }

    public void addAngularModule(String moduleName) {
        angularModules.add(moduleName);
    }

    public void removeAngularModule(String moduleName) {
        angularModules.remove(moduleName);
    }

    public void addSubMenu(String url, String label) {
        subMenu.put(label, url);
    }

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

    public Map<String, String> getSubMenu() {
        return subMenu;
    }

    public Map<String, String> getI18n() {
        return i18n;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

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
}
