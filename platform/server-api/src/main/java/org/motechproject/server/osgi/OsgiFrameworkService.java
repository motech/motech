package org.motechproject.server.osgi;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Ricky Wang
 */
public class OsgiFrameworkService implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(OsgiFrameworkService.class);

    private ApplicationContext applicationContext;

    private String internalBundleFolder;

    private String externalBundleFolder;

    private String fragmentSubFolder;

    @Autowired
    private Framework osgiFramework;

    private List<BundleLoader> bundleLoaders;

    private List<Bundle> bundles = new ArrayList<>();

    private Map<String, ClassLoader> bundleClassLoaderLookup = new HashMap<String, ClassLoader>();

    private Map<String, String> bundleLocationMapping = new HashMap<String, String>();

    public static final String BUNDLE_ACTIVATOR_HEADER = "Bundle-Activator";

    private static final int THREADS_NUMBER = 10;

    /**
     * Initialize, install bundles and start non-MOTECH bundles and the OSGi framework
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
            }

            Bundle server = null;

            ExecutorService bundleLoader = Executors.newFixedThreadPool(THREADS_NUMBER);

            for (Bundle bundle : bundles) {
                String bundleSymbolicName = bundle.getSymbolicName();
                if (!bundleSymbolicName.startsWith("org.motechproject.motech-")) {
                    bundleLoader.execute(new BundleStarter(bundle));
                }

                if (bundleSymbolicName.equalsIgnoreCase("org.motechproject.motech-platform-server-bundle")) {
                    server = bundle;
                }
            }

            waitForBundles(bundleLoader);

            if (server != null) {
                startBundle(server);
            }

            osgiFramework.start();
            logger.info("OSGi framework started");
        } catch (Throwable e) {
            logger.error("Failed to start OSGi framework", e);
            throw new RuntimeException(e);
        }
    }

    private void waitForBundles(ExecutorService bundleLoader) {
        bundleLoader.shutdown();
        while (!bundleLoader.isTerminated()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.warn("InterruptedException when waiting for bundle loader to finish...");
            }
            logger.debug("Waiting for bundle loader to finish...");
        }
    }

    /**
     * Start MOTECH bundles
     */
    public void startMotechBundles() {

        ExecutorService bundleLoader = Executors.newFixedThreadPool(THREADS_NUMBER);

        for (Bundle bundle : bundles) {
            String bundleSymbolicName = bundle.getSymbolicName();

            if (bundleSymbolicName.startsWith("org.motechproject.motech-") && !bundleSymbolicName.contains("-platform-")) {
                try {
                    bundleLoader.execute(new BundleStarter(bundle));
                } catch (Exception e) {
                    logger.error("Failed to start Bundles", e);
                }
            }
        }

        waitForBundles(bundleLoader);
    }

    /**
     * Find first bundle with given name and start it
     *
     * @param bundleSymbolicName symbolic name of the bundle you want to launch
     * @return true if bundle was found and launched, otherwise false
     */
    public boolean startBundle(final String bundleSymbolicName) {
        boolean found = false;

        try {
            for (Bundle bundle : bundles) {
                if (bundle.getSymbolicName().equals(bundleSymbolicName)) {
                    startBundle(bundle);
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

    private void startBundle(Bundle bundle) throws Exception {
        logger.debug("Starting bundle [" + bundle + "]");

        storeClassCloader(bundle);

        // custom bundle loaders
        if (bundleLoaders != null) {
            for (BundleLoader loader : bundleLoaders) {
                loader.loadBundle(bundle);
            }
        }

        if (!isFragmentBundle(bundle)) {
            bundle.start();
        }
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
        List<URL> list = findFragmentBundles(); //start with fragment bundles
        list.addAll(findInternalBundles(servletContext));
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
        List<URL> list = new ArrayList<>();
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
        List<URL> list = new ArrayList<>();
        if (StringUtils.isNotBlank(externalBundleFolder)) {
            File folder = new File(externalBundleFolder);
            boolean exists = folder.exists();

            if (!exists) {
                exists = folder.mkdirs();
            }

            if (exists) {
                File[] files = folder.listFiles((FileFilter) new SuffixFileFilter(".jar"));
                list.addAll(Arrays.asList(FileUtils.toURLs(files)));
            }
        }

        return list;
    }

    List<URL> findFragmentBundles() throws Exception {
        List<URL> list = new ArrayList<>();
        String fragmentDirName = buildFragmentDirName();
        if (StringUtils.isNotBlank(fragmentDirName)) {
            File fragmentDir = new File(fragmentDirName);
            boolean exists = fragmentDir.exists();

            if (!exists) {
                exists = fragmentDir.mkdirs();
            }

            if (exists) {
                File[] files = fragmentDir.listFiles((FileFilter) new SuffixFileFilter(".jar"));
                list.addAll(Arrays.asList(FileUtils.toURLs(files)));
            }
        }
        return list;
    }

    private String buildFragmentDirName() {
        String result = null;
        if (StringUtils.isNotBlank(externalBundleFolder)) {
            StringBuilder sb = new StringBuilder(externalBundleFolder);
            if (!externalBundleFolder.endsWith(File.separator)) {
                sb.append(File.separatorChar);
            }
            sb.append(fragmentSubFolder);
            result = sb.toString();
        }

        return result;
    }

    private boolean isFragmentBundle(Bundle bundle) {
        return bundle.getHeaders().get("Fragment-Host") != null;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) {
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

    public String getFragmentSubFolder() {
        return fragmentSubFolder;
    }

    public void setFragmentSubFolder(String fragmentSubFolder) {
        this.fragmentSubFolder = fragmentSubFolder;
    }

    public List<JarInformation> getBundledModules() {
        ServletContext servletContext = ((WebApplicationContext) applicationContext).getServletContext();
        JarInformationHandler jarsHandler = new JarInformationHandler(servletContext.getRealPath("/"));
        jarsHandler.initHandler();
        return jarsHandler.getJarList();
    }

    public List<BundleInformation> getExternalBundles() {
        List<BundleInformation> bundleInformationList = new ArrayList<>();

        if (osgiFramework.getBundleContext() != null) {
            for (Bundle bundle : osgiFramework.getBundleContext().getBundles()) {
                bundleInformationList.add(new BundleInformation(bundle));
            }
        }

        return bundleInformationList;
    }

    public void restart(String symbolicName) throws BundleException {
        for(Bundle bundle : bundles) {
            if (bundle.getSymbolicName().equalsIgnoreCase(symbolicName)) {
                bundle.stop();
                bundle.start();
                break;
            }
        }
    }

    private class BundleStarter implements Runnable {

        private Bundle bundle;
        
        BundleStarter(Bundle bundle) {
            this.bundle = bundle;
        }

        @Override
        public void run() {
            try {
                startBundle(bundle);
            } catch (Exception e) {
                logger.error("Exception when starting bundle [" + bundle + "]", e);
            }
        }
    }
}
