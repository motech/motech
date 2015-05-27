package org.motechproject.osgi.web;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.motechproject.osgi.web.exception.RenderException;
import org.osgi.framework.BundleContext;
import org.springframework.web.servlet.view.JstlView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * This is a class that should be used as the <b>viewClass</b> with Spring view resolvers in
 * order to support loading of JSP pages coming from OSGi bundles in Tomcat.
 * This class will set the <b>org.apache.catalina.jsp_file</b> attribute in the request. That attribute
 * is recognized by Tomcat and will make it load the JSP from the bundle. When registered as bean, this will
 * obtain the bundle context (since it implements {@link org.eclipse.gemini.blueprint.context.BundleContextAware}
 * and will point Tomcat to resources of the bundle the context comes from.
 */
public class BundledJspView extends JstlView implements BundleContextAware {

    private static final String JSP_FILE = "org.apache.catalina.jsp_file";

    private BundleContext bundleContext;

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws RenderException {
        request.setAttribute(JSP_FILE, "/" + bundleContext.getBundle().getBundleId() + getUrl());
        try {
            super.render(model, request, response);
        } catch (Exception e) {
            throw new RenderException(e);
        }
    }
}
