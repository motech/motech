package org.motechproject.server.osgi;

import org.apache.commons.lang.StringUtils;
import org.motechproject.scheduler.event.EventRelay;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.event.EventListenerRegistryService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ricky Wang
 */
public class OsgiFrameworkService implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(OsgiFrameworkService.class);

    private ApplicationContext applicationContext;

    private String internalBundleFolder;

    private String externalBundleFolder;

    @Autowired
    private Framework osgiFramework;

    @Autowired
    private PlatformSettingsService platformSettingsService;

    @Autowired
    private EventListenerRegistryService eventListenerRegistryService;

    @Autowired
    private EventRelay eventRelay;

    private List<BundleLoader> bundleLoaders;

    private List<Bundle> bundles = new ArrayList<>();

    private Map<String, ClassLoader> bundleClassLoaderLookup = new HashMap<String, ClassLoader>();

    private Map<String, String> bundleLocationMapping = new HashMap<String, String>();

    public static final String BUNDLE_ACTIVATOR_HEADER = "Bundle-Activator";

    /**
     * Initialize, install bundles and start internal bundles and the OSGi framework
     */
    public void start() {
        try {
            ServletContext servletContext = ((WebApplicationContext) applicationContext).getServletContext();

            osgiFramework.init();

            BundleContext bundleContext = osgiFramework.getBundleContext();

            // This is mandatory for Felix http servlet bridge
            servletContext.setAttribute(BundleContext.class.getName(), bundleContext);

            for (URL url : findBundles(servletContext)) {
                logger.debug("Installing bundle [" + url + "]");
                Bundle bundle = bundleContext.installBundle(url.toExternalForm());
                bundles.add(bundle);

                if (!bundle.getLocation().contains(".motech")) {
                    startBundle(bundle.getSymbolicName());
                }
            }

            registerPlatformServices(bundleContext);

            osgiFramework.start();
            logger.info("OSGi framework started");
        } catch (Throwable e) {
            logger.error("Failed to start OSGi framework", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Start external bundles
     */
    public void startExternalBundles() {
        try {
            ServletContext servletContext = ((WebApplicationContext) applicationContext).getServletContext();
            BundleContext bundleContext = (BundleContext) servletContext.getAttribute(BundleContext.class.getName());

            for (Bundle bundle : bundles) {
                startBundle(bundle.getSymbolicName());
            }
        } catch (Exception e) {
            logger.error("Failed to start Bundles", e);
        }
    }

    public void stopExternalBundles() {
        for (Bundle bundle : bundles) {
            try {
                if (bundle.getLocation().startsWith("file:")) {
                    bundle.stop();
                }
            } catch (BundleException e) {
                logger.error(String.format("Failed to stop %s bundle", bundle.getSymbolicName()), e);
            }
        }
    }

    /**
     * Find first bundle with given name and start it
     *
     * @param bundleName bundle name which you want to launch
     * @return true if bundle was found and launched, otherwise false
     */
    public boolean startBundle(final String bundleName) {
        boolean found = false;

        try {
            for (Bundle bundle : bundles) {
                if (bundle.getSymbolicName().contains(bundleName)) {
                    logger.debug("Starting bundle [" + bundle + "]");
                    storeClassCloader(bundle);
                    // custom bundle loaders
                    if (bundleLoaders != null) {
                        for (BundleLoader loader : bundleLoaders) {
                            loader.loadBundle(bundle);
                        }
                    }

                    bundle.start();
                    found = true;
                    break; // found bundle
                }
            }
        } catch (Exception e) {
            logger.error("Failed to start Bundle", e);
            found = false;
        }

        return found;
    }

    /**
     * Stop the OSGi framework.
     */
    public void stop() {
        try {
            if (osgiFramework != null) {
                osgiFramework.stop();
                logger.info("OSGi framework stopped");
            }
        } catch (Throwable e) {
            logger.error("Error stopping OSGi framework", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * The current OSGi (4.2) doesn't provide a standard way to retrieve the bundle ClassLoader.
     * So we have to use this as a workaround.
     *
     * @param bundleSymbolicName
     * @return The ClassLoader of the bundle
     */
    public ClassLoader getClassLoaderBySymbolicName(String bundleSymbolicName) {
        return bundleClassLoaderLookup.get(bundleSymbolicName);
    }

    public String getBundleLocationByBundleId(String bundleId) {
        return bundleLocationMapping.get(bundleId);
    }

    public Map<String, ClassLoader> getBundleClassLoaderLookup() {
        return bundleClassLoaderLookup;
    }

    private void registerPlatformServices(BundleContext bundleContext) {
        bundleContext.registerService(EventRelay.class.getName(), eventRelay, null);
        bundleContext.registerService(EventListenerRegistryService.class.getName(), eventListenerRegistryService, null);
        bundleContext.registerService(PlatformSettingsService.class.getName(), platformSettingsService, null);
    }

    private void storeClassCloader(Bundle bundle) throws Exception {
        String key = bundle.getSymbolicName();
        String activator = (String) bundle.getHeaders().get(BUNDLE_ACTIVATOR_HEADER);
        if (activator != null) {
            @SuppressWarnings("rawtypes")
            Class activatorClass = bundle.loadClass(activator);
            if (activatorClass != null) {
                bundleClassLoaderLookup.put(key, activatorClass.getClassLoader());
                String bundleLocation = bundle.getLocation();
                if (bundleLocation.startsWith("file:")) { // we do not want any jndi locations
                    bundleLocationMapping.put(bundle.getBundleId() + ".0", bundleLocation);
                }
            }
        }
    }

    private List<URL> findBundles(ServletContext servletContext) throws Exception {
        List<URL> list = findInternalBundles(servletContext);
        list.addAll(findExternalBundles());
        return list;
    }

    /**
     * Find built-in/mandatory bundles
     *
     * @param servletContext
     * @return
     * @throws Exception
     */
    private List<URL> findInternalBundles(ServletContext servletContext) throws Exception {
        List<URL> list = new ArrayList<URL>();
        if (StringUtils.isNotBlank(internalBundleFolder)) {
            @SuppressWarnings("unchecked")
            Set<String> paths = servletContext.getResourcePaths(internalBundleFolder);
            if (paths != null) {
                for (String path : paths) {
                    if (path.endsWith(".jar")) {
                        URL url = servletContext.getResource(path);
                        if (url != null) {
                            list.add(url);
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * Find external/optional bundles
     *
     * @return
     * @throws Exception
     */
    private List<URL> findExternalBundles() throws Exception {
        List<URL> list = new ArrayList<URL>();
        if (StringUtils.isNotBlank(externalBundleFolder)) {
            File folder = new File(externalBundleFolder);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            File[] files = folder.listFiles();
            for (File file : files) {
                if (file.getAbsolutePath().endsWith(".jar")) {
                    URL url = file.toURI().toURL();
                    if (url != null) {
                        list.add(url);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }

    public void setInternalBundleFolder(String bundleFolder) {
        this.internalBundleFolder = bundleFolder;
    }

    public void setExternalBundleFolder(String externalBundleFolder) {
        this.externalBundleFolder = externalBundleFolder;
    }

    public void setOsgiFramework(Framework osgiFramework) {
        this.osgiFramework = osgiFramework;
    }

    public void setBundleLoaders(List<BundleLoader> bundleLoaders) {
        this.bundleLoaders = bundleLoaders;
    }

    public String getInternalBundleFolder() {
        return internalBundleFolder;
    }

    public String getExternalBundleFolder() {
        return externalBundleFolder;
    }

    public List<JarInformation> getBundledModules() {
        ServletContext servletContext = ((WebApplicationContext) applicationContext).getServletContext();
        JarInformationHandler jarsHandler = new JarInformationHandler(servletContext.getRealPath("/"));
        jarsHandler.initHandler();
        return jarsHandler.getJarList();
    }

    public List<BundleInformation> getExternalBundles() {
        List<BundleInformation> bundles = new ArrayList<>();

        if (osgiFramework.getBundleContext() != null) {
            for (Bundle bundle : osgiFramework.getBundleContext().getBundles()) {
                bundles.add(new BundleInformation(bundle));
            }
        }

        return bundles;
    }
}
