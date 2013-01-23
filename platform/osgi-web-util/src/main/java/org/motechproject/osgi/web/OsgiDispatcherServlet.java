package org.motechproject.osgi.web;

import org.osgi.framework.BundleContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;

public class OsgiDispatcherServlet extends DispatcherServlet {


    private BundleContext bundleContext;
    private ApplicationContext applicationContext;

    public OsgiDispatcherServlet(BundleContext bundleContext) {
        this(bundleContext, null);
    }


    public OsgiDispatcherServlet(BundleContext bundleContext, ApplicationContext applicationContext) {
        this.bundleContext = bundleContext;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
        if (wac instanceof MotechOsgiWebApplicationContext) {
            MotechOsgiWebApplicationContext wc = (MotechOsgiWebApplicationContext) wac;
            wc.setBundleContext(bundleContext);
            if (applicationContext != null) {
                wac.setParent(applicationContext);
            }
        }
        super.postProcessWebApplicationContext(wac);
    }

    @Override
    protected void initFrameworkServlet() throws ServletException {
        getServletContext().log("Initialized servlet for " + bundleContext.getBundle().toString());
    }
}
