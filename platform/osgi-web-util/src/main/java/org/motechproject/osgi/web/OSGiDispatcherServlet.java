package org.motechproject.osgi.web;

import org.osgi.framework.BundleContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;

/**
 * This class extends Spring's {@link org.springframework.web.servlet.DispatcherServlet} and is used
 * by MOTECH for registering HTTP endpoints. This extension adds support for OSGi by making
 * sure that {@link MotechOSGiWebApplicationContext} instances are connected
 * with their parent context instances, created by the Gemini Extender. It also injects the bundle context
 * into those contexts.
 */
public class OSGiDispatcherServlet extends DispatcherServlet {

    private static final long serialVersionUID = -4481880383536608077L;

    private BundleContext bundleContext;
    private ConfigurableWebApplicationContext configurableWebApplicationContext;

    public OSGiDispatcherServlet(BundleContext bundleContext) {
        this(bundleContext, null);
    }


    public OSGiDispatcherServlet(BundleContext bundleContext, ConfigurableWebApplicationContext configurableWebApplicationContext) {
        this.bundleContext = bundleContext;
        this.configurableWebApplicationContext = configurableWebApplicationContext;
    }

    @Override
    protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
        if (wac instanceof MotechOSGiWebApplicationContext) {
            MotechOSGiWebApplicationContext wc = (MotechOSGiWebApplicationContext) wac;
            wc.setBundleContext(bundleContext);
            if (configurableWebApplicationContext != null) {
                configurableWebApplicationContext.setServletContext(getServletContext());
                wac.setParent(configurableWebApplicationContext);
            }
        }
        super.postProcessWebApplicationContext(wac);
    }

    @Override
    protected void initFrameworkServlet() throws ServletException {
        getServletContext().log("Initialized servlet for " + bundleContext.getBundle().toString());
    }
}
