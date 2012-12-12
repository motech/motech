package org.motechproject.sms.http.osgi;

class ServletDefinition {
    private String contextConfigLocation;
    private String servletUrlMapping;
    private Class<Activator.SmsHttpApplicationContext> applicationContextClass;
    private String resourceUrlMapping;

    ServletDefinition(String contextConfigLocation, String servletUrlMapping, Class applicationContextClass, String resourceUrlMapping) {
        this.contextConfigLocation = contextConfigLocation;
        this.servletUrlMapping = servletUrlMapping;
        this.applicationContextClass = applicationContextClass;
        this.resourceUrlMapping = resourceUrlMapping;
    }

    public String getContextConfigLocation() {
        return contextConfigLocation;
    }

    public String getServletUrlMapping() {
        return servletUrlMapping;
    }

    public Class getApplicationContextClass() {
        return applicationContextClass;
    }

    public String getResourceUrlMapping() {
        return resourceUrlMapping;
    }
}
