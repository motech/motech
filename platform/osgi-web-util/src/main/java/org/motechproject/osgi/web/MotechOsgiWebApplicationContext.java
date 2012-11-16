package org.motechproject.osgi.web;

import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class MotechOsgiWebApplicationContext extends OsgiBundleXmlApplicationContext implements ConfigurableWebApplicationContext {
    private ServletContext servletContext;
    private ServletConfig servletConfig;
    private String namespace;

    public MotechOsgiWebApplicationContext() {
        super();
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    @Override
    public ServletConfig getServletConfig() {
        return this.servletConfig;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setConfigLocation(String configLocation) {
        this.setConfigLocations(new String[]{configLocation});
    }
}
