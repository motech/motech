package org.motechproject.server.web.dto;

import java.io.Serializable;

public class ModuleConfig implements Serializable {
    private static final long serialVersionUID = 848322990033591043L;

    private String name;
    private String script;
    private String template;
    private String css;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }
}
