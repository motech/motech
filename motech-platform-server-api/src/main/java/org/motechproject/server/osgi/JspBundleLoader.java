package org.motechproject.server.osgi;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

public class JspBundleLoader implements BundleLoader, ServletContextAware {

    private static Logger logger = LoggerFactory.getLogger(JspBundleLoader.class);

    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadBundle(Bundle bundle) throws Exception {
        Enumeration<URL> jsps = bundle.findEntries("/webapp", "*.jsp", true);
        if (jsps != null) {
            while (jsps.hasMoreElements()) {
                URL jspUrl = jsps.nextElement();

                String destFilename = buildDestFilename(jspUrl, bundle.getBundleId());
                File destFile = new File(destFilename);

                FileUtils.copyURLToFile(jspUrl, destFile);
                logger.debug("Loaded " + jspUrl.getFile() + " from [" + bundle.getLocation() + "]");
            }
        }
    }

    private String buildDestFilename(URL jspUrl, long bundleId) {
        String path = servletContext.getRealPath("/");
        String filename = jspUrl.getFile();
        StringBuilder sb = new StringBuilder();

        sb.append(path);
        if (!path.endsWith(File.separator)) {
            sb.append(File.separator);
        }

        sb.append(bundleId).append(File.separator);

        if (filename.startsWith(File.separator)) {
            sb.append(filename.substring(1));
        } else {
            sb.append(filename);
        }

        return sb.toString();
    }
}
