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

    public static HttpContext getHttpContext(HttpContext httpContext, Bundle bundle, ApplicationEnvironment environment) {
        LOGGER.info("Environment is " + environment.getEnvironment());

        if (!environment.isInDevelopmentMode()) {
            return null;
        }

        String resourceDirPath = environment.getModulePath(new BundleName(bundle.getSymbolicName()));

        if (isBlank(resourceDirPath)) {
            LOGGER.info(String.format("Resource path not given for bundle %s", bundle.getSymbolicName()));
            return null;
        }

        LOGGER.info(String.format("Resource path for bundle %s is %s", bundle.getSymbolicName(), resourceDirPath));
        return new FileSystemAwareUIHttpContext(httpContext, resourceDirPath);
    }

}
