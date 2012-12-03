package org.motechproject.osgi.web;

import org.osgi.framework.BundleContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;

public class OsgiDispatcherServlet extends DispatcherServlet {
    private BundleContext bundleContext;

    public OsgiDispatcherServlet(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
        if (wac instanceof MotechOsgiWebApplicationContext) {
            MotechOsgiWebApplicationContext wc = (MotechOsgiWebApplicationContext) wac;
            wc.setBundleContext(bundleContext);
        }
        super.postProcessWebApplicationContext(wac);
    }

    @Override
    protected void initFrameworkServlet() throws ServletException {
        getServletContext().log("Initialized servlet for " + bundleContext.getBundle().toString());
    }
}
