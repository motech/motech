package org.motechproject.mds.osgi;

import org.apache.commons.lang.time.StopWatch;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.motechproject.commons.api.StopWatchHelper;
import org.motechproject.commons.api.ThreadSuspender;
import org.motechproject.mds.annotations.internal.EntityProcessorOutput;
import org.motechproject.mds.annotations.internal.MDSAnnotationProcessor;
import org.motechproject.mds.annotations.internal.MDSProcessorOutput;
import org.motechproject.mds.annotations.internal.SchemaComparator;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.exception.MdsException;
import org.motechproject.mds.helper.bundle.MdsBundleHelper;
import org.motechproject.mds.loader.EditableLookupsLoader;
import org.motechproject.mds.repository.internal.SchemaChangeLockManager;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.MigrationService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.wiring.FrameworkWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import static org.apache.commons.lang.StringUtils.startsWith;

/**
 * The <code>MdsBundleWatcher</code> in Motech Data Services listens for bundle installation and
 * processes the annotations in the given bundle. It also processes all installed bundles after startup.
 * After annotations are found in a bundle, the entities jar is regenerated and the target bundle is refreshed.
 */
@Component
public class MdsBundleWatcher implements SynchronousBundleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MdsBundleWatcher.class);

    private static final int MAX_WAIT_TO_RESOLVE = 10;
    private static final int WAIT_TIME = 500;

    private MDSAnnotationProcessor processor;
    private JarGeneratorService jarGeneratorService;
    private MigrationService migrationService;
    private BundleContext bundleContext;
    private EntitiesBundleMonitor monitor;
    private EntityService entityService;
    private JdoTransactionManager transactionManager;
    private List<Bundle> bundlesToRefresh;
    private SchemaChangeLockManager schemaChangeLockManager;
    private EditableLookupsLoader editableLookupsLoader;
    private SchemaComparator schemaComparator;

    private boolean processingSuspended = false;
    private Queue<AwaitingBundle> awaitingBundles = new LinkedBlockingQueue<>();

    private final Object lock = new Object();

    // called by the initializer after the initial entities bundle was generated
    public void start() {
        LOGGER.info("Scanning for MDS annotations");
        bundlesToRefresh = new ArrayList<>();

        StopWatch stopWatch = new StopWatch();

        SchemaHolder schemaHolder = lockAndGetSchema();
        final List<MDSProcessorOutput> mdsProcessorOutputs = processInstalledBundles(schemaHolder);

        stopWatch.start();
        new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                schemaChangeLockManager.acquireLock(MdsBundleWatcher.class.getName() + " - start annotation processing");

                for (MDSProcessorOutput output : mdsProcessorOutputs) {
                    processAnnotationScanningResults(output);
                }

                schemaChangeLockManager.releaseLock(MdsBundleWatcher.class.getName() + " - start annotation processing");
            }
        });
        stopWatch.stop();

        LOGGER.info("Annotation processing finished in {} ms", stopWatch.getTime());

        // if we found annotations, we will refresh the bundle in order to start weaving the
        // classes it exposes
        if (!bundlesToRefresh.isEmpty()) {
            LOGGER.info("Starting bundle refresh process");

            schemaHolder = lockAndGetSchema();

            LOGGER.info("Refreshing bundles: {}", bundlesToRefresh);

            StopWatchHelper.restart(stopWatch);
            refreshBundles(bundlesToRefresh, schemaHolder);
            stopWatch.stop();

            LOGGER.info("Bundle refresh finished in {} ms", stopWatch.getTime());
        } else {
            LOGGER.info("No bundles to refresh, proceeding");
        }

        bundleContext.addBundleListener(this);
    }

    /**
     * Invoked, when an event about bundle change is received. In case a new bundle gets installed
     * or an existing bundle is updated, we need to scan that bundle for MDS annotations and process them.
     *
     * @param event BundleEvent, generated by the OSGi framework
     */
    @Override
    public void bundleChanged(BundleEvent event) {
        final Bundle bundle = event.getBundle();

        final int eventType = event.getType();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Bundle event of type {} received from {}: {} -> {}", OsgiStringUtils.nullSafeBundleEventToString(event.getType()),
                    bundle.getSymbolicName(), String.valueOf(eventType), String.valueOf(bundle.getState()));
        }

        handleBundleEvent(bundle, eventType);
    }

    private List<MDSProcessorOutput> processInstalledBundles(SchemaHolder schemaHolder) {
        List<MDSProcessorOutput> outputs = new ArrayList<>();

        for (Bundle bundle : bundleContext.getBundles()) {
            MDSProcessorOutput output = process(bundle, schemaHolder);
            if (hasNonEmptyOutput(output)) {
                outputs.add(output);

                bundlesToRefresh.add(bundle);
            }
        }

        return outputs;
    }

    private void handleBundleEvent(final Bundle bundle, final int eventType) {
        if (eventType == BundleEvent.INSTALLED || eventType == BundleEvent.UPDATED) {
            if (processingSuspended) {
                awaitingBundles.add(new AwaitingBundle(bundle, eventType));
            } else {
                processSingleBundle(bundle);
            }
        } else if (eventType == BundleEvent.UNRESOLVED && !skipBundle(bundle)) {
            LOGGER.info("Unregistering JDO classes for Bundle: {}", bundle.getSymbolicName());
            MdsBundleHelper.unregisterBundleJDOClasses(bundle);
        } else if (eventType == BundleEvent.UNINSTALLED && !skipBundle(bundle)) {
            SchemaHolder schemaHolder = lockAndGetSchema();
            refreshBundle(bundle, schemaHolder);
        }
    }

    private void processSingleBundle(final Bundle bundle) {
        SchemaHolder schemaHolder = lockAndGetSchema();

        final MDSProcessorOutput output = process(bundle, schemaHolder);

        if (hasNonEmptyOutput(output)) {
            TransactionTemplate tmpl = new TransactionTemplate(transactionManager);
            tmpl.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    schemaChangeLockManager.acquireLock(MdsBundleWatcher.class.getName() + " - saving output of bundle processing");

                    processAnnotationScanningResults(output);

                    schemaChangeLockManager.releaseLock(MdsBundleWatcher.class.getName() + " - saving output of bundle processing");
                }
            });

            schemaHolder = lockAndGetSchema();

            // if we found annotations, we will refresh the bundle in order to start weaving the
            // classes it exposes
            refreshBundle(bundle, schemaHolder);
        }
    }

    private MDSProcessorOutput process(Bundle bundle, SchemaHolder schemaHolder) {
        if (skipBundle(bundle)) {
            return null;
        }

        synchronized (lock) {
            // Before we process annotations, we wait until bundle resolves its dependencies
            int count = 0;
            while (!isBundleResolved(bundle) && count < MAX_WAIT_TO_RESOLVE) {
                ThreadSuspender.sleep(WAIT_TIME);
                count++;
            }

            // Assert the bundle is resolved before processing annotations, to log any problems before annotation processing fails.
            assertBundleClassLoading(bundle);

            LOGGER.debug("Processing bundle {}", bundle.getSymbolicName());
            return processor.processAnnotations(bundle, schemaHolder);
        }
    }

    private boolean isBundleResolved(Bundle bundle) {
        return bundle.getState() >= Bundle.RESOLVED;
    }

    private void assertBundleClassLoading(Bundle bundle) {
        // We attempt to find a class that any bundle should be able to load.
        // If exception is thrown, this signals a problem with resolving the bundle.
        // This is done to get access to the BundleException and its stacktrace that lies behind the ClassNotFoundException
        try {
            bundle.loadClass(Object.class.getName());
        } catch (ClassNotFoundException e) {
            LOGGER.error("The {} [{}] bundle cannot be resolved. This might indicate a problem with bundle dependencies. " +
                            "Any further exceptions are most likely caused by this problem.",
                    bundle.getSymbolicName(), bundle.getBundleId(), e.getCause());
        }
    }

    private boolean skipBundle(Bundle bundle) {
        // we skip the generated entities bundle, MDS bundle and the framework bundle
        if (MdsBundleHelper.isMdsBundle(bundle) || MdsBundleHelper.isMdsEntitiesBundle(bundle) ||
                MdsBundleHelper.isFrameworkBundle(bundle)) {
            return true;
        }

        // we also skip bundles which locations start with "link:", as these are pax exam bundles, which we
        // encounter only during tests. Maybe in some distant future, support for resolving these locations will be
        // added, but there is no need to do it right now.
        if (startsWith(bundle.getLocation(), "link:") || startsWith(bundle.getLocation(), "local")) {
            return true;
        }

        // finally we skip bundles that don't have an MDS dependency
        return !MdsBundleHelper.isBundleMdsDependent(bundle);
    }

    private void refreshBundle(Bundle bundle, SchemaHolder schemaHolder) {
        refreshBundles(Collections.singletonList(bundle), schemaHolder);
    }

    private void refreshBundles(List<Bundle> bundles, SchemaHolder schemaHolder) {
        if (LOGGER.isInfoEnabled()) {
            for (Bundle bundle : bundles) {
                LOGGER.info("Refreshing wiring for bundle {}", bundle.getSymbolicName());
            }
        }

        // we generate the entities bundle but not start it to avoid exceptions when the framework
        // will refresh bundles
        jarGeneratorService.regenerateMdsDataBundle(schemaHolder, false);

        FrameworkWiring framework = bundleContext.getBundle(0).adapt(FrameworkWiring.class);
        framework.refreshBundles(bundles);

        // give the framework 3 seconds to do a refresh
        ThreadSuspender.sleep(3000);

        // after refreshing all bundles we can start the entities bundle
        monitor.start();
    }

    private void processAnnotationScanningResults(MDSProcessorOutput output) {
        Map<String, Long> entityIdMappings = new HashMap<>();
        Set<String> newEntities = new HashSet<>();

        Bundle bundle = output.getBundle();

        try {
            migrationService.processBundle(bundle);
        } catch (IOException e) {
            LOGGER.error("An error occurred while copying the migrations from bundle: {}", bundle.getSymbolicName(), e);
        }

        try {
            editableLookupsLoader.addEditableLookups(output, bundle);
        } catch (MdsException e) {
            LOGGER.error("Unable to read JSON defined lookups from bundle: {}", bundle, e);
        }

        for (EntityProcessorOutput result : output.getEntityProcessorOutputs()) {
            EntityDto processedEntity = result.getEntityProcessingResult();

            EntityDto entity = entityService.getEntityByClassName(processedEntity.getClassName());

            if (entity == null) {
                entity = entityService.createEntity(processedEntity);
                newEntities.add(entity.getClassName());
            }
            entityIdMappings.put(entity.getClassName(), entity.getId());

            entityService.updateRestOptions(entity.getId(), result.getRestProcessingResult());
            entityService.updateTracking(entity.getId(), result.getTrackingProcessingResult());
            entityService.addFields(entity, result.getFieldProcessingResult());
            entityService.addFilterableFields(entity, result.getUiFilterableProcessingResult());
            entityService.addDisplayedFields(entity, result.getUiDisplayableProcessingResult());
            entityService.updateSecurityOptions(entity.getId(), processedEntity.getSecurityMode(),
                    processedEntity.getSecurityMembers(), processedEntity.getReadOnlySecurityMode(), processedEntity.getReadOnlySecurityMembers());
            entityService.updateMaxFetchDepth(entity.getId(), processedEntity.getMaxFetchDepth());
            entityService.addNonEditableFields(entity, result.getNonEditableProcessingResult());
        }

        for (Map.Entry<String, List<LookupDto>> entry : output.getLookupProcessorOutputs().entrySet()) {
            String entityClassName = entry.getKey();
            Long entityId = entityIdMappings.get(entityClassName);
            if (schemaComparator.lookupsDiffer(entityId, entry.getValue())) {
                entityService.addLookups(entityId, entry.getValue());
                if (!newEntities.contains(entityClassName)) {
                    entityService.incrementVersion(entityId);
                }
            }
        }
    }

    private boolean hasNonEmptyOutput(MDSProcessorOutput output) {
        return output != null && !(output.getEntityProcessorOutputs().isEmpty() &&
                output.getLookupProcessorOutputs().isEmpty());
    }

    public void suspendProcessing() {
        processingSuspended = true;
    }

    public void restoreProcessing() {
        processingSuspended = false;

        while (!awaitingBundles.isEmpty()) {
            AwaitingBundle awaitingBundle = awaitingBundles.poll();
            processSingleBundle(awaitingBundle.bundle);
        }
    }

    private SchemaHolder lockAndGetSchema() {
        LOGGER.info("Retrieving MDS schema");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        TransactionTemplate trxTemplate = new TransactionTemplate(transactionManager);

        final SchemaHolder schemaHolder = trxTemplate.execute(new TransactionCallback<SchemaHolder>() {
            @Override
            public SchemaHolder doInTransaction(TransactionStatus status) {
                schemaChangeLockManager.acquireLock(MdsBundleWatcher.class.getName() + " - start refreshing bundles");

                SchemaHolder result = entityService.getSchema();

                schemaChangeLockManager.releaseLock(MdsBundleWatcher.class.getName() + " - start refreshing bundles");

                return result;
            }
        });

        stopWatch.stop();

        LOGGER.info("Schema retrieved in {} ms", stopWatch.getTime());

        return schemaHolder;
    }

    @Autowired
    public void setProcessor(MDSAnnotationProcessor processor) {
        this.processor = processor;
    }

    @Autowired
    public void setJarGeneratorService(JarGeneratorService jarGeneratorService) {
        this.jarGeneratorService = jarGeneratorService;
    }

    @Autowired
    public void setMigrationService(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Autowired
    public void setMonitor(EntitiesBundleMonitor monitor) {
        this.monitor = monitor;
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    @Autowired
    public void setTransactionManager(JdoTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Autowired
    public void setSchemaChangeLockManager(SchemaChangeLockManager schemaChangeLockManager) {
        this.schemaChangeLockManager = schemaChangeLockManager;
    }

    @Autowired
    public void setEditableLookupsLoader(EditableLookupsLoader editableLookupsLoader) {
        this.editableLookupsLoader = editableLookupsLoader;
    }

    @Autowired
    public void setSchemaComparator(SchemaComparator schemaComparator) {
        this.schemaComparator = schemaComparator;
    }

    private class AwaitingBundle {
        private Bundle bundle;
        private int eventType;

        public AwaitingBundle(Bundle bundle, int eventType) {
            this.bundle = bundle;
            this.eventType = eventType;
        }
    }
}
