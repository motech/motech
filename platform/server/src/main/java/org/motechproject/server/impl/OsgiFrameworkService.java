package org.motechproject.server.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.eclipse.gemini.blueprint.OsgiException;
import org.motechproject.server.api.BundleLoader;
import org.motechproject.server.api.BundleLoadingException;
import org.motechproject.server.api.JarInformation;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.launch.Framework;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.BundleTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import static org.apache.commons.lang.StringUtils.startsWith;

/**
 * @author Ricky Wang
 */
public class OsgiFrameworkService implements ApplicationContextAware {
    private static final String PLATFORM_BUNDLES = "platform";
    private static final String MODULE_BUNDLES = "module";
    private static final String THIRD_PARTY_BUNDLES = "3party";

    private static final String STARTUP_TOPIC = "org/motechproject/osgi/event/STARTUP";

    private static Logger logger = LoggerFactory.getLogger(OsgiFrameworkService.class);

    private ApplicationContext applicationContext;

    private String internalBundleFolder;

    private String externalBundleFolder;

    private String fragmentSubFolder;

    @Autowired
    private Framework osgiFramework;

    private List<BundleLoader> bundleLoaders;

    private Map<String, List<Bundle>> bundles = new HashMap<>();

    private Map<String, ClassLoader> bundleClassLoaderLookup = new HashMap<String, ClassLoader>();

    private Map<String, String> bundleLocationMapping = new HashMap<String, String>();

    private static final int THREADS_NUMBER = 1;

    private boolean httpServiceRegistered = false;
    private boolean startupEventReceived = false;

    /**
     * Initialize, install and start bundles and the OSGi framework
     */
    public void start() {
        try {
            ServletContext servletContext = ((WebApplicationContext) applicationContext).getServletContext();

            osgiFramework.init();

            BundleContext bundleContext = osgiFramework.getBundleContext();

            // This is mandatory for Felix http servlet bridge
            servletContext.setAttribute(BundleContext.class.getName(), bundleContext);

            installAllBundles(servletContext, bundleContext);

            registerHttpServiceListener();

            startBundles(THIRD_PARTY_BUNDLES);

            registerStartupListener();

            registerBundleLoaderExecutor();

            startBundles(PLATFORM_BUNDLES);

            osgiFramework.start();

            logger.info("OSGi framework started");
        } catch (BundleException | BundleLoadingException | IOException | ClassNotFoundException |
                InvalidSyntaxException e) {
            logger.error("Failed to start OSGi framework", e);
            throw new OsgiException(e);
        }
    }

    private synchronized void startupModules() {
        if (httpServiceRegistered && startupEventReceived) {
            startBundles(MODULE_BUNDLES);
        }
    }

    private void registerStartupListener() throws ClassNotFoundException {
        BundleContext bundleContext = osgiFramework.getBundleContext();

        // use the EventHandler class from the eventadmin bundle's classloader and construct a proxy
        // we can't use the class from the webapp classloader
        ClassLoader eventAdminCl = getClassLoaderBySymbolicName("org.apache.felix.eventadmin");

        if (eventAdminCl == null) {
            allowStartup();
        } else {
            Class<?> eventHandlerClass = eventAdminCl.loadClass(EventHandler.class.getName());

            Object proxy = Proxy.newProxyInstance(eventAdminCl, new Class[] { eventHandlerClass }, new StartupListener());

            Dictionary<String, String[]> properties = new Hashtable<>();
            properties.put(EventConstants.EVENT_TOPIC, new String[] { STARTUP_TOPIC });

            bundleContext.registerService(EventHandler.class.getName(), proxy, properties);
        }
    }

    private void registerHttpServiceListener() throws InvalidSyntaxException {
        BundleContext bundleContext = osgiFramework.getBundleContext();

        bundleContext.addServiceListener(new ServiceListener() {
            @Override
            public void serviceChanged(ServiceEvent event) {
                if (event.getType() == ServiceEvent.REGISTERED) {
                    httpServiceRegistered();
                }
            }
        }, String.format("(&(%s=%s))", Constants.OBJECTCLASS, HttpService.class.getName()));
    }

    private void installAllBundles(ServletContext servletContext, BundleContext bundleContext) throws IOException, BundleLoadingException {
        for (URL url : findBundles(servletContext)) {
            if (!isBundle(url)) {
                logger.info("Skipping :" + url);
                continue;
            }
            logger.info("Installing bundle [" + url + "]");
            try {
                addBundle(bundleContext.installBundle(url.toExternalForm()));
            } catch (BundleException e) {
                throw new BundleLoadingException("Failed to install bundle from " + url, e);
            }
        }
    }

    private void addBundle(Bundle bundle) {
        String symbolicName = bundle.getSymbolicName();
        String key = null;

        if (isFragmentBundle(bundle) || !startsWith(symbolicName, "org.motechproject.motech-")) {
            key = THIRD_PARTY_BUNDLES;
        } else if (startsWith(symbolicName, "org.motechproject.motech-platform-")) {
            key = PLATFORM_BUNDLES;
        } else if (startsWith(symbolicName, "org.motechproject.motech-")) {
            key = MODULE_BUNDLES;
        }

        if (key != null) {
            if (!bundles.containsKey(key)) {
                bundles.put(key, new ArrayList<Bundle>());
            }

            bundles.get(key).add(bundle);
        } else {
            logger.error(String.format("Cant add bundle: %s", symbolicName));
        }
    }

    private void registerBundleLoaderExecutor() {
        /* bundle loader extensions will be registered so that custom loaders like JSPBundle
           loader can watch for other bundles and run extension service*/
        new BundleTracker(OsgiFrameworkService.this.osgiFramework.getBundleContext(), Bundle.STARTING, null) {
            @Override
            public Object addingBundle(Bundle bundle, BundleEvent event) {
                // custom bundle loaders
                if (bundleLoaders != null) {
                    for (BundleLoader loader : bundleLoaders) {
                        try {
                            loader.loadBundle(bundle);
                        } catch (Exception e) {
                            logger.error("Error while running custom bundle loader " + loader.getClass().getName() + " Error: " + e.getMessage());
                        }
                    }
                }
                return super.addingBundle(bundle, event);
            }
        }.open();
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

    private boolean isBundle(URL url) throws IOException {
        JarInputStream jarStream = new JarInputStream(url.openStream());
        Manifest mf = jarStream.getManifest();
        return null != mf.getMainAttributes().getValue(JarInformation.BUNDLE_SYMBOLIC_NAME);
    }

    private void startBundles(String key) {
        if (bundles.containsKey(key) && !bundles.get(key).isEmpty()) {
            ExecutorService bundleLoader = Executors.newFixedThreadPool(THREADS_NUMBER);

            for (Bundle bundle : bundles.get(key)) {
                bundleLoader.execute(new BundleStarter(bundle));
            }

            waitForBundles(bundleLoader);
        }
    }

    private void startBundle(Bundle bundle) throws BundleLoadingException, BundleException, ClassNotFoundException {
        logger.debug("Starting bundle [" + bundle + "]");

        storeClassCloader(bundle);

        if (!isFragmentBundle(bundle)) {
            bundle.start();
        }
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
        } catch (Exception e) {
            logger.error("Error stopping OSGi framework", e);
            throw new OsgiException(e);
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

    private void storeClassCloader(Bundle bundle) throws ClassNotFoundException {
        String key = bundle.getSymbolicName();
        String activator = (String) bundle.getHeaders().get(Constants.BUNDLE_ACTIVATOR);
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

    private List<URL> findBundles(ServletContext servletContext) throws IOException {
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
     * @throws MalformedURLException
     */
    private List<URL> findInternalBundles(ServletContext servletContext) throws MalformedURLException {
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
     * @throws java.io.IOException
     */
    private List<URL> findExternalBundles() throws IOException {
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

    private List<URL> findFragmentBundles() throws IOException {
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
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
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

    public void httpServiceRegistered() {
        httpServiceRegistered = true;
        startupModules();
    }

    public void allowStartup() {
        startupEventReceived = true;
        startupModules();
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
