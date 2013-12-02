package org.motechproject.osgi.web.ext;

import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang.StringUtils.isBlank;

public final class HttpContextFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpContextFactory.class);

    private HttpContextFactory() {
    }

    public static HttpContext getHttpContext(HttpContext httpContext, Bundle bundle) {
        LOGGER.info("Environment is " + ApplicationEnvironment.getEnvironment());

        if (!ApplicationEnvironment.isInDevelopmentMode()) {
            return null;
        }

        String resourceDirPath = ApplicationEnvironment.getModulePath(new BundleName(bundle.getSymbolicName()));

        if (isBlank(resourceDirPath)) {
            LOGGER.info(String.format("Resource path not given for bundle %s", bundle.getSymbolicName()));
            return null;
        }

        LOGGER.info(String.format("Resource path for bundle %s is %s", bundle.getSymbolicName(), resourceDirPath));
        return new FileSystemAwareUIHttpContext(httpContext, resourceDirPath);
    }

}
