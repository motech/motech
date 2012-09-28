package org.motechproject.server.ui;

import java.util.ArrayList;
import java.util.List;

public class ModuleRegistrationData {

    private String moduleName;
    private String url;
    private String header;

    private List<String> angularModules = new ArrayList<>();

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
}
