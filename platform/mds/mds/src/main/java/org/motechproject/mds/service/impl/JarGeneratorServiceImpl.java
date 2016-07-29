package org.motechproject.mds.service.impl;

import javassist.CannotCompileException;
import javassist.CtClass;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.commons.api.ThreadSuspender;
import org.motechproject.mds.tasks.MDSDataProvider;
import org.motechproject.mds.builder.MDSConstructor;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.entityinfo.EntityInfo;
import org.motechproject.mds.entityinfo.FieldInfo;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.dto.UIDisplayFieldComparator;
import org.motechproject.mds.event.CrudEventBuilder;
import org.motechproject.mds.exception.MdsException;
import org.motechproject.mds.helper.ActionParameterTypeResolver;
import org.motechproject.mds.helper.bundle.MdsBundleHelper;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.osgi.EntitiesBundleMonitor;
import org.motechproject.mds.repository.internal.MetadataHolder;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.JdoListenerRegistryService;
import org.motechproject.mds.service.MdsOsgiBundleApplicationContextListener;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.JavassistUtil;
import org.motechproject.osgi.web.util.BundleHeaders;
import org.motechproject.osgi.web.util.WebBundleUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.wiring.FrameworkWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static java.util.jar.Attributes.Name;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.split;
import static org.motechproject.mds.util.Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME;

import static org.motechproject.mds.util.Constants.BundleNames.SERVER_CONFIG_MODULE;
import static org.motechproject.mds.util.Constants.BundleNames.TASKS_MODULE;
import static org.motechproject.mds.util.Constants.BundleNames.WEB_SECURITY_MODULE;
import static org.motechproject.mds.util.Constants.Manifest.BUNDLE_MANIFESTVERSION;
import static org.motechproject.mds.util.Constants.Manifest.BUNDLE_NAME_SUFFIX;
import static org.motechproject.mds.util.Constants.Manifest.MANIFEST_VERSION;
import static org.motechproject.mds.util.Constants.PackagesGenerated;

/**
 * Default implementation of {@link org.motechproject.mds.service.JarGeneratorService} interface.
 */
@Service
public class JarGeneratorServiceImpl implements JarGeneratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JarGeneratorServiceImpl.class);
    private static final Long WAIT_TIME = 50L;
    private static final Integer MAX_WAIT_COUNT = 1000;

    private BundleHeaders bundleHeaders;
    private MetadataHolder metadataHolder;
    private MDSConstructor mdsConstructor;
    private JdoListenerRegistryService jdoListenerRegistryService;
    private VelocityEngine velocityEngine;
    private MDSDataProvider mdsDataProvider;
    private EntitiesBundleMonitor monitor;
    private BundleContext bundleContext;
    private MdsOsgiBundleApplicationContextListener mdsOsgiBundleApplicationContextListener;

    private final Object lock = new Object();
    private boolean moduleRefreshed;

    @Override
    public synchronized void regenerateMdsDataBundle(SchemaHolder schemaHolder) {
        regenerateMdsDataBundle(schemaHolder, true);
    }

    @Override
    public void regenerateMdsDataBundleAfterDdeEnhancement(SchemaHolder schemaHolder, String... moduleNames) {
        regenerateMdsDataBundle(schemaHolder, true, null == moduleNames ? new String[0] : moduleNames);
    }

    @Override
    public void regenerateMdsDataBundle(SchemaHolder schemaHolder, boolean startBundle) {
        regenerateMdsDataBundle(schemaHolder, startBundle, new String[0]);
    }

    private synchronized void regenerateMdsDataBundle(SchemaHolder schemaHolder, boolean startBundle, String... moduleNames) {
        LOGGER.info("Regenerating the mds entities bundle");

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

            clearModulesCache(moduleNames);
            cleanEntitiesBundleCachedClasses();

            boolean constructed = mdsConstructor.constructEntities(schemaHolder);

            if (!constructed) {
                return;
            }

            LOGGER.info("Updating mds data provider");
            mdsDataProvider.updateDataProvider(schemaHolder);

            File dest = new File(monitor.bundleLocation());
            if (dest.exists()) {
                // proceed when the bundles context is ready, we want the context processors to finish
                LOGGER.info("Waiting for entities context");
                monitor.waitForEntitiesContext();
            }

            File tmpBundleFile;

            try {
                LOGGER.info("Generating bundle jar");
                tmpBundleFile = generate(schemaHolder);
                LOGGER.info("Generated bundle jar");
            } catch (IOException e) {
                throw new MdsException("Unable to generate entities bundle", e);
            }

            FileUtils.deleteQuietly(dest);

            try {
                FileUtils.copyFile(tmpBundleFile, dest);
            } catch (IOException e) {
                LOGGER.error("Unable to copy the mds-entities bundle to the bundle directory. Installing from temp directory", e);
                // install from temp directory
                dest = tmpBundleFile;
            }

            monitor.stopEntitiesBundle();

            // In case of some core bundles, we must first stop some modules in order to avoid problems during refresh
            stopModulesForCoreBundleRefresh(moduleNames);

            try {
                monitor.start(dest, false);
            } finally {
                FileUtils.deleteQuietly(tmpBundleFile);
            }

            // We must clear module names which was restarted after failing
            mdsOsgiBundleApplicationContextListener.clearBundlesSet();
            refreshModules(moduleNames);

            if (startBundle) {
                monitor.start();
            }

            // Start bundles again if we stopped them manually
            startModulesForCoreBundleRefresh(moduleNames);

            // Give framework some time before returning to the caller
            ThreadSuspender.sleep(2000);
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    private void stopModulesForCoreBundleRefresh(String[] moduleNames) {
        if (Arrays.asList(moduleNames).contains(WEB_SECURITY_MODULE)) {
            stopBundle(WEB_SECURITY_MODULE);
        } else if (Arrays.asList(moduleNames).contains(SERVER_CONFIG_MODULE)) {
            stopBundle(SERVER_CONFIG_MODULE);
            stopBundle(WEB_SECURITY_MODULE);
        } else if (Arrays.asList(moduleNames).contains(TASKS_MODULE)){
            stopBundle(SERVER_CONFIG_MODULE);
            stopBundle(WEB_SECURITY_MODULE);
        }
    }

    private void startModulesForCoreBundleRefresh(String[] moduleNames) {
        if (Arrays.asList(moduleNames).contains(WEB_SECURITY_MODULE)) {
            startBundle(WEB_SECURITY_MODULE);
        } else if (Arrays.asList(moduleNames).contains(SERVER_CONFIG_MODULE)) {
            startBundle(SERVER_CONFIG_MODULE);
            startBundle(WEB_SECURITY_MODULE);
        } else if (Arrays.asList(moduleNames).contains(TASKS_MODULE)){
            startBundle(SERVER_CONFIG_MODULE);
            startBundle(WEB_SECURITY_MODULE);
        }
    }

    private void stopBundle(String moduleName) {
        Bundle bundle = WebBundleUtil.findBundleBySymbolicName(bundleContext, moduleName);
        try {
            bundle.stop();
        } catch (BundleException e) {
            LOGGER.error("A problem occured trying to stop bundle {} before refresh", moduleName);
        }
    }

    private void startBundle(String moduleName) {
        Bundle bundle = WebBundleUtil.findBundleBySymbolicName(bundleContext, moduleName);
        try {
            bundle.start();
        } catch (BundleException e) {
            LOGGER.error("A problem occured trying to start bundle {} after refresh", moduleName);
        }
    }

    private void refreshModules(String... moduleNames) {
        if (isAnyModuleNameNotBlank(moduleNames)) {
            for (String moduleName : moduleNames) {
                if (StringUtils.isNotBlank(moduleName)) {
                    refreshModule(moduleName);
                }
            }
        }
    }

    private boolean isAnyModuleNameNotBlank(String... moduleNames) {
        for (String moduleName : moduleNames) {
            if (StringUtils.isNotBlank(moduleName)) {
                return true;
            }
        }
        return false;
    }

    private void clearModulesCache(String[] moduleNames) {
        for (String moduleName : moduleNames) {
            if (StringUtils.isNotBlank(moduleName)) {
                Bundle bundleToRefresh = WebBundleUtil.findBundleBySymbolicName(bundleContext, moduleName);

                Bundle frameworkBundle = bundleContext.getBundle(0);
                FrameworkWiring frameworkWiring = frameworkBundle.adapt(FrameworkWiring.class);
                Collection<Bundle> dependencyClosureBundles = frameworkWiring.getDependencyClosure(Arrays.asList(bundleToRefresh));

                for (Bundle bundle : dependencyClosureBundles) {
                    MdsBundleHelper.unregisterBundleJDOClasses(bundle);
                }
            }
        }

        ResourceBundle.clearCache();
    }

    @Override
    public File generate(SchemaHolder schemaHolder) throws IOException {
        Path tempDir = Files.createTempDirectory("mds");
        Path tempFile = Files.createTempFile(tempDir, "mds-entities", ".jar");

        java.util.jar.Manifest manifest = createManifest();
        StringBuilder entityNamesSb = new StringBuilder();
        StringBuilder historyEntitySb = new StringBuilder();

        try (FileOutputStream fileOutput = new FileOutputStream(tempFile.toFile());
             JarOutputStream output = new JarOutputStream(fileOutput, manifest)) {

            List<EntityInfo> information = new ArrayList<>();

            for (ClassData classData : MotechClassPool.getEnhancedClasses(false)) {
                String className = classData.getClassName();

                // insert entity class, only for EUDE, note that this can also be a generated enum class
                if (!classData.isDDE()) {
                    addEntry(output, JavassistUtil.toClassPath(className), classData.getBytecode());
                }

                // insert history and trash classes, these classes will not be present for enums
                ClassData historyClassData = MotechClassPool.getHistoryClassData(className);
                if (historyClassData != null) {
                    addEntry(output, JavassistUtil.toClassPath(historyClassData.getClassName()),
                            historyClassData.getBytecode());
                    historyEntitySb.append(className).append('\n');
                }

                ClassData trashClassData = MotechClassPool.getTrashClassData(className);
                if (trashClassData != null) {
                    addEntry(output, JavassistUtil.toClassPath(trashClassData.getClassName()),
                            trashClassData.getBytecode());
                }

                if (!classData.isEnumClassData()) {
                    EntityDto entity = schemaHolder.getEntityByClassName(classData.getClassName());
                    List<FieldDto> fields = schemaHolder.getFields(entity);
                    AdvancedSettingsDto advancedSettings = schemaHolder.getAdvancedSettings(entity);

                    EntityInfo info = buildEntityInfo(entity, fields, advancedSettings);

                    // we keep the name to construct a file containing all entity names
                    // the file is required for schema generation
                    entityNamesSb.append(className).append('\n');

                    // insert repository
                    String repositoryName = MotechClassPool.getRepositoryName(className);
                    if (addClass(output, repositoryName)) {
                        info.setRepository(repositoryName);
                    }

                    // insert service implementation
                    String serviceClass = MotechClassPool.getServiceImplName(className);
                    if (addClass(output, serviceClass)) {
                        info.setServiceClass(serviceClass);
                        info.setServiceName(ClassName.getServiceName(className));
                    }

                    // insert the interface
                    String interfaceName = MotechClassPool.getInterfaceName(className);
                    if (MotechClassPool.isServiceInterfaceRegistered(className)) {
                        // we import the service interface
                        info.setInterfaceName(interfaceName);
                    } else {
                        // we generated the service interface from scratch and include it in the bundle
                        if (addClass(output, interfaceName)) {
                            info.setInterfaceName(interfaceName);
                        }
                    }

                    information.add(info);
                }
            }

            String blueprint = mergeTemplate(information, BLUEPRINT_TEMPLATE);
            String context = mergeTemplate(information, MDS_ENTITIES_CONTEXT_TEMPLATE);
            String channel = mergeTemplate(information, MDS_CHANNEL_TEMPLATE);
            jdoListenerRegistryService.updateEntityNames();
            jdoListenerRegistryService.removeInactiveListeners(entityNamesSb.toString());
            String entityWithListenersNames = jdoListenerRegistryService.getEntitiesListenerStr();

            addEntries(output, blueprint, context, channel, entityNamesSb.toString(), historyEntitySb.toString(),
                    entityWithListenersNames);
            addEntityInfoFiles(output, information);

            return tempFile.toFile();
        }
    }

    private EntityInfo buildEntityInfo(EntityDto entity, List<FieldDto> fields, AdvancedSettingsDto advancedSettings) {
        EntityInfo info = new EntityInfo();

        info.setEntity(entity);
        info.setAdvancedSettings(advancedSettings);
        info.setFieldsInfo(getFieldsInfo(entity, fields, advancedSettings));

        return info;
    }

    private void addEntries(JarOutputStream output, String blueprint, String context, String channel,
                            String entityNames, String historyEntities, String entityWithListenersNames) throws IOException  {
        addEntry(output, PACKAGE_JDO, metadataHolder.getJdoMetadata().toString().getBytes());
        addEntry(output, BLUEPRINT_XML, blueprint.getBytes());
        addEntry(output, MDS_ENTITIES_CONTEXT, context.getBytes());
        addEntry(output, TASK_CHANNEL_JSON, channel.getBytes());
        addEntry(output, ENTITY_LIST_FILE, entityNames.getBytes());
        addEntry(output, HISTORY_LIST_FILE, historyEntities.getBytes());
        addEntry(output, LISTENER_LIST_FILE, entityWithListenersNames.getBytes());
        addEntry(output, MDS_COMMON_CONTEXT);
        addEntry(output, DATANUCLEUS_PROPERTIES);
        addEntry(output, FLYWAY_PROPERTIES);
        addEntry(output, MOTECH_MDS_PROPERTIES);
        addEntry(output, VALIDATION_PROVIDER);
    }

    private void addEntityInfoFiles(JarOutputStream output, List<EntityInfo> entityInfos) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();

        for (EntityInfo entityInfo : entityInfos) {
            String asJson = objectMapper.writeValueAsString(entityInfo);
            addEntry(output, ENTITY_INFO_DIR + entityInfo.getClassName() + ".json",
                    asJson.getBytes(Charset.forName("UTF-8")));
        }
    }

    private boolean addClass(JarOutputStream output, String name) {
        CtClass clazz = MotechClassPool.getDefault().getOrNull(name);
        boolean added = false;

        if (null != clazz) {
            try {
                addEntry(output, JavassistUtil.toClassPath(name), clazz.toBytecode());
                added = true;
            } catch (IOException | CannotCompileException e) {
                LOGGER.error("There were problems with adding entry: ", e);
                added = false;
            }
        }

        return added;
    }

    private void addEntry(JarOutputStream output, String name) throws IOException {
        addEntry(output, name, null);
    }

    private void addEntry(JarOutputStream output, String name, byte[] bytes) throws IOException {
        JarEntry entry = new JarEntry(name);

        output.putNextEntry(entry);

        if (null != bytes) {
            output.write(bytes);
        } else {
            writeResourceToStream(name, output);
        }

        output.closeEntry();
    }

    private String mergeTemplate(List<EntityInfo> information, String templatePath) {
        StringWriter writer = new StringWriter();
        Map<String, Object> model = new HashMap<>();

        model.put("StringUtils", StringUtils.class);
        model.put("ClassName", ClassName.class);
        model.put("Entity", EntityInfo.class);
        model.put("CrudEventBuilder", CrudEventBuilder.class);

        model.put("list", information);

        VelocityEngineUtils.mergeTemplate(velocityEngine, templatePath, model, writer);

        return writer.toString();
    }

    private java.util.jar.Manifest createManifest() throws IOException {
        java.util.jar.Manifest manifest = new java.util.jar.Manifest();
        Attributes attributes = manifest.getMainAttributes();

        // standard attributes
        attributes.put(Name.MANIFEST_VERSION, MANIFEST_VERSION);

        // osgi attributes
        attributes.putValue(Constants.BUNDLE_MANIFESTVERSION, BUNDLE_MANIFESTVERSION);
        attributes.putValue(Constants.BUNDLE_NAME, createName());
        attributes.putValue(Constants.BUNDLE_SYMBOLICNAME, MDS_ENTITIES_SYMBOLIC_NAME);
        attributes.putValue(Constants.BUNDLE_VERSION, bundleHeaders.getVersion());
        attributes.putValue(Constants.EXPORT_PACKAGE, getExports());
        attributes.putValue(Constants.IMPORT_PACKAGE, getImports());

        return manifest;
    }

    private String createName() {
        return String.format("%s%s", bundleHeaders.getName(), BUNDLE_NAME_SUFFIX);
    }

    private String createExportPackage(Set<String> packages) {
        StringBuilder builder = new StringBuilder();
        String prefix = "";

        for (String pack : packages) {
            builder.append(prefix);
            builder.append(pack);
            builder.append(";");
            builder.append(Constants.VERSION_ATTRIBUTE);
            builder.append("=");
            builder.append(bundleHeaders.getVersion());

            if (isBlank(prefix)) {
                prefix = ",";
            }
        }

        return builder.toString();
    }

    private String loadResourceAsString(String resource) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resource)) {
            return IOUtils.toString(in);
        }
    }

    private void writeResourceToStream(String resource, OutputStream output) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resource)) {
            IOUtils.copy(in, output);
        }
    }

    private String getExports() {
        Set<String> exports = new HashSet<>();

        exports.add(PackagesGenerated.ENTITY);
        exports.add(PackagesGenerated.SERVICE);

        for (ClassData enhancedClass : MotechClassPool.getEnhancedClasses(false)) {
            if (enhancedClass.isEnumClassData()) {
                String pkg = ClassName.getPackage(enhancedClass.getClassName());
                exports.add(pkg);
            }
            if (!MotechClassPool.isServiceInterfaceRegistered(enhancedClass.getClassName()) && enhancedClass.isDDE()) {
                String pkg = ClassName.getPackage(enhancedClass.getClassName()).concat(".mdsservice");
                exports.add(pkg);
            }

        }

        return createExportPackage(exports);
    }

    private String getImports() throws IOException {
        // first load the standard imports
        String stdImports = loadResourceAsString(BUNDLE_IMPORTS).replaceAll("\\r|\\n", "");

        // we want to prevent duplicate imports
        StringBuilder sb = new StringBuilder(stdImports);
        Set<String> alreadyImported = new HashSet<>();

        Collections.addAll(alreadyImported, split(stdImports, ","));

        // add imports for DDE classes
        for (ClassData classData : MotechClassPool.getEnhancedClasses(true)) {
            if (classData.isDDE()) {
                String pkg = ClassName.getPackage(classData.getClassName());
                if (!alreadyImported.contains(pkg)) {
                    sb.append(',').append(pkg);
                    alreadyImported.add(pkg);
                }
            }
        }

        for (String enumName : MotechClassPool.registeredEnums()) {
            String pkg = ClassName.getPackage(enumName);

            if (!alreadyImported.contains(pkg)) {
                sb.append(',').append(pkg);
                alreadyImported.add(pkg);
            }
        }

        return sb.toString();
    }

    private List<FieldInfo> getFieldsInfo(EntityDto entity, List<FieldDto> fields,
                                          AdvancedSettingsDto advancedSettings) {
        List<FieldInfo> fieldsInfo = new ArrayList<>();

        Collections.sort(fields, new UIDisplayFieldComparator(advancedSettings.getBrowsing().getDisplayedFields()));

        for (FieldDto field : fields) {
            FieldInfo fieldInfo = new FieldInfo();

            fieldInfo.setField(field);

            boolean exposedByRest = advancedSettings.isFieldExposedByRest(field.getBasic().getName());
            fieldInfo.setRestExposed(exposedByRest);

            FieldInfo.TypeInfo typeInfo = fieldInfo.getTypeInfo();
            typeInfo.setType(field.getType().getTypeClass());
            typeInfo.setTaskType(ActionParameterTypeResolver.resolveType(entity, field));

            // combobox values
            typeInfo.setCombobox(field.getType().isCombobox());
            if (field.getType().isCombobox()) {
                ComboboxHolder cbHolder = new ComboboxHolder(entity, field);
                String[] items = cbHolder.getValues();
                if (ArrayUtils.isNotEmpty(items)) {
                    typeInfo.setItems(Arrays.asList(items));
                }

                typeInfo.setAllowsMultipleSelection(cbHolder.isCollection());
                typeInfo.setAllowUserSupplied(cbHolder.isAllowUserSupplied());
            }

            fieldsInfo.add(fieldInfo);
        }
        return fieldsInfo;
    }

    private void refreshModule(String moduleName) {
        LOGGER.info("Refreshing module '{}' before restarting the entities bundle", moduleName);
        Bundle bundleToRefresh = WebBundleUtil.findBundleBySymbolicName(bundleContext, moduleName);
        if (bundleToRefresh != null) {
            Bundle frameworkBundle = bundleContext.getBundle(0);
            FrameworkWiring frameworkWiring = frameworkBundle.adapt(FrameworkWiring.class);
            moduleRefreshed = false;
            FrameworkListener frameworkListener = new FrameworkListener() {
                @Override
                public void frameworkEvent(FrameworkEvent frameworkEvent) {
                    synchronized (lock) {
                        moduleRefreshed = frameworkEvent.getType() == FrameworkEvent.PACKAGES_REFRESHED;
                    }
                }
            };

            frameworkWiring.refreshBundles(Arrays.asList(bundleToRefresh), frameworkListener);
            waitForPackagesRefreshed();
        } else {
            LOGGER.warn("Module '{}' not present, skipping refresh, but this can indicate of an error",
                    moduleName);
        }
    }

    private void cleanEntitiesBundleCachedClasses() {
        Bundle entitiesBundles = MdsBundleHelper.findMdsEntitiesBundle(bundleContext);
        if (entitiesBundles != null) {
            MdsBundleHelper.unregisterBundleJDOClasses(entitiesBundles);
        }
    }

    private void waitForPackagesRefreshed() {
        int count = 0;
        synchronized (lock) {
            while (!moduleRefreshed && count < MAX_WAIT_COUNT) {
                try {
                    lock.wait(WAIT_TIME);
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted while waiting", e);
                }
                ++count;
            }
            if (count >= MAX_WAIT_COUNT) {
                LOGGER.error("Packages have not been refreshed, reached time limit");
            }
        }
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.bundleHeaders = new BundleHeaders(bundleContext);
    }

    @Autowired
    public void setMetadataHolder(MetadataHolder metadataHolder) {
        this.metadataHolder = metadataHolder;
    }

    @Autowired
    public void setMdsConstructor(MDSConstructor mdsConstructor) {
        this.mdsConstructor = mdsConstructor;
    }

    @Resource(name = "mdsVelocityEngine")
    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    @Autowired
    public void setMdsDataProvider(MDSDataProvider mdsDataProvider) {
        this.mdsDataProvider = mdsDataProvider;
    }

    @Autowired
    public void setMonitor(EntitiesBundleMonitor monitor) {
        this.monitor = monitor;
    }

    @Autowired
    public void setListenerRegistryService(JdoListenerRegistryService jdoListenerRegistryService) {
        this.jdoListenerRegistryService = jdoListenerRegistryService;
    }

    @Autowired
    @Qualifier("mdsOsgiBundleApplicationContextListener")
    public void setMdsOsgiBundleApplicationContextListener(MdsOsgiBundleApplicationContextListener mdsOsgiBundleApplicationContextListener) {
        this.mdsOsgiBundleApplicationContextListener = mdsOsgiBundleApplicationContextListener;
    }
}
