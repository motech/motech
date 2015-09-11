package org.motechproject.mds.osgi;

import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.motechproject.commons.api.ThreadSuspender;
import org.motechproject.mds.annotations.internal.AnnotationProcessingContext;
import org.motechproject.mds.annotations.internal.EntityProcessorOutput;
import org.motechproject.mds.annotations.internal.MDSAnnotationProcessor;
import org.motechproject.mds.annotations.internal.MDSProcessorOutput;
import org.motechproject.mds.annotations.internal.SchemaComparator;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.ex.MdsException;
import org.motechproject.mds.helper.MdsBundleHelper;
import org.motechproject.mds.loader.EditableLookupsLoader;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.AllTypes;
import org.motechproject.mds.repository.SchemaChangeLockManager;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.MigrationService;
import org.motechproject.mds.util.SecurityHolder;
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
import org.springframework.util.StopWatch;

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
    private AllEntities allEntities;
    private AllTypes allTypes;

    private boolean processingSuspended = false;
    private Queue<AwaitingBundle> awaitingBundles = new LinkedBlockingQueue<>();

    private final Object lock = new Object();

    // called by the initializer after the initial entities bundle was generated
    public void start() {
        StopWatch stopWatch = new StopWatch();

        LOGGER.info("Scanning for MDS annotations");
        bundlesToRefresh = new ArrayList<>();

        TransactionTemplate tmpl = new TransactionTemplate(transactionManager);

        // load types and entities beforehand
        stopWatch.start();
        AnnotationProcessingContext context = tmpl.execute(new TransactionCallback<AnnotationProcessingContext>() {
            @Override
            public AnnotationProcessingContext doInTransaction(TransactionStatus status) {
                schemaChangeLockManager.acquireLock(MdsBundleWatcher.class.getName() +
                        " - retrieving context for annotation processing");

                AnnotationProcessingContext context = createContext();

                schemaChangeLockManager.releaseLock(MdsBundleWatcher.class.getName() +
                        " - retrieving context for annotation processing");

                return context;
            }
        });
        stopWatch.stop();

        LOGGER.debug("Retrieval of context finished in {} ms", stopWatch.getTotalTimeMillis());

        stopWatch.start();
        final Map<String, MDSProcessorOutput> outputs = processInstalledBundles(context);
        stopWatch.stop();

        LOGGER.debug("Annotation processing finished in {} ms", stopWatch.getTotalTimeMillis());

        stopWatch.start();
        context = tmpl.execute(new TransactionCallback<AnnotationProcessingContext>() {
            @Override
            public AnnotationProcessingContext doInTransaction(TransactionStatus status) {
                schemaChangeLockManager.acquireLock(MdsBundleWatcher.class.getName() + " - saving annotation processing results");

                LOGGER.info("Processing bundles for migrations and JSON lookups");

                for (Bundle bundle : bundleContext.getBundles()) {
                    if (hasNonEmptyOutput(outputs.get(bundle.getSymbolicName()))) {

                        LOGGER.debug("Processing {} for migrations and JSON", bundle.getSymbolicName());

                        try {
                            migrationService.processBundle(bundle);
                        } catch (IOException e) {
                            LOGGER.error("An error occurred while copying the migrations from bundle: {}", bundle.getSymbolicName(), e);
                        }

                        try {
                            editableLookupsLoader.addEditableLookups(outputs.get(bundle.getSymbolicName()), bundle);
                        } catch (MdsException e) {
                            LOGGER.error("Unable to read JSON defined lookups from bundle: {}", bundle, e);
                        }
                    }
                }

                for (Map.Entry<String, MDSProcessorOutput> entry : outputs.entrySet()) {
                    final String symbolicName = entry.getKey();
                    final MDSProcessorOutput output = entry.getValue();

                    LOGGER.debug("Saving processor output for {}", symbolicName);
                    processAnnotationScanningResults(output.getEntityProcessorOutputs(), output.getLookupProcessorOutputs());
                }

                AnnotationProcessingContext context = createContext();

                schemaChangeLockManager.releaseLock(MdsBundleWatcher.class.getName() + " - saving annotation processing results");

                return context;
            }
        });
        stopWatch.stop();

        LOGGER.debug("Annotation results saved in {} ms", stopWatch.getTotalTimeMillis());

        refreshBundles(bundlesToRefresh, context);

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

    private Map<String, MDSProcessorOutput> processInstalledBundles(AnnotationProcessingContext context) {
        Map<String, MDSProcessorOutput> outputs = new HashMap<>();

        for (Bundle bundle : bundleContext.getBundles()) {
            MDSProcessorOutput output = process(bundle, context);
            if (hasNonEmptyOutput(output)) {
                outputs.put(bundle.getSymbolicName(), output);

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
                processBundle(bundle);
            }
        } else if (eventType == BundleEvent.UNRESOLVED && !skipBundle(bundle)) {
            LOGGER.info("Unregistering JDO classes for Bundle: {}", bundle.getSymbolicName());
            MdsBundleHelper.unregisterBundleJDOClasses(bundle);
        } else if (eventType == BundleEvent.UNINSTALLED && !skipBundle(bundle)) {
            refreshBundle(bundle, null);
        }
    }

    private void processBundle(final Bundle bundle) {
        // load types and entities beforehand
        AnnotationProcessingContext context = new AnnotationProcessingContext(allEntities.retrieveAll(),
                allTypes.retrieveAll());

        final MDSProcessorOutput output = process(bundle, context);

        if (hasNonEmptyOutput(output)) {
            TransactionTemplate tmpl = new TransactionTemplate(transactionManager);
            tmpl.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    schemaChangeLockManager.acquireLock(MdsBundleWatcher.class.getName() + " - saving output of bundle processing");

                    processAnnotationScanningResults(output.getEntityProcessorOutputs(), output.getLookupProcessorOutputs());

                    schemaChangeLockManager.releaseLock(MdsBundleWatcher.class.getName() + " - saving output of bundle processing");
                }
            });

            tmpl = new TransactionTemplate(transactionManager);
            tmpl.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    schemaChangeLockManager.acquireLock(MdsBundleWatcher.class.getName() + " - searching for flyway migrations");

                    try {
                        migrationService.processBundle(bundle);
                    } catch (IOException e) {
                        LOGGER.error("An error occurred while copying the migrations from bundle: {}", bundle.getSymbolicName(), e);
                    }

                    schemaChangeLockManager.releaseLock(MdsBundleWatcher.class.getName() + " - searching for flyway migrations");
                }
            });
            // if we found annotations, we will refresh the bundle in order to start weaving the
            // classes it exposes
            tmpl = new TransactionTemplate(transactionManager);
            tmpl.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    schemaChangeLockManager.acquireLock(MdsBundleWatcher.class.getName() + " - refreshing after bundle event");

                    refreshBundle(bundle, createContext());

                    schemaChangeLockManager.releaseLock(MdsBundleWatcher.class.getName() + " - refreshing after bundle event");
                }
            });
        }
    }

    private MDSProcessorOutput process(Bundle bundle, AnnotationProcessingContext context) {
        if (skipBundle(bundle)) {
            return null;
        }

        synchronized (lock) {
            // Before we process annotations, we wait until bundle resolves its dependencies
            int count = 0;
            while (bundle.getState() < Bundle.RESOLVED && count < MAX_WAIT_TO_RESOLVE) {
                ThreadSuspender.sleep(500);
                count++;
            }

            LOGGER.debug("Processing bundle {}", bundle.getSymbolicName());
            return processor.processAnnotations(bundle, context);
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

    private void refreshBundle(Bundle bundle, AnnotationProcessingContext context) {
        refreshBundles(Collections.singletonList(bundle), context);
    }

    private void refreshBundles(List<Bundle> bundles, AnnotationProcessingContext context) {
        if (LOGGER.isInfoEnabled()) {
            for (Bundle bundle : bundles) {
                LOGGER.info("Refreshing wiring for bundle {}", bundle.getSymbolicName());
            }
        }

        // we generate the entities bundle but not start it to avoid exceptions when the framework
        // will refresh bundles
        jarGeneratorService.regenerateMdsDataBundle(false, context);

        FrameworkWiring framework = bundleContext.getBundle(0).adapt(FrameworkWiring.class);
        framework.refreshBundles(bundles);

        // give the framework 3 seconds to do a refresh
        ThreadSuspender.sleep(3000);

        // after refreshing all bundles we can start the entities bundle
        monitor.start();
    }

    private void processAnnotationScanningResults(List<EntityProcessorOutput> entityProcessorOutput,
                                                  Map<String, List<LookupDto>> lookupProcessingResult) {
        Map<String, Long> entityIdMappings = new HashMap<>();
        Set<String> newEntities = new HashSet<>();

        for (EntityProcessorOutput result : entityProcessorOutput) {
            LOGGER.debug("Processing result for {}", result.getEntityProcessingResult().getClassName());

            EntityDto processedEntity = result.getEntityProcessingResult();

            EntityDto entity = entityService.getEntityByClassName(processedEntity.getClassName());

            if (entity == null) {
                entity = entityService.createEntity(processedEntity);
                newEntities.add(entity.getClassName());
            }
            entityIdMappings.put(entity.getClassName(), entity.getId());

            SecurityHolder securityHolder = new SecurityHolder(processedEntity.getSecurityMode(),
                    processedEntity.getReadOnlySecurityMode(), processedEntity.getSecurityMembers(),
                    processedEntity.getReadOnlySecurityMembers());

            entityService.updateEntity(entity.getId(), result.getRestProcessingResult(),
                    result.getTrackingProcessingResult(), result.getFieldProcessingResult(),
                    result.getUiFilterableProcessingResult(), result.getUiDisplayableProcessingResult(),
                    securityHolder);
        }

        for (Map.Entry<String, List<LookupDto>> entry : lookupProcessingResult.entrySet()) {
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
            processBundle(awaitingBundle.bundle);
        }
    }

    private AnnotationProcessingContext createContext() {
        return new AnnotationProcessingContext(allEntities.retrieveAll(), allTypes.retrieveAll());
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

    @Autowired
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }

    @Autowired
    public void setAllTypes(AllTypes allTypes) {
        this.allTypes = allTypes;
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
