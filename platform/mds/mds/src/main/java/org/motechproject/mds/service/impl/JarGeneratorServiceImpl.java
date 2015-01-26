package org.motechproject.mds.service.impl;

import javassist.CannotCompileException;
import javassist.CtClass;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.motechproject.mds.MDSDataProvider;
import org.motechproject.mds.builder.MDSConstructor;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityInfo;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldInfo;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.ex.MdsException;
import org.motechproject.mds.helper.ActionParameterTypeResolver;
import org.motechproject.mds.helper.MdsBundleHelper;
import org.motechproject.mds.javassist.JavassistHelper;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.osgi.EntitiesBundleMonitor;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MetadataHolder;
import org.motechproject.mds.repository.RestDocsRepository;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.osgi.web.util.BundleHeaders;
import org.motechproject.osgi.web.util.WebBundleUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.wiring.FrameworkWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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
    private VelocityEngine velocityEngine;
    private MDSDataProvider mdsDataProvider;
    private EntitiesBundleMonitor monitor;
    private BundleContext bundleContext;
    private AllEntities allEntities;
    private RestDocsRepository restDocsRepository;
    private final Object lock = new Object();
    private boolean moduleRefreshed;

    @Override
    public void regenerateMdsDataBundle() {
        regenerateMdsDataBundle(true, true);
    }

    @Override
    @Transactional
    public synchronized void regenerateMdsDataBundle(boolean buildDDE) {
        regenerateMdsDataBundle(buildDDE, true);
    }

    @Override
    @Transactional
    public void regenerateMdsDataBundleAfterDdeEnhancement(String moduleName) {
        regenerateMdsDataBundle(true, true, moduleName);
    }

    @Override
    @Transactional
    public void regenerateMdsDataBundle(boolean buildDDE, boolean startBundle) {
        regenerateMdsDataBundle(buildDDE, startBundle, null);
    }

    private synchronized void regenerateMdsDataBundle(boolean buildDDE, boolean startBundle, String moduleName) {
        LOGGER.info("Regenerating the mds entities bundle");

        if (StringUtils.isNotBlank(moduleName)) {
            Bundle bundleToRefresh = WebBundleUtil.findBundleByName(bundleContext, moduleName);
            MdsBundleHelper.unregisterBundleJDOClasses(bundleToRefresh);
            ResourceBundle.clearCache();
        }

        boolean constructed = mdsConstructor.constructEntities(buildDDE);

        if (!constructed) {
            return;
        }

        cleanEntitiesBundleCachedClasses();

        LOGGER.info("Updating mds data provider");
        mdsDataProvider.updateDataProvider();

        File dest = new File(monitor.bundleLocation());
        if (dest.exists()) {
            // proceed when the bundles context is ready, we want the context processors to finish
            LOGGER.info("Waiting for entities context");
            monitor.waitForEntitiesContext();
        }

        File tmpBundleFile;

        try {
            LOGGER.info("Generating bundle jar");
            tmpBundleFile = generate();
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

        if (StringUtils.isNotBlank(moduleName)) {
            monitor.stopEntitiesBundle();
            refreshModule(moduleName);
        }

        try {
            monitor.start(dest, startBundle);
        } finally {
            FileUtils.deleteQuietly(tmpBundleFile);
        }
    }

    @Override
    @Transactional
    public File generate() throws IOException {
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
                    addEntry(output, JavassistHelper.toClassPath(className), classData.getBytecode());
                }

                // insert history and trash classes, these classes will not be present for enums
                ClassData historyClassData = MotechClassPool.getHistoryClassData(className);
                if (historyClassData != null) {
                    addEntry(output, JavassistHelper.toClassPath(historyClassData.getClassName()),
                            historyClassData.getBytecode());
                    historyEntitySb.append(className).append('\n');
                }

                ClassData trashClassData = MotechClassPool.getTrashClassData(className);
                if (trashClassData != null) {
                    addEntry(output, JavassistHelper.toClassPath(trashClassData.getClassName()),
                            trashClassData.getBytecode());
                }

                if (!classData.isEnumClassData()) {
                    EntityInfo info = buildEntityInfo(classData);

                    // we keep the name to construct a file containing all entity names
                    // the file is required for schema generation
                    entityNamesSb.append(className).append('\n');

                    // insert repository
                    String repositoryName = MotechClassPool.getRepositoryName(className);
                    if (addClass(output, repositoryName)) {
                        info.setRepository(repositoryName);
                    }

                    // insert service implementation
                    String serviceName = MotechClassPool.getServiceImplName(className);
                    if (addClass(output, serviceName)) {
                        info.setServiceName(serviceName);
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

                    Entity entity = allEntities.retrieveByClassName(classData.getClassName());

                    info.setFieldsInfo(getFieldsInfo(entity));
                    info.setEntityName(entity.getName());
                    setAllowedEvents(info, entity);
                    updateRestOptions(info, entity);

                    information.add(info);
                }
            }

            String blueprint = mergeTemplate(information, BLUEPRINT_TEMPLATE);
            String context = mergeTemplate(information, MDS_ENTITIES_CONTEXT_TEMPLATE);
            String channel = mergeTemplate(information, MDS_CHANNEL_TEMPLATE);
            String entityNames = entityNamesSb.toString();

            addEntries(output, blueprint, context, channel, entityNames, historyEntitySb.toString());

            // regenerate the REST documentation
            restDocsRepository.regenerateDocumentation(information);

            return tempFile.toFile();
        }
    }

    private EntityInfo buildEntityInfo(ClassData classData) {
        EntityInfo info = new EntityInfo();

        info.setClassName(classData.getClassName());
        info.setModule(classData.getModule());
        info.setNamespace(classData.getNamespace());

        return info;
    }

    private void updateRestOptions(EntityInfo info, Entity entity) {
        RestOptions restOptions = entity.getRestOptions();

        if (restOptions != null) {
            info.setRestCreateEnabled(restOptions.isAllowCreate());
            info.setRestReadEnabled(restOptions.isAllowRead());
            info.setRestUpdateEnabled(restOptions.isAllowUpdate());
            info.setRestDeleteEnabled(restOptions.isAllowDelete());
        }
    }

    private void setAllowedEvents(EntityInfo info, Entity entity) {
        info.setCreateEventFired(entity.isAllowCreateEvent());
        info.setUpdateEventFired(entity.isAllowUpdateEvent());
        info.setDeleteEventFired(entity.isAllowDeleteEvent());
    }

    private void addEntries(JarOutputStream output, String blueprint, String context, String channel,
                            String entityNames, String historyEntities) throws IOException  {
        addEntry(output, PACKAGE_JDO, metadataHolder.getJdoMetadata().toString().getBytes());
        addEntry(output, BLUEPRINT_XML, blueprint.getBytes());
        addEntry(output, MDS_ENTITIES_CONTEXT, context.getBytes());
        addEntry(output, TASK_CHANNEL_JSON, channel.getBytes());
        addEntry(output, ENTITY_LIST_FILE, entityNames.getBytes());
        addEntry(output, HISTORY_LIST_FILE, historyEntities.getBytes());
        addEntry(output, MDS_COMMON_CONTEXT);
        addEntry(output, DATANUCLEUS_PROPERTIES);
        addEntry(output, MOTECH_MDS_PROPERTIES);
        addEntry(output, VALIDATION_PROVIDER);
    }

    private boolean addClass(JarOutputStream output, String name) {
        CtClass clazz = MotechClassPool.getDefault().getOrNull(name);
        boolean added = false;

        if (null != clazz) {
            try {
                addEntry(output, JavassistHelper.toClassPath(name), clazz.toBytecode());
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
        model.put("list", information);

        try {
            VelocityEngineUtils.mergeTemplate(velocityEngine, templatePath, model, writer);
        } catch (Exception e) {
            LOGGER.error("An exception occurred, while trying to load" + templatePath + " template and merge it with data", e);
        }

        return writer.toString();
    }

    private java.util.jar.Manifest createManifest() throws IOException {
        java.util.jar.Manifest manifest = new java.util.jar.Manifest();
        Attributes attributes = manifest.getMainAttributes();

        String exports = createExportPackage(
                PackagesGenerated.ENTITY, PackagesGenerated.SERVICE
        );

        // standard attributes
        attributes.put(Name.MANIFEST_VERSION, MANIFEST_VERSION);

        // osgi attributes
        attributes.putValue(Constants.BUNDLE_MANIFESTVERSION, BUNDLE_MANIFESTVERSION);
        attributes.putValue(Constants.BUNDLE_NAME, createName());
        attributes.putValue(Constants.BUNDLE_SYMBOLICNAME, MDS_ENTITIES_SYMBOLIC_NAME);
        attributes.putValue(Constants.BUNDLE_VERSION, bundleHeaders.getVersion());
        attributes.putValue(Constants.EXPORT_PACKAGE, exports);
        attributes.putValue(Constants.IMPORT_PACKAGE, getImports());

        return manifest;
    }

    private String createName() {
        return String.format("%s%s", bundleHeaders.getName(), BUNDLE_NAME_SUFFIX);
    }

    private String createExportPackage(String... packages) {
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

    private List<FieldInfo> getFieldsInfo(Entity entity) {
        List<FieldInfo> fieldsInfo = new ArrayList<>();
        for (Field field : entity.getFields()) {
            if (!field.hasMetadata(org.motechproject.mds.util.Constants.Util.AUTO_GENERATED)) {
                FieldInfo fieldInfo = new FieldInfo(field.getName(), field.getDisplayName(),
                        ActionParameterTypeResolver.resolveType(field), field.isRequired(),
                        field.isExposedViaRest());
                fieldsInfo.add(fieldInfo);

                // we mark comoboxes that allow multiple selections
                // required for REST documentation generation
                if (field.getType().isCombobox()) {
                    ComboboxHolder cbHolder = new ComboboxHolder(field);
                    if (cbHolder.isList()) {
                        fieldInfo.setAdditionalTypeInfo(FieldInfo.TypeInfo.ALLOWS_MULTIPLE_SELECTIONS);
                    }
                }
            }
        }
        return fieldsInfo;
    }

    private void refreshModule(String moduleName) {
        LOGGER.info("Refreshing module '{}' before restarting the entities bundle", moduleName);
        Bundle bundleToRefresh = WebBundleUtil.findBundleByName(bundleContext, moduleName);
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
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }

    @Autowired
    public void setRestDocsRepository(RestDocsRepository restDocsRepository) {
        this.restDocsRepository = restDocsRepository;
    }
}
