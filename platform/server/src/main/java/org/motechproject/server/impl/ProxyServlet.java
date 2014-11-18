package org.motechproject.server.impl;

import org.apache.felix.http.proxy.DispatcherTracker;
import org.osgi.framework.BundleContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import java.io.IOException;

/**
 * The <code>ProxyServlet</code> is loaded on startup after bootstrap-servlet.
 * The main reason this class is used instead of Felix proxy servlet {@link org.apache.felix.http.proxy.ProxyServlet}
 * is fact that initialization can be done after BundleContext is registered in the servlet context,
 * in our case it means when bootstrap config is present.
 */
public class ProxyServlet extends HttpServlet {
    private DispatcherTracker tracker;
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) throws ServletException {
        if (OsgiListener.isBootstrapPresent()) {
            super.init(config);

            try {
                this.tracker = new DispatcherTracker(getBundleContext(), null, getServletConfig());
                this.tracker.open();
            } catch (ServletException e) {
                throw e;
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } else {
            this.config = config;
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (this.tracker != null) {
            HttpServlet dispatcher = this.tracker.getDispatcher();
            if (dispatcher != null) {
                dispatcher.service(req, res);
            } else {
                res.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            }
        } else {
            res.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            init(config);
        }
    }

    @Override
    public void destroy() {
        this.tracker.close();
        super.destroy();
    }

    private BundleContext getBundleContext() throws ServletException {
        Object context = getServletContext().getAttribute(BundleContext.class.getName());
        if (context instanceof BundleContext) {
            return (BundleContext) context;
        }

        throw new ServletException("Bundle context attribute [" + BundleContext.class.getName() +
                "] not set in servlet context");
    }
}
