package org.motechproject.osgi.web.ext;

import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * This factory is responsible for creating {@link org.osgi.service.http.HttpContext} decorator objects
 * for bundles. If dynamic resource loading is set for a given bundle, meaning the <code>ENVIRONMENT</code> variable is set
 * to <code>DEVELOPMENT</code> and variable with an underscored version of the bundle symbolic name is defined, an instance
 * of {@link org.motechproject.osgi.web.ext.FileSystemAwareUIHttpContext} will be created for the bundle. In other
 * cases the provided contex is unchaged.
 */
public final class HttpContextFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpContextFactory.class);

    private HttpContextFactory() {
    }

    /**
     * Decorates the given HttpContext with a @{link FileSystemAwareUIHttpContext} if dynamic resource loading
     * is set up.
     * @param httpContext the default http context for a given bundle
     * @param bundle the bundle for which this http context will be registered
     * @return the decorated instance of the provided context if dynamic loading was set up, the original instance otherwise
     */
    public static HttpContext getHttpContext(HttpContext httpContext, Bundle bundle) {
        LOGGER.info("Environment is " + ApplicationEnvironment.getEnvironment());

        if (!ApplicationEnvironment.isInDevelopmentMode()) {
            return httpContext;
        }

        String resourceDirPath = ApplicationEnvironment.getModulePath(new BundleName(bundle.getSymbolicName()));

        if (isBlank(resourceDirPath)) {
            LOGGER.info(String.format("Resource path not given for bundle %s", bundle.getSymbolicName()));
            return httpContext;
        }

        LOGGER.info(String.format("Resource path for bundle %s is %s", bundle.getSymbolicName(), resourceDirPath));
        return new FileSystemAwareUIHttpContext(httpContext, resourceDirPath);
    }

}
