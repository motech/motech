package org.motechproject.testing.osgi;

import org.eclipse.gemini.blueprint.test.AbstractConfigurableBundleCreatorTests;
import org.eclipse.gemini.blueprint.test.platform.Platforms;
import org.junit.Assert;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.springframework.core.JdkVersion;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseOsgiIT extends AbstractConfigurableBundleCreatorTests {
    private static Logger logger = Logger.getLogger(BaseOsgiIT.class.getName());

    private static final String SPRING_GROUP_ID = "org.springframework";
    private static final String SPRING_OSGI_GROUP_ID = SPRING_GROUP_ID + ".osgi";
    private static final String SPRING_CONTEXT_ID = SPRING_GROUP_ID + ".context";
    private static final String SPRING_OSGI_CORE_ID = SPRING_OSGI_GROUP_ID + ".core";
    private static final String ECLIPSE_OSGI = "org.eclipse.osgi";
    private static final String BACKPORT_GROUP_ID = "edu.emory.mathcs.backport";
    private static final String SPRING_OSGI_VERSION_PROP_KEY = "spring.osgi.version";
    private static final String SPRING_VERSION_PROP_KEY = "spring.version";

    private int connectTimeout = 30;

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void testOsgiPlatformStarts() throws Exception {
        logger.info(bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
        logger.info(bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
        logger.info(bundleContext.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
        Assert.assertNotNull(bundleContext.getBundles());
    }

    protected Object verifyServiceAvailable(String clazz) {
        ServiceReference serviceReference = bundleContext.getServiceReference(clazz);
        Assert.assertNotNull(serviceReference);
        Object service = bundleContext.getService(serviceReference);
        Assert.assertNotNull(service);
        return service;
    }


    @Override
    protected String getPlatformName() {
        return Platforms.FELIX;
    }

    @Override
    protected Resource getTestingFrameworkBundlesConfiguration() {
        final Resource testingFrameworkBundlesConfiguration = super.getTestingFrameworkBundlesConfiguration();
        logger.info(testingFrameworkBundlesConfiguration.toString());
        return testingFrameworkBundlesConfiguration;
    }


    @Override
    protected String[] getTestBundlesNames() {
        List<MavenArtifact> artifacts = getMavenArtifacts();

        logger.finest("Maven artifacts " + artifacts);

        String springBundledVersion = null;
        String springOsgiVersion = null;
        Iterator<MavenArtifact> iter = artifacts.iterator();

        while (iter.hasNext()) {
            MavenArtifact artifact = iter.next();
            if (artifact.getGroupId().equals(SPRING_GROUP_ID) && artifact.getArtifactId().equals(SPRING_CONTEXT_ID)) {
                springBundledVersion = artifact.getVersion();

            } else if (artifact.getGroupId().equals(SPRING_OSGI_GROUP_ID)) {
                if (artifact.getArtifactId().equals(SPRING_OSGI_CORE_ID)) {
                    springOsgiVersion = artifact.getVersion();
                }
            } else if (artifact.getGroupId().equals(ECLIPSE_OSGI)
                    && artifact.getArtifactId().equals(ECLIPSE_OSGI)) {
                // filter out this since it is started by the framework specification in
                // the POM.
                iter.remove();
            } else if (artifact.getGroupId().equals(BACKPORT_GROUP_ID)
                    && JdkVersion.isAtLeastJava15()) {
                // Filter out backport if Java is 1.5 or higher.
                iter.remove();
            }
        }

        String[] bundles = bundlesFromArtifacts(artifacts);

        // pass properties to test instance running inside OSGi space
        setSystemProperties(springOsgiVersion, springBundledVersion);

        bundles = MotechBundleSorter.sort(bundles);

        bundles = removeTestFrameworkBundles(bundles);
        logger.info("Test bundles :" + ObjectUtils.nullSafeToString(bundles));

        return bundles;
    }

    @Override
    protected Resource[] getTestBundles() {
        final List<Resource> dependentBundles = new ArrayList(Arrays.asList(super.getTestBundles()));
        final String artifactId = getArtifactId(getPomPath());
        final String path = getModulePath() + "/target/";
        final String[] list = new File(path).list(new ArtifactJarFilter(artifactId));
        for (String fileName : list) {
            final FileSystemResource fileSystemResource = new FileSystemResource(new File(path, fileName));
            dependentBundles.add(fileSystemResource);
        }
        return dependentBundles.toArray(new Resource[0]);
    }

    @Override
    protected String[] getTestFrameworkBundlesNames() {
        String[] bundles = {
                "org.springframework,org.springframework.asm,3.1.0.RELEASE",
                "org.springframework,org.springframework.beans,3.1.0.RELEASE",
                "org.springframework,org.springframework.core,3.1.0.RELEASE",
                "org.springframework,org.springframework.context,3.1.0.RELEASE",
                "org.springframework,org.springframework.expression,3.1.0.RELEASE",
                "org.springframework,org.springframework.aop,3.1.0.RELEASE",
                "org.springframework,org.springframework.test,3.1.0.RELEASE"
        };
        logger.info("Framework Bundles: " + Arrays.asList(bundles).toString());
        return bundles;
    }

    @Override
    protected Manifest getManifest() {
        Manifest mf = super.getManifest();
        List<String> importPackages = getImports();
        if (importPackages != null && importPackages.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (String importPackage : importPackages) {
                builder.append(importPackage).append(",");
            }
            String originalImports = mf.getMainAttributes().getValue(Constants.IMPORT_PACKAGE);
            mf.getMainAttributes().putValue(Constants.IMPORT_PACKAGE, builder.append(originalImports).toString());
        }
        return mf;
    }

    protected List<String> getImports() {
        return null;
    }

    private String[] removeTestFrameworkBundles(String[] bundles) {
        ArrayList<String> filteredBundles = new ArrayList<String>();
        List<String> testFrameworkBundles = Arrays.asList(getTestFrameworkBundlesNames());
        for (String bundle : bundles) {
            if (!testFrameworkBundles.contains(bundle)) {
                filteredBundles.add(bundle);
            }
        }
        return filteredBundles.toArray(new String[0]);
    }

    public String getDefaultDependenciesListFilename() {
        return getModulePath() + "/target/dependencies.list";
    }

    protected String getPomPath() {
        return getModulePath() + "/pom.xml";
    }

    private String getModulePath() {
        return getPomPath(URLDecoder.decode(this.getClass().getResource(".").getPath()));
    }

    private String getArtifactId(String pomFilePath) {
        try {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setIgnoringComments(true);
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document pomDoc = documentBuilder.parse(pomFilePath);
            return getPomInfo(pomDoc, "artifactId");
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to get artifact ID from pom.xml " + pomFilePath, e);
        }
    }

    private String getPomInfo(Document pomDoc, String tagName) {
        final Element root = pomDoc.getDocumentElement();
        final Node node = getChild(root, tagName);
        return getNodeContent(node, Node.TEXT_NODE);
    }

    private String getNodeContent(Node node, short nodeType) {
        final NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            final Node item = nodes.item(i);
            if (item.getNodeType() == nodeType) {
                return item.getNodeValue().trim();
            }
        }
        return null;
    }

    private Node getChild(Element root, String tagName) {
        final NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            final Node item = nodes.item(i);
            if (tagName.equals(item.getNodeName())) {
                return item;
            }
        }
        return null;
    }

    private String getPomPath(String path) {
        if (path == null) {
            return null;
        }
        if (new File(path + "/pom.xml").exists()) {
            return path;
        }
        return getPomPath(new File(path).getParent());
    }

    private List<MavenArtifact> getMavenArtifacts() {
        try {
            return MavenDependencyListParser.parseDependencies(new FileSystemResource(getDefaultDependenciesListFilename()));
        } catch (IOException e) {
            String error = "Error loading the dependency list resource: " + getTestingFrameworkBundlesConfiguration();
            logger.log(Level.SEVERE, error, e);
            throw new IllegalArgumentException(error, e);
        }
    }

    private String[] bundlesFromArtifacts(List<MavenArtifact> artifacts) {
        String[] bundles = new String[artifacts.size()];
        Iterator<MavenArtifact> iter = artifacts.iterator();
        for (int i = 0; iter.hasNext(); ++i) {
            MavenArtifact artifact = iter.next();

            bundles[i] = artifact.getGroupId() + "," + artifact.getArtifactId() + ","
                    + artifact.getVersion();
        }
        return bundles;
    }

    private void setSystemProperties(String springOsgiVersion, String springBundledVersion) {
        if (springOsgiVersion != null) {
            System.getProperties().put(SPRING_OSGI_VERSION_PROP_KEY, springOsgiVersion);
        }
        if (springBundledVersion != null) {
            System.getProperties().put(SPRING_VERSION_PROP_KEY, springBundledVersion);
        }
    }
}
