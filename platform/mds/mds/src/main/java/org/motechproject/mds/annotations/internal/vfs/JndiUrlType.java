package org.motechproject.mds.annotations.internal.vfs;

import org.apache.commons.beanutils.MethodUtils;
import org.motechproject.osgi.web.util.OSGiServiceUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.reflections.vfs.Vfs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

/**
 * Vfs url type handling JNDI urls. JNDI urls will come for bundles loaded from the servlet context.
 */
public class JndiUrlType implements Vfs.UrlType {

    private static final String JNDI_SERVICE = "org.motechproject.server.jndi.JndiLookupService";
    private static final String WRITE_TO_FILE_METHOD = "writeToFile";

    @Override
    public boolean matches(URL url) {
        return "jndi".equals(url.getProtocol());
    }

    @Override
    public Vfs.Dir createDir(URL url) {
        try {
            File tmpFile = File.createTempFile("vfs-jndi-bundle", ".jar");

            // we need to load this resource from the server context using the server ClassLoader
            // we use reflections to call the service exposed by the server
            BundleContext bundleContext = FrameworkUtil.getBundle(JndiUrlType.class).getBundleContext();
            Object jndiService = OSGiServiceUtils.findService(bundleContext, JNDI_SERVICE);

            // copy to the resource to the jdo file
            MethodUtils.invokeMethod(jndiService, WRITE_TO_FILE_METHOD,
                    new Object[] {url.toString(), tmpFile.getAbsolutePath()});

            return new TmpDir(tmpFile);
        } catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("Unable to create mvn url for " + url, e);
        }
    }
}
