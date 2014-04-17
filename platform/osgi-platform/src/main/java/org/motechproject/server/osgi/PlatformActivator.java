package org.motechproject.server.osgi;

import org.eclipse.gemini.blueprint.OsgiException;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.service.event.Event;
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
 *
 */
public class PlatformActivator implements BundleActivator {

    private static final Logger LOG = LoggerFactory.getLogger(PlatformActivator.class);

    private boolean httpServiceRegistered;
    private boolean startupEventReceived;

    private BundleContext bundleContext;

    private final Object lock = new Object();

    private Map<BundleType, List<Bundle>> bundlesByType = new HashMap<>();

    @Override
    public void start(BundleContext context) throws Exception {
        this.bundleContext = context;

        categorizeBundles();

        for (Bundle bundle : bundlesByType.get(BundleType.PAX_EXAM_BUNDLE)) {
            bundle.stop();
        }

        registerListeners();

        LOG.info("Starting 3rd party bundles");

        startBundles(BundleType.THIRD_PARTY_BUNDLE);

        startBundles(BundleType.HTTP_BUNDLE);

        startBundles(BundleType.PLATFORM_BUNDLE_PRE_MDS);

        startBundles(BundleType.MDS_BUNDLE);

        // continues in postMdsStart()
    }

    private void postMdsStartup() throws ClassNotFoundException {
        LOG.info("Starting platform bundles");

        startBundles(BundleType.PAX_EXAM_BUNDLE);

        startBundles(BundleType.PLATFORM_BUNDLE_PRE_WS);

        startBundles(BundleType.WS_BUNDLE);

        // verifyBundleState(Bundle.RESOLVED, PlatformConstants.SECURITY_BUNDLE_SYMBOLIC_NAME);

        startBundles(BundleType.PLATFORM_BUNDLE_POST_WS);

        LOG.info("MOTECH Platform started");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        LOG.info("Platform bundle stopped");
    }

    private void registerListeners() throws InvalidSyntaxException, ClassNotFoundException {
        registerDbServiceListener();

        registerHttpServiceListener();

        registerMdsStartupListener();

        registerStartupListener();
    }

    private void registerDbServiceListener() throws InvalidSyntaxException {
        bundleContext.addServiceListener(new ServiceListener() {
            @Override
            public void serviceChanged(ServiceEvent event) {
                if (event.getType() == ServiceEvent.REGISTERED) {
                    try {
                        LOG.info("Db service registered, starting the http bridge");
                        startHttp();
                    } catch (Exception e) {
                        LOG.error("Unable to start the Felix http bridge", e);
                    }
                }
            }
        }, String.format("(&(%s=%s))", Constants.OBJECTCLASS, PlatformConstants.DB_SERVICE_CLASS));
    }

    private void registerHttpServiceListener() throws InvalidSyntaxException {
        bundleContext.addServiceListener(new ServiceListener() {
            @Override
            public void serviceChanged(ServiceEvent event) {
                if (event.getType() == ServiceEvent.REGISTERED) {
                    LOG.info("Http service registered");
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

    private void startHttp() throws BundleException, ClassNotFoundException {
        Bundle httpBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, PlatformConstants.HTTP_BRIDGE_BUNDLE);
        if (httpBundle != null) {
            startBundle(httpBundle, BundleType.HTTP_BUNDLE);
        } else {
            LOG.warn("Felix http bundle unavailable, http endpoints will not be active");
        }
    }

    private void startBundles(BundleType bundleType) {
        List<Bundle> bundlesToStart = bundlesByType.get(bundleType);

        if (bundlesToStart != null) {
            for (Bundle bundle : bundlesToStart) {
                if (shouldStartBundle(bundle)) {
                    try {
                        startBundle(bundle, bundleType);
                    } catch (BundleException e) {
                        LOG.error("Error while starting bundle " + bundle.getSymbolicName(), e);
                    }
                }
            }
        }
    }

    private void startBundle(Bundle bundle, BundleType bundleType) throws BundleException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Starting {} {}", new String[]{bundleType.name(), bundle.getSymbolicName()});
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

    private void startupModules() {
        synchronized (lock) {
            if (httpServiceRegistered && startupEventReceived) {
                startBundles(BundleType.MOTECH_MODULE);
            }
        }
    }

    private void categorizeBundles() {
        for (Bundle bundle : bundleContext.getBundles()) {
            BundleType type = BundleType.forBundle(bundle);

            if (!bundlesByType.containsKey(type)) {
                bundlesByType.put(type, new ArrayList<Bundle>());
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
