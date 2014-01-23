package org.motechproject.mds.service.impl.internal;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.AllEntityMappings;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.osgi.web.BundleHeaders;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static java.util.jar.Attributes.Name;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.mds.constants.Constants.Manifest;
import static org.motechproject.mds.constants.Constants.Packages;
import static org.motechproject.mds.util.ClassName.getInterfaceName;

/**
 * Default implementation of {@link org.motechproject.mds.service.JarGeneratorService} interface.
 */
@Service
public class JarGeneratorServiceImpl extends BaseMdsService implements JarGeneratorService {
    private AllEntityMappings entityMappings;
    private BundleHeaders bundleHeaders;

    @Override
    @Transactional
    public File generate() throws IOException, NotFoundException, CannotCompileException {
        Path tempDir = Files.createTempDirectory("mds");
        Path tempFile = Files.createTempFile(tempDir, "mds-entities", ".jar");

        java.util.jar.Manifest manifest = createManifest();
        FileOutputStream fileOutput = new FileOutputStream(tempFile.toFile());
        JarOutputStream output = new JarOutputStream(fileOutput, manifest);

        List<EntityMapping> mappings = entityMappings.getAllEntities();
        for (EntityMapping mapping : mappings) {
            String[] classes = new String[]{
                    mapping.getClassName(), getInterfaceName(mapping.getClassName())
            };

            for (String c : classes) {
                CtClass clazz = MotechClassPool.getDefault().get(c);

                JarEntry entry = new JarEntry(createClassPath(c));
                output.putNextEntry(entry);
                output.write(clazz.toBytecode());
                output.closeEntry();
            }
        }

        output.close();

        return tempFile.toFile();
    }

    private java.util.jar.Manifest createManifest() {
        java.util.jar.Manifest manifest = new java.util.jar.Manifest();
        Attributes attributes = manifest.getMainAttributes();

        String exports = createExportPackage(Packages.ENTITY, Packages.SERVICE);

        // standard attributes
        attributes.put(Name.MANIFEST_VERSION, Manifest.MANIFEST_VERSION);

        // osgi attributes
        attributes.putValue(Constants.BUNDLE_MANIFESTVERSION, Manifest.BUNDLE_MANIFESTVERSION);
        attributes.putValue(Constants.BUNDLE_NAME, createName());
        attributes.putValue(Constants.BUNDLE_SYMBOLICNAME, createSymbolicName());
        attributes.putValue(Constants.BUNDLE_VERSION, bundleHeaders.getVersion());
        attributes.putValue(Constants.EXPORT_PACKAGE, exports);
        attributes.putValue(Constants.IMPORT_PACKAGE, "*");

        return manifest;
    }

    private String createName() {
        return String.format("%s%s", bundleHeaders.getName(), Manifest.BUNDLE_NAME_SUFFIX);
    }

    private String createSymbolicName() {
        return String.format("%s%s", bundleHeaders.getSymbolicName(), Manifest.SYMBOLIC_NAME_SUFFIX);
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

    private String createClassPath(String className) {
        return className.replace('.', '/') + ".class";
    }

    @Autowired
    public void setEntityMappings(AllEntityMappings entityMappings) {
        this.entityMappings = entityMappings;
    }

    @Autowired
    public void setBundleHeaders(BundleContext bundleContext) {
        this.bundleHeaders = new BundleHeaders(bundleContext);
    }
}
