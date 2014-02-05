package org.motechproject.mds.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.util.ClassName;
import org.motechproject.osgi.web.BundleHeaders;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.junit.Assert.assertEquals;
import static org.motechproject.mds.constants.Constants.Manifest;
import static org.motechproject.mds.constants.Constants.Packages;

public class JarGeneratorServiceIT extends BaseIT {
    private static final String SAMPLE = "Sample";
    private static final String EXAMPLE = "Example";
    private static final String FOO = "Foo";
    private static final String BAR = "Bar";

    private static final String SAMPLE_CLASS = String.format("%s.%s", Packages.ENTITY, SAMPLE);
    private static final String EXAMPLE_CLASS = String.format("%s.%s", Packages.ENTITY, EXAMPLE);
    private static final String FOO_CLASS = String.format("%s.%s", Packages.ENTITY, FOO);
    private static final String BAR_CLASS = String.format("%s.%s", Packages.ENTITY, BAR);

    @Autowired
    private JarGeneratorService generator;

    @Autowired
    private EntityService entityService;

    @Autowired
    private BundleContext bundleContext;

    private BundleHeaders bundleHeaders;

    @Before
    public void setUp() throws Exception {
        entityService.createEntity(new EntityDto(null, SAMPLE));
        entityService.createEntity(new EntityDto(null, EXAMPLE));
        entityService.createEntity(new EntityDto(null, FOO));
        entityService.createEntity(new EntityDto(null, BAR));

        bundleHeaders = new BundleHeaders(bundleContext);
    }

    @After
    public void tearDown() throws Exception {
        getPersistenceManager().newQuery(EntityMapping.class).deletePersistentAll();
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

        expected.addAll(createClassPathEntries(SAMPLE_CLASS));
        expected.addAll(createClassPathEntries(EXAMPLE_CLASS));
        expected.addAll(createClassPathEntries(FOO_CLASS));
        expected.addAll(createClassPathEntries(BAR_CLASS));

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

    private void assertManifest(JarInputStream input) {
        java.util.jar.Manifest manifest = input.getManifest();
        Attributes attributes = manifest.getMainAttributes();

        String exports = createExportPackage(Packages.ENTITY, Packages.SERVICE);

        // standard attributes
        assertEquals(Manifest.MANIFEST_VERSION, attributes.getValue(Attributes.Name.MANIFEST_VERSION));

        // osgi attributes
        assertEquals(Manifest.BUNDLE_MANIFESTVERSION, attributes.getValue(Constants.BUNDLE_MANIFESTVERSION));
        assertEquals(createName(), attributes.getValue(Constants.BUNDLE_NAME));
        assertEquals(createSymbolicName(), attributes.getValue(Constants.BUNDLE_SYMBOLICNAME));
        assertEquals(bundleHeaders.getVersion(), attributes.getValue(Constants.BUNDLE_VERSION));
        assertEquals(exports, attributes.getValue(Constants.EXPORT_PACKAGE));
        assertEquals("*", attributes.getValue(Constants.IMPORT_PACKAGE));
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
}
