package org.motechproject.server.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyServlet.class);

    private static final long serialVersionUID = -337240813688980442L;

    private static ProxyServlet instance;

    private org.apache.felix.http.proxy.ProxyServlet felixServlet;
    private ServletConfig servletConfig;

    public static ProxyServlet getInstance() {
        return instance;
    }

    public void reInit() throws ServletException {
        LOGGER.info("Reinitialize triggered");
        init(servletConfig);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        LOGGER.info("Initializing proxy servlet for OSGi");

        instance = this;
        servletConfig = config;

        if (OsgiListener.isBootstrapPresent()) {
            felixServlet = new org.apache.felix.http.proxy.ProxyServlet();
            felixServlet.init(config);
            LOGGER.info("Proxy servlet for OSGi initialized");
        } else {
            LOGGER.info("No bootstrap config, didn't initialize");
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());
            if (felixServlet == null) {
                LOGGER.warn("OSGi proxy servlet not yet initialized, yet received request for {} from {}",
                        req.getPathInfo(), req.getRemoteAddr());
                new org.springframework.web.servlet.DispatcherServlet().service(req, res);

            } else {
                felixServlet.service(req, res);
            }
    }

    @Override
    public void destroy() {
        LOGGER.info("Destroying servlet");
        if (felixServlet != null) {
            felixServlet.destroy();
        }
    }
}
