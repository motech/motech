package org.motechproject.osgi.web;

import org.osgi.framework.BundleContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

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
}
