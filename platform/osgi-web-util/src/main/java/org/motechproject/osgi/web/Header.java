package org.motechproject.osgi.web;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.osgi.web.util.BundleHeaders;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

import static org.motechproject.osgi.web.ext.ApplicationEnvironment.isInDevelopmentMode;

public class Header {
    private static final Logger LOGGER = LoggerFactory.getLogger(Header.class);

    private static final String HEADER_TEMPLATE = "header.template";
    private static final String HEADER_MIN_TEMPLATE = "header-min.template";
    private static final String RESOURCE_PATH_PARAM = "$RESOURCE_PATH";

    private BundleContext bundleContext;
    private String resourcePath;

    @Autowired
    public Header(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String asString() {
        String template = isInDevelopmentMode() ? HEADER_TEMPLATE : HEADER_MIN_TEMPLATE;
        Bundle bundle = bundleContext.getBundle();

        if (resourcePath == null) {
            resourcePath = new BundleHeaders(bundle).getResourcePath();
        }

        StringWriter out = new StringWriter();
        InputStream is = null;
        URL resource = bundle.getResource(template);

        if (null != resource) {
            try {
                is = resource.openStream();
                IOUtils.copy(is, out);
            } catch (IOException e) {
                throw new MotechException("Header could not be written", e);
            } finally {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(out);
            }
        } else {
            LOGGER.warn("Not found resource for: " + template);
        }

        return StringUtils.replace(out.toString(), RESOURCE_PATH_PARAM, resourcePath);
    }

}
