package org.motechproject.mds.service.impl.internal;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.motechproject.mds.builder.ClassData;
import org.motechproject.mds.ex.MdsException;
import org.motechproject.mds.javassist.JavassistHelper;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.MetadataHolder;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.osgi.web.util.BundleHeaders;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static java.util.jar.Attributes.Name;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.mds.util.ClassName.getInterfaceName;
import static org.motechproject.mds.util.ClassName.getRepositoryName;
import static org.motechproject.mds.util.ClassName.getServiceName;
import static org.motechproject.mds.util.Constants.Manifest;

/**
 * Default implementation of {@link org.motechproject.mds.service.JarGeneratorService} interface.
 */
@Service
public class JarGeneratorServiceImpl extends BaseMdsService implements JarGeneratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JarGeneratorServiceImpl.class);

    private BundleHeaders bundleHeaders;
    private BundleContext bundleContext;
    private MetadataHolder metadataHolder;

    @Override
    @Transactional
    public void regenerateMdsDataBundle() {
        File tmpBundleFile;

        try {
            tmpBundleFile = generate();
        } catch (IOException | NotFoundException | CannotCompileException e) {
            throw new MdsException("Unable to generate entities bundle", e);
        }

        Bundle dataBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, createSymbolicName());

        try (InputStream in = new FileInputStream(tmpBundleFile)) {
            if (dataBundle == null) {
                dataBundle = bundleContext.installBundle(bundleLocation(), in);
            } else {
                dataBundle.update(in);
            }

            File dest = new File(bundleLocation());
            FileUtils.deleteQuietly(dest);
            FileUtils.moveFile(tmpBundleFile, dest);

            dataBundle.start();
        } catch (IOException e) {
            throw new MdsException("Unable to read temporary entities bundle", e);
        } catch (BundleException e) {
            throw new MdsException("Unable to start the entities bundle", e);
        }
    }

    @Override
    @Transactional
    public File generate() throws IOException, NotFoundException, CannotCompileException {
        Path tempDir = Files.createTempDirectory("mds");
        Path tempFile = Files.createTempFile(tempDir, "mds-entities", ".jar");

        java.util.jar.Manifest manifest = createManifest();
        FileOutputStream fileOutput = new FileOutputStream(tempFile.toFile());

        try (JarOutputStream output = new JarOutputStream(fileOutput, manifest)) {
            List<String> classNames = new ArrayList<>();

            for (ClassData classData : MotechClassPool.getEnhancedClasses()) {
                String className = classData.getClassName();
                classNames.add(className);

                // insert entity class
                JarEntry entityEntry = new JarEntry(JavassistHelper.toClassPath(className));
                output.putNextEntry(entityEntry);
                output.write(classData.getBytecode());

                // insert infrastructure
                String[] classes = new String[]{
                        getInterfaceName(className),
                        getServiceName(className), getRepositoryName(className)
                };

                for (String c : classes) {
                    CtClass clazz = MotechClassPool.getDefault().get(c);

                    JarEntry entry = new JarEntry(JavassistHelper.toClassPath(c));
                    output.putNextEntry(entry);
                    output.write(clazz.toBytecode());
                    output.closeEntry();
                }
            }

            JarEntry jdoEntry = new JarEntry(PACKAGE_JDO);
            output.putNextEntry(jdoEntry);
            output.write(metadataHolder.getJdoMetadata().toString().getBytes());
            output.closeEntry();

            String blueprint = mergeTemplate(classNames, BLUEPRINT_TEMPLATE);
            String context = mergeTemplate(classNames, MDS_ENTITIES_CONTEXT_TEMPLATE);

            JarEntry blueprintEntry = new JarEntry(BLUEPRINT_XML);
            output.putNextEntry(blueprintEntry);
            output.write(blueprint.getBytes());
            output.closeEntry();

            JarEntry contextEntry = new JarEntry(MDS_ENTITIES_CONTEXT);
            output.putNextEntry(contextEntry);
            output.write(context.getBytes());
            output.closeEntry();

            JarEntry commonContextEntry = new JarEntry(MDS_COMMON_CONTEXT);
            output.putNextEntry(commonContextEntry);
            writeResourceToStream(MDS_COMMON_CONTEXT, output);

            JarEntry dnProperties = new JarEntry(DATANUCLEUS_PROPERTIES);
            output.putNextEntry(dnProperties);
            writeResourceToStream(DATANUCLEUS_PROPERTIES, output);

            JarEntry mdsProperties = new JarEntry(MOTECH_MDS_PROPERTIES);
            output.putNextEntry(mdsProperties);
            writeResourceToStream(MOTECH_MDS_PROPERTIES, output);

            output.closeEntry();

            return tempFile.toFile();
        }
    }

    private String mergeTemplate(List<String> classNames, String templatePath) {
        List<Map<String, String>> allClassesList = new ArrayList<>();

        for (String className : classNames) {
            Map<String, String> map = new HashMap<>();
            map.put("name", className.substring(className.lastIndexOf(".") + 1));
            map.put("className", className);
            map.put("interface", getInterfaceName(className));
            map.put("service", getServiceName(className));
            map.put("repository", getRepositoryName(className));
            allClassesList.add(map);
        }

        VelocityEngine velocityEngine = new VelocityEngine();
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("list", allClassesList);
        Template template;
        StringWriter writer = new StringWriter();

        try {
            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

            template = velocityEngine.getTemplate(templatePath);
            template.merge(velocityContext, writer);
        } catch (Exception e) {
            LOGGER.error("An exception occured, while trying to load" + templatePath + " template and merge it with data", e);
        }

        return writer.toString();
    }

    private java.util.jar.Manifest createManifest() throws IOException {
        java.util.jar.Manifest manifest = new java.util.jar.Manifest();
        Attributes attributes = manifest.getMainAttributes();

        String exports = createExportPackage(org.motechproject.mds.util.Constants.PackagesGenerated.ENTITY, org.motechproject.mds.util.Constants.PackagesGenerated.SERVICE);

        // standard attributes
        attributes.put(Name.MANIFEST_VERSION, Manifest.MANIFEST_VERSION);

        // osgi attributes
        attributes.putValue(Constants.BUNDLE_MANIFESTVERSION, Manifest.BUNDLE_MANIFESTVERSION);
        attributes.putValue(Constants.BUNDLE_NAME, createName());
        attributes.putValue(Constants.BUNDLE_SYMBOLICNAME, createSymbolicName());
        attributes.putValue(Constants.BUNDLE_VERSION, bundleHeaders.getVersion());
        attributes.putValue(Constants.EXPORT_PACKAGE, exports);
        // load imports from a handy file and remove line breaks
        attributes.putValue(Constants.IMPORT_PACKAGE, loadResourceAsString(BUNDLE_IMPORTS).replaceAll("\\r|\\n", ""));
        return manifest;
    }

    private String createName() {
        return String.format("%s%s", bundleHeaders.getName(), Manifest.BUNDLE_NAME_SUFFIX);
    }

    private String createSymbolicName() {
        return String.format("%s%s", bundleHeaders.getSymbolicName(), Manifest.SYMBOLIC_NAME_SUFFIX);
    }

    private String bundleLocation() {
        Path path = FileSystems.getDefault().getPath(System.getProperty("user.home"), ".motech/bundles", "mds-entities.jar");
        return path.toAbsolutePath().toString();
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

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.bundleHeaders = new BundleHeaders(bundleContext);
    }

    @Autowired
    public void setMetadataHolder(MetadataHolder metadataHolder) {
        this.metadataHolder = metadataHolder;
    }
}
