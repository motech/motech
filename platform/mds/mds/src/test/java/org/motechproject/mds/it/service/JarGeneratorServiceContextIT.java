package org.motechproject.mds.it.service;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.builder.MDSConstructor;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.it.BaseIT;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.osgi.EntitiesBundleMonitor;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.osgi.web.util.BundleHeaders;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.junit.Assert.assertEquals;
import static org.motechproject.mds.util.Constants.Manifest;
import static org.motechproject.mds.util.Constants.PackagesGenerated;

public class JarGeneratorServiceContextIT extends BaseIT {
    private static final String SAMPLE = "Sample";
    private static final String EXAMPLE = "Example";
    private static final String FOO = "Foo";
    private static final String BAR = "Bar";

    private static final String SAMPLE_CLASS = String.format("%s.%s", PackagesGenerated.ENTITY, SAMPLE);
    private static final String EXAMPLE_CLASS = String.format("%s.%s", PackagesGenerated.ENTITY, EXAMPLE);
    private static final String FOO_CLASS = String.format("%s.%s", PackagesGenerated.ENTITY, FOO);
    private static final String BAR_CLASS = String.format("%s.%s", PackagesGenerated.ENTITY, BAR);

    @Autowired
    private JarGeneratorService generator;

    @Autowired
    private EntityService entityService;

    @Autowired
    private BundleContext bundleContext;

    @Autowired
    private EntitiesBundleMonitor monitor;

    @Autowired
    private MDSConstructor constructor;

    private BundleHeaders bundleHeaders;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        MotechClassPool.clearEnhancedData();

        EntityDto entitySAMPLE = new EntityDto(null, SAMPLE);
        entitySAMPLE.setRecordHistory(true);
        EntityDto entityEXAMPLE = new EntityDto(null, EXAMPLE);
        entityEXAMPLE.setRecordHistory(true);
        EntityDto entityFOO = new EntityDto(null, FOO);
        entityFOO.setRecordHistory(true);

        //Entity without history
        EntityDto entityBAR = new EntityDto(null, BAR);
        entityBAR.setRecordHistory(false);

        entityService.createEntity(entitySAMPLE);
        entityService.createEntity(entityEXAMPLE);
        entityService.createEntity(entityFOO);
        entityService.createEntity(entityBAR);

        bundleHeaders = new BundleHeaders(bundleContext);

        Path path = Paths.get(monitor.bundleLocation());
        Files.deleteIfExists(path);

        setProperty(monitor, "bundleStarted", true);
        setProperty(monitor, "bundleInstalled", true);
        setProperty(monitor, "contextInitialized", true);

        constructor.constructEntities(true);
    }

    @Test
    public void testGenerate() throws Exception {
        File file = generator.generate();
        FileInputStream stream = new FileInputStream(file);
        JarInputStream input = new JarInputStream(stream);

        assertManifest(input);
        assertJarEntries(input);
    }

    private void assertJarEntries(JarInputStream input) throws IOException {
        List<String> expected = new ArrayList<>();

        expected.add(createClassPath(ClassName.getHistoryClassName(SAMPLE_CLASS)));
        expected.add(createClassPath(ClassName.getHistoryClassName(EXAMPLE_CLASS)));
        expected.add(createClassPath(ClassName.getHistoryClassName(FOO_CLASS)));
        expected.add(createClassPath(ClassName.getTrashClassName(SAMPLE_CLASS)));
        expected.add(createClassPath(ClassName.getTrashClassName(EXAMPLE_CLASS)));
        expected.add(createClassPath(ClassName.getTrashClassName(FOO_CLASS)));
        expected.add(createClassPath(ClassName.getTrashClassName(BAR_CLASS)));
        expected.addAll(createClassPathEntries(SAMPLE_CLASS));
        expected.addAll(createClassPathEntries(EXAMPLE_CLASS));
        expected.addAll(createClassPathEntries(FOO_CLASS));
        expected.addAll(createClassPathEntries(BAR_CLASS));
        expected.addAll(asList(JarGeneratorService.BLUEPRINT_XML, JarGeneratorService.DATANUCLEUS_PROPERTIES,
                JarGeneratorService.MDS_COMMON_CONTEXT, JarGeneratorService.MDS_ENTITIES_CONTEXT,
                JarGeneratorService.MOTECH_MDS_PROPERTIES, JarGeneratorService.PACKAGE_JDO,
                JarGeneratorService.ENTITY_LIST_FILE,  JarGeneratorService.HISTORY_LIST_FILE,
                JarGeneratorService.VALIDATION_PROVIDER, JarGeneratorService.TASK_CHANNEL_JSON));

        JarEntry entry = input.getNextJarEntry();
        List<String> actual = new ArrayList<>(8);

        while (entry != null) {
            actual.add(entry.getName());
            entry = input.getNextJarEntry();
        }

        Collections.sort(expected);
        Collections.sort(actual);

        assertEquals(expected, actual);
    }

    private void assertManifest(JarInputStream input) throws IOException {
        java.util.jar.Manifest manifest = input.getManifest();
        Attributes attributes = manifest.getMainAttributes();

        String exports = createExportPackage(PackagesGenerated.ENTITY, PackagesGenerated.SERVICE);

        // standard attributes
        assertEquals(Manifest.MANIFEST_VERSION, attributes.getValue(Attributes.Name.MANIFEST_VERSION));

        // osgi attributes
        assertEquals(Manifest.BUNDLE_MANIFESTVERSION, attributes.getValue(Constants.BUNDLE_MANIFESTVERSION));
        assertEquals(createName(), attributes.getValue(Constants.BUNDLE_NAME));
        assertEquals(createSymbolicName(), attributes.getValue(Constants.BUNDLE_SYMBOLICNAME));
        assertEquals(bundleHeaders.getVersion(), attributes.getValue(Constants.BUNDLE_VERSION));
        assertEquals(exports, attributes.getValue(Constants.EXPORT_PACKAGE));
        assertEquals(loadImports(), attributes.getValue(Constants.IMPORT_PACKAGE));
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

    private List<String> createClassPathEntries(String className) {
        List<String> classes = new ArrayList<>();

        classes.add(createClassPath(className));
        classes.add(createClassPath(ClassName.getInterfaceName(className)));
        classes.add(createClassPath(ClassName.getServiceName(className)));
        classes.add(createClassPath(ClassName.getRepositoryName(className)));

        return classes;
    }

    private String loadImports() throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(JarGeneratorService.BUNDLE_IMPORTS)) {
            // get rid of newlines
            return IOUtils.toString(in).replaceAll("\\r|\\n", "");
        }
    }
}
