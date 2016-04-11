package org.motechproject.server.osgi;

import org.eclipse.gemini.blueprint.OsgiException;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.extender.support.scanning.ConfigurationScanner;
import org.eclipse.gemini.blueprint.extender.support.scanning.DefaultConfigurationScanner;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.motechproject.server.osgi.event.OsgiEventProxy;
import org.motechproject.server.osgi.event.impl.OsgiEventProxyImpl;
import org.motechproject.server.osgi.status.PlatformStatusManager;
import org.motechproject.server.osgi.status.PlatformStatusManagerImpl;
import org.motechproject.server.osgi.util.BundleType;
import org.motechproject.server.osgi.util.PlatformConstants;
import org.motechproject.server.osgi.util.ValidationWeavingHook;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


/**
 * The PlatformActivator is responsible for starting up MOTECH. Formerly this code lived in the WAR archive
 * (OSGiFrameworkService). It was moved to its own bundle, so that it can be reused during PAX integration tests.
 * The activator first starts 3rd party bundles, then MDS and its dependencies. After MDS is started it continues to start
 * platform bundles. When it gets the startup event from the server-bundle (meaning MOTECH is initialized) it will start
 * other modules.
 */
public class PlatformActivator implements BundleActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformActivator.class);

    private boolean httpServiceRegistered;
    private boolean startupEventReceived;
    private boolean platformStarted;

    private BundleContext bundleContext;

    private PlatformStatusManagerImpl platformStatusManager;

    private final Object lock = new Object();

    private Map<BundleType, List<Bundle>> bundlesByType = new HashMap<>();

    @Override
    public void start(BundleContext context) throws InvalidSyntaxException, ClassNotFoundException {
        this.bundleContext = context;

        // first categorize the bundles in a map, for our own convenience
        categorizeBundles();

        // we register the listeners for services and events
        registerListeners();

        startHttp();

        // start all 3rd party libraries
        startBundles(BundleType.THIRD_PARTY_BUNDLE);

        registerEventProxy();

        // register OSGi hooks
        registerOSGiHooks();

        // start the http bridge
        startBundles(BundleType.HTTP_BUNDLE);

        // start platform bundles on which MDS depends on
        startBundles(BundleType.PLATFORM_BUNDLE_PRE_MDS);

        // start MDS
        startBundles(BundleType.MDS_BUNDLE);

        // continues in postMdsStart() after MDS gets started

        // in case there is no MDS bundle(test environment), we continue with startup right away
        if (!bundlesByType.containsKey(BundleType.MDS_BUNDLE)) {
            postMdsStartup();
        }
    }

    @Override
    public void stop(BundleContext context) {
        LOGGER.info("MOTECH Platform bundle stopped");
    }


    private void postMdsStartup() throws ClassNotFoundException {
        LOGGER.info("MDS started, continuing startup");

        // we start bundles required for web-security start
        startBundles(BundleType.PLATFORM_BUNDLE_PRE_WS);

        // we start web-security itself
        startBundles(BundleType.WS_BUNDLE);

        // make sure security is started
        if (bundlesByType.containsKey(BundleType.WS_BUNDLE)) {
            verifyBundleState(Bundle.ACTIVE, PlatformConstants.SECURITY_SYMBOLIC_NAME);
        }

        // we start other platform bundles
        startBundles(BundleType.PLATFORM_BUNDLE_POST_WS);

        platformStarted();

        LOGGER.info("MOTECH Platform started");
    }

    private void registerEventProxy() {
        ServiceReference<EventAdmin> ref = bundleContext.getServiceReference(EventAdmin.class);
        if (ref == null) {
            throw new IllegalStateException("OSGi event Admin unavailable");
        } else {
            EventAdmin eventAdmin = bundleContext.getService(ref);

            OsgiEventProxy osgiEventProxy = new OsgiEventProxyImpl(eventAdmin);
            platformStatusManager.setOsgiEventProxy(osgiEventProxy);
            bundleContext.registerService(OsgiEventProxy.class, osgiEventProxy, null);
        }
    }

    private void registerOSGiHooks() {
        bundleContext.registerService(WeavingHook.class, new ValidationWeavingHook(), new Hashtable<>());
    }

    private void registerListeners() throws InvalidSyntaxException, ClassNotFoundException {
        // HTTP service and the startup event coming from the server-bundle are required for booting up modules
        registerHttpServiceListener();
        registerStartupListener();

        // We want to also know when MDS starts
        registerMdsStartupListener();

        // this is for monitoring the startup status
        registerStatusManager();
    }
    private void registerHttpServiceListener() throws InvalidSyntaxException {
        bundleContext.addServiceListener(new ServiceListener() {
            @Override
            public void serviceChanged(ServiceEvent event) {
                if (event.getType() == ServiceEvent.REGISTERED) {
                    LOGGER.info("Http service registered");
                    httpServiceRegistered();
                }
            }
        }, String.format("(&(%s=%s))", Constants.OBJECTCLASS, HttpService.class.getName()));
    }

    private void registerStartupListener() throws ClassNotFoundException {
        Dictionary<String, String[]> properties = new Hashtable<>();
        properties.put(EventConstants.EVENT_TOPIC, new String[]{ PlatformConstants.STARTUP_TOPIC });

        bundleContext.registerService(EventHandler.class.getName(), new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                allowStartup();
            }
        }, properties);
    }

    private void registerMdsStartupListener() {
        Dictionary<String, String[]> properties = new Hashtable<>();
        properties.put(EventConstants.EVENT_TOPIC, new String[]{ PlatformConstants.MDS_STARTUP_TOPIC});

        bundleContext.registerService(EventHandler.class.getName(), new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                try {
                    postMdsStartup();
                } catch (ClassNotFoundException e) {
                    throw new OsgiException(e);
                }
            }
        }, properties);
    }


    private void registerStatusManager() {
        List<Bundle> bundles = new ArrayList<>();
        if (bundlesByType.containsKey(BundleType.MOTECH_MODULE)) {
            bundles.addAll(bundlesByType.get(BundleType.MOTECH_MODULE));
        }
        if (bundlesByType.containsKey(BundleType.MDS_BUNDLE)) {
            bundles.addAll(bundlesByType.get(BundleType.MDS_BUNDLE));
        }
        if (bundlesByType.containsKey(BundleType.PLATFORM_BUNDLE_PRE_MDS)) {
            bundles.addAll(bundlesByType.get(BundleType.PLATFORM_BUNDLE_PRE_MDS));
        }
        if (bundlesByType.containsKey(BundleType.PLATFORM_BUNDLE_PRE_WS)) {
            bundles.addAll(bundlesByType.get(BundleType.PLATFORM_BUNDLE_PRE_WS));
        }
        if (bundlesByType.containsKey(BundleType.WS_BUNDLE)) {
            bundles.addAll(bundlesByType.get(BundleType.WS_BUNDLE));
        }
        if (bundlesByType.containsKey(BundleType.PLATFORM_BUNDLE_POST_WS)) {
            bundles.addAll(bundlesByType.get(BundleType.PLATFORM_BUNDLE_POST_WS));
        }

        List<Bundle> osgiBundles = new ArrayList<>();
        List<Bundle> blueprintBundles = new ArrayList<>();

        ConfigurationScanner configurationScanner = new DefaultConfigurationScanner();

        for(Bundle bundle : bundles){
            String[] config = configurationScanner.getConfigurations(bundle);

            if (config.length > 0) {
                blueprintBundles.add(bundle);
            } else {
                osgiBundles.add(bundle);
            }
        }

        platformStatusManager = new PlatformStatusManagerImpl(osgiBundles, blueprintBundles);

        bundleContext.addBundleListener(platformStatusManager);

        bundleContext.registerService(OsgiBundleApplicationContextListener.class, platformStatusManager, null);
        bundleContext.registerService(PlatformStatusManager.class, platformStatusManager, null);
    }

    private void startHttp() {
        try {
            Bundle httpBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, PlatformConstants.HTTP_BRIDGE_BUNDLE);
            if (httpBundle != null) {
                startBundle(httpBundle, BundleType.HTTP_BUNDLE);
            } else {
                LOGGER.warn("Felix http bundle unavailable, http endpoints will not be active");
            }
        } catch (BundleException e) {
            LOGGER.error("Error while starting the http bundle", e);
        }
    }

    private void startBundles(BundleType bundleType) {
        LOGGER.info("Starting bundles of type {}", bundleType.name());

        List<Bundle> bundlesToStart = bundlesByType.get(bundleType);

        if (bundlesToStart != null) {
            for (Bundle bundle : bundlesToStart) {
                if (shouldStartBundle(bundle)) {
                    try {
                        startBundle(bundle, bundleType);
                    } catch (BundleException | RuntimeException e) {
                        LOGGER.error("Error while starting bundle " + bundle.getSymbolicName(), e);
                        platformStatusManager.registerBundleError(bundle.getSymbolicName(), e.getMessage());
                    }
                }
            }
        }
    }

    private void startBundle(Bundle bundle, BundleType bundleType) throws BundleException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Starting {} {}", bundleType.name(), bundle.getSymbolicName());
        }
        bundle.start();
    }

    private boolean shouldStartBundle(Bundle bundle) {
        return !PlatformConstants.PLATFORM_BUNDLE_SYMBOLIC_NAME.equals(bundle.getSymbolicName()) &&
                (bundle.getState() == Bundle.INSTALLED || bundle.getState() == Bundle.RESOLVED);
    }

    private void httpServiceRegistered() {
        synchronized (lock) {
            httpServiceRegistered = true;
        }
        startupModules();
    }

    private void allowStartup() {
        synchronized (lock) {
            startupEventReceived = true;
        }
        startupModules();
    }

    private void platformStarted() {
        synchronized (lock) {
            platformStarted = true;
        }
        startupModules();
    }

    private void startupModules() {
        synchronized (lock) {
            if (httpServiceRegistered && startupEventReceived && platformStarted) {
                startBundles(BundleType.MOTECH_MODULE);
            }
        }
    }

    private void categorizeBundles() {
        for (Bundle bundle : bundleContext.getBundles()) {
            BundleType type = BundleType.forBundle(bundle);

            if (!bundlesByType.containsKey(type)) {
                bundlesByType.put(type, new ArrayList<>());
            }

            bundlesByType.get(type).add(bundle);
        }
    }

    private void verifyBundleState(int targetBundleValue, String bundleSymbolicName) {
        for (Bundle bundle : bundleContext.getBundles()) {
            if (bundleSymbolicName.equals(bundle.getSymbolicName())) {
                if (bundle.getState() == targetBundleValue) {
                    return;
                }
            }
        }
        throw new OsgiException("Bundle: " + bundleSymbolicName + " did not start properly");
    }
}
