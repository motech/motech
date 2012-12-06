package org.motechproject.sms.http.osgi;

class ServletDefinition {
    private String contextConfigLocation;
    private String servletUrlMapping;
    private Class<Activator.SmsHttpApplicationContext> smsHttpApplicationContextClass;
    private String resourceUrlMapping;

    ServletDefinition(String contextConfigLocation, String servletUrlMapping, Class smsHttpApplicationContextClass, String resourceUrlMapping) {
        this.contextConfigLocation = contextConfigLocation;
        this.servletUrlMapping = servletUrlMapping;
        this.smsHttpApplicationContextClass = smsHttpApplicationContextClass;
        this.resourceUrlMapping = resourceUrlMapping;
    }

    public String getContextConfigLocation() {
        return contextConfigLocation;
    }

    public String getServletUrlMapping() {
        return servletUrlMapping;
    }

    public Class getApplicationContextClass() {
        return smsHttpApplicationContextClass;
    }

    public String getResourceUrlMapping() {
        return resourceUrlMapping;
    }
}
