package org.motechproject.server.impl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The <code>ProxyServlet</code> is loaded on startup after bootstrap-servlet.
 * The main reason this class is used instead of Felix proxy servlet {@link org.apache.felix.http.proxy.ProxyServlet}
 * is fact that initialization can be done after BundleContext is registered in the servlet context,
 * in our case it means when bootstrap config is present.
 */
public class ProxyServlet extends HttpServlet {

    private static final long serialVersionUID = -337240813688980442L;

    private static ProxyServlet instance;

    private org.apache.felix.http.proxy.ProxyServlet felixServlet;
    private ServletConfig servletConfig;

    public static ProxyServlet getInstance() {
        return instance;
    }

    public void reInit() throws ServletException {
        init(servletConfig);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        instance = this;
        servletConfig = config;

        if (OsgiListener.isBootstrapPresent()) {
            felixServlet = new org.apache.felix.http.proxy.ProxyServlet();
            felixServlet.init(config);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        felixServlet.service(req, res);
    }

    @Override
    public void destroy() {
        felixServlet.destroy();
    }
}
