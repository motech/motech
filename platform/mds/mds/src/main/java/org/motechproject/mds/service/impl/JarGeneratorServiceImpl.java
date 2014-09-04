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
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityInfo;
import org.motechproject.mds.ex.MdsException;
import org.motechproject.mds.javassist.JavassistHelper;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.osgi.EntitiesBundleMonitor;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MetadataHolder;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.osgi.web.util.BundleHeaders;
import org.motechproject.osgi.web.util.WebBundleUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
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

    private BundleHeaders bundleHeaders;
    private MetadataHolder metadataHolder;
    private MDSConstructor mdsConstructor;
    private VelocityEngine velocityEngine;
    private MDSDataProvider mdsDataProvider;
    private EntitiesBundleMonitor monitor;
    private BundleContext bundleContext;
    private AllEntities allEntities;

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

        boolean constructed = mdsConstructor.constructEntities(buildDDE);

        if (!constructed) {
            return;
        }

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
        FileOutputStream fileOutput = new FileOutputStream(tempFile.toFile());

        StringBuilder entityNamesSb = new StringBuilder();

        try (JarOutputStream output = new JarOutputStream(fileOutput, manifest)) {
            List<EntityInfo> information = new ArrayList<>();

            for (ClassData classData : MotechClassPool.getEnhancedClasses(false)) {
                String className = classData.getClassName();

                // we keep the name to construct a file containing all entity names
                // the file is required for schema generation
                entityNamesSb.append(className).append('\n');

                EntityInfo info = new EntityInfo();
                info.setClassName(className);

                // insert entity class, only for EUDE, note that this can also be a generated enum class
                if (!classData.isDDE()) {
                    addEntry(output, JavassistHelper.toClassPath(className), classData.getBytecode());
                }

                // insert history and trash classes, these classes will not be present for enums
                ClassData historyClassData = MotechClassPool.getHistoryClassData(className);
                if (historyClassData != null) {
                    addEntry(output, JavassistHelper.toClassPath(historyClassData.getClassName()),
                            historyClassData.getBytecode());
                }

                ClassData trashClassData = MotechClassPool.getTrashClassData(className);
                if (trashClassData != null) {
                    addEntry(output, JavassistHelper.toClassPath(trashClassData.getClassName()),
                            trashClassData.getBytecode());
                }

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

                info.setModule(classData.getModule());
                info.setNamespace(classData.getNamespace());

                Entity entity = allEntities.retrieveByClassName(classData.getClassName());
                info.setSupportsRest(entity.supportsAnyRestOperations());
                info.setEntityName(entity.getName());

                information.add(info);
            }

            String blueprint = mergeTemplate(information, BLUEPRINT_TEMPLATE);
            String context = mergeTemplate(information, MDS_ENTITIES_CONTEXT_TEMPLATE);
            String entityNames = entityNamesSb.toString();

            addEntry(output, PACKAGE_JDO, metadataHolder.getJdoMetadata().toString().getBytes());
            addEntry(output, BLUEPRINT_XML, blueprint.getBytes());
            addEntry(output, MDS_ENTITIES_CONTEXT, context.getBytes());
            addEntry(output, ENTITY_LIST_FILE, entityNames.getBytes());
            addEntry(output, MDS_COMMON_CONTEXT);
            addEntry(output, DATANUCLEUS_PROPERTIES);
            addEntry(output, MOTECH_MDS_PROPERTIES);

            return tempFile.toFile();
        }
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

    private void refreshModule(String moduleName) {
        LOGGER.info("Refreshing module '{}' before restarting the entities bundle", moduleName);

        Bundle bundleToRefresh = WebBundleUtil.findBundleByName(bundleContext, moduleName);

        if (bundleToRefresh != null) {
            Bundle frameworkBundle = bundleContext.getBundle(0);
            FrameworkWiring frameworkWiring = frameworkBundle.adapt(FrameworkWiring.class);

            frameworkWiring.refreshBundles(Arrays.asList(bundleToRefresh));
        } else {
            LOGGER.warn("Module '{}' not present, skipping refresh, but this can indicate of an error",
                    moduleName);
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
}
