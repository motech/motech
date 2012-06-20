package org.motechproject.server.demo.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.web.servlet.view.JstlView;

public class BundledJspView extends JstlView implements BundleContextAware {

    private static final String JSP_FILE = "org.apache.catalina.jsp_file";

    private BundleContext bundleContext;

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(JSP_FILE, "/" + bundleContext.getBundle().getBundleId() + getUrl());
        super.render(model, request, response);
    }
}
