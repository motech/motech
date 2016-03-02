package org.motechproject.admin.internal.service.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.motechproject.admin.bundles.BundleDirectoryManager;
import org.motechproject.admin.bundles.ExtendedBundleInformation;
import org.motechproject.admin.bundles.ImportExportResolver;
import org.motechproject.admin.bundles.MotechBundleFilter;
import org.motechproject.admin.exception.BundleNotFoundException;
import org.motechproject.admin.internal.service.ModuleAdminService;
import org.motechproject.admin.service.impl.MavenRepositorySystemSession;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mds.service.BundleWatcherSuspensionService;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.service.UIFrameworkService;
import org.motechproject.admin.bundles.BundleInformation;
import org.motechproject.admin.bundles.JarInformation;
import org.motechproject.admin.bundles.PomInformation;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.RepositoryException;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.connector.wagon.WagonProvider;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;
import org.sonatype.aether.util.filter.DependencyFilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.config.core.constants.ConfigurationConstants.FILE_CHANGED_EVENT_SUBJECT;
import static org.springframework.util.CollectionUtils.isEmpty;


/**
 * Implementation of the {@link ModuleAdminService} interface for bundle management.
 */
@Service
public class ModuleAdminServiceImpl implements ModuleAdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleAdminServiceImpl.class);

    @Autowired
    private BundleContext bundleContext;

    @Autowired
    private BundleDirectoryManager bundleDirectoryManager;

    @Autowired
    private ImportExportResolver importExportResolver;

    @Autowired
    private MotechBundleFilter motechBundleFilter;

    @Autowired
    private CommonsMultipartResolver commonsMultipartResolver;

    @Autowired
    private UIFrameworkService uiFrameworkService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private BundleWatcherSuspensionService bundleWatcherSuspensionService;

    @Override
    public List<BundleInformation> getBundles() {
        List<BundleInformation> bundles = new ArrayList<>();

        List<Bundle> motechBundles = motechBundleFilter.filter(bundleContext.getBundles());

        for (Bundle bundle : motechBundles) {
            BundleInformation bundleInformation = new BundleInformation(bundle);
            ModuleRegistrationData moduleRegistrationData = uiFrameworkService.getModuleDataByBundle(bundle);
            if (moduleRegistrationData != null) {
                bundleInformation.setSettingsURL(moduleRegistrationData.getSettingsURL());
                bundleInformation.setModuleName(moduleRegistrationData.getModuleName());

                List<String> angularModules = moduleRegistrationData.getAngularModules();
                String angularModuleName = isEmpty(angularModules)
                        ? moduleRegistrationData.getModuleName()
                        : angularModules.get(0);

                bundleInformation.setAngularModule(angularModuleName);
            }
            bundles.add(bundleInformation);
        }

        return bundles;
    }

    @Override
    public BundleInformation getBundleInfo(long bundleId) {
        Bundle bundle = getBundle(bundleId);
        return new BundleInformation(bundle);
    }

    @Override
    public BundleInformation stopBundle(long bundleId) throws BundleException {
        Bundle bundle = getBundle(bundleId);
        bundle.stop();
        return new BundleInformation(bundle);
    }

    @Override
    public BundleInformation startBundle(long bundleId) throws BundleException {
        Bundle bundle = getBundle(bundleId);
        bundle.start();
        return new BundleInformation(bundle);
    }

    @Override
    public BundleInformation restartBundle(long bundleId) throws BundleException {
        Bundle bundle = getBundle(bundleId);
        bundle.stop();
        bundle.start();
        return new BundleInformation(bundle);
    }

    @Override
    public void uninstallBundle(long bundleId, boolean removeConfig) throws BundleException {
        Bundle bundle = getBundle(bundleId);
        bundle.uninstall();
        if (removeConfig) {
            // this is important that config is removed after bundle uninstall!
            configurationService.removeAllBundleProperties(bundle.getSymbolicName());
        }

        try {
            boolean deleted = bundleDirectoryManager.removeBundle(bundle);
            importExportResolver.refreshPackage(bundle);
            if (!deleted) {
                LOGGER.warn("Failed to delete bundle file: " + bundle.getLocation());
            }
        } catch (IOException e) {
            throw new MotechException("Error while removing bundle file", e);
        }
    }

    @Override
    public BundleInformation installBundleFromRepository(String moduleId, boolean startBundle) {
        try {
            return installFromRepository(new Dependency(new DefaultArtifact(moduleId), JavaScopes.RUNTIME), startBundle);
        } catch (RepositoryException | IOException | BundleException e) {
            throw new MotechException("Unable to install module from repository " + moduleId, e);
        }
    }

    @Override
    public BundleInformation installBundle(MultipartFile bundleFile, boolean startBundle) {
        File savedBundleFile = null;
        try {
            savedBundleFile = bundleDirectoryManager.saveBundleFile(bundleFile);
            return installWithDependenciesFromFile(savedBundleFile, startBundle);
        } catch (IOException e) {
            if (savedBundleFile != null) {
                LOGGER.error("Removing bundle due to exception", e);
                FileUtils.deleteQuietly(savedBundleFile);
            }
            throw new MotechException("Cannot install file", e);
        }
    }

    @Override
    public ExtendedBundleInformation getBundleDetails(long bundleId) {
        Bundle bundle = getBundle(bundleId);

        ExtendedBundleInformation bundleInfo = new ExtendedBundleInformation(bundle);
        importExportResolver.resolveBundleWiring(bundleInfo);

        return bundleInfo;
    }

    private List<ArtifactResult> resolveDependencies(Dependency dependency, PomInformation pomInformation) throws RepositoryException {
        LOGGER.info("Resolving dependencies for {}", dependency);

        org.apache.maven.repository.internal.DefaultServiceLocator locator = new org.apache.maven.repository.internal.DefaultServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
        locator.setServices(WagonProvider.class, new HttpWagonProvider());

        RepositorySystem system = locator.getService(RepositorySystem.class);

        MavenRepositorySystemSession mvnRepository = new MavenRepositorySystemSession();

        mvnRepository.setLocalRepositoryManager(system.newLocalRepositoryManager(new LocalRepository(System.getProperty("java.io.tmpdir") + "/repo")));
        RemoteRepository remoteRepository = new RemoteRepository("central", "default", "http://nexus.motechproject.org/content/repositories/public");

        CollectRequest collectRequest = new CollectRequest();

        if (pomInformation != null) {
            String version = parseDependencyVersion(dependency, mvnRepository, system, remoteRepository, pomInformation);
            String groupId = parseDependencyGroupId(dependency, mvnRepository, system, remoteRepository, pomInformation);

            Artifact artifact = dependency.getArtifact();
            Artifact updatedArtifact = new DefaultArtifact(groupId, artifact.getArtifactId(), artifact.getClassifier(),
                    artifact.getExtension(), version);

            // The method setArtifact instead of updating dependency object, creates new dependency object with the given artifact
            dependency = dependency.setArtifact(updatedArtifact); //NO CHECKSTYLE ParameterAssignmentCheck

            if (pomInformation.getRepositories() != null) {
                for (RemoteRepository repository : pomInformation.getRepositories()) {
                    collectRequest.addRepository(repository);
                }
            }
        }

        collectRequest.setRoot(dependency);
        collectRequest.addRepository(remoteRepository);

        DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, DependencyFilterUtils.classpathFilter(JavaScopes.RUNTIME));
        try {
            LOGGER.debug("Sending dependency request");
            return system.resolveDependencies(mvnRepository, dependencyRequest).getArtifactResults();
        } catch (RuntimeException e) {
            LOGGER.error("Unable to resolve dependencies for {}", dependency.toString(), e);
            return Collections.emptyList();
        }
    }

    private String parseDependencyVersion(Dependency dependency, MavenRepositorySystemSession mvnRepository, RepositorySystem system,
                                          RemoteRepository remoteRepository, PomInformation pomInformation) throws ArtifactResolutionException {
        String parsedVersion;
        String version = dependency.getArtifact().getVersion();

        if (StringUtils.isEmpty(version)) {
            parsedVersion = "[0,)";
        } else {
            Set<String> properties = getPropertiesFromString(version);

            for (String propertyName : properties) {
                String parsedProperty = getPropertyFromPom(parsePropertyName(propertyName), mvnRepository, system,
                        remoteRepository, pomInformation);

                if (parsedProperty == null) {
                    LOGGER.error("The property: {} used in dependency: {} cannot be found in pom " +
                                    "and its parents. For this dependency the latest version will be used",
                            propertyName, dependency.getArtifact().getArtifactId());
                    version = "[0,)";
                    break;
                }

                version = StringUtils.replace(version, propertyName, parsedProperty);
            }

            parsedVersion = version;
        }

        return parsedVersion;
    }

    private String parseDependencyGroupId(Dependency dependency, MavenRepositorySystemSession mvnRepository, RepositorySystem system,
                                          RemoteRepository remoteRepository, PomInformation pomInformation) throws ArtifactResolutionException {
        String parsedGroupId;
        String groupId = dependency.getArtifact().getGroupId();

        Set<String> properties = getPropertiesFromString(groupId);

        for (String propertyName : properties) {
            String parsedProperty = getPropertyFromPom(parsePropertyName(propertyName), mvnRepository, system,
                    remoteRepository, pomInformation);

            if (parsedProperty == null) {
                throw new MotechException(String.format("The property: %s used for groupId in dependency: %s cannot be found in pom and its parents.",
                        propertyName, dependency.getArtifact().getArtifactId()));
            }

            groupId = StringUtils.replace(groupId, propertyName, parsedProperty);
        }

        parsedGroupId = groupId;

        return parsedGroupId;
    }

    private Set<String> getPropertiesFromString(String input) {
        Set<String> properties = new HashSet<>();

        // Seeking properties like '${something}' in the input
        Pattern p = Pattern.compile("\\$\\{\\S+?\\}");
        Matcher matcher = p.matcher(input);
        while (matcher.find()) {
            properties.add(matcher.group());
        }

        return properties;
    }

    private String parsePropertyName(String property) {
        String parsedPropertyName = StringUtils.remove(property, "${");
        return StringUtils.remove(parsedPropertyName, "}");
    }

    private String getPropertyFromPom(String propertyName, MavenRepositorySystemSession mvnRepository, RepositorySystem system,
                                      RemoteRepository remoteRepository, PomInformation pomInformation) throws ArtifactResolutionException {
        Properties properties = pomInformation.getProperties();
        String property = properties.getProperty(propertyName);

        if (property == null) {
            if (pomInformation.getParentPomInformation() == null) {
                if (pomInformation.getParent() != null) {
                    Artifact artifact = new DefaultArtifact(pomInformation.getParent().getGroupId(),
                            pomInformation.getParent().getArtifactId(), "pom", pomInformation.getParent().getVersion());

                    ArtifactRequest artifactRequest = new ArtifactRequest();
                    artifactRequest.setArtifact(artifact);

                    if (pomInformation.getRepositories() != null) {
                        for (RemoteRepository repository : pomInformation.getRepositories()) {
                            artifactRequest.addRepository(repository);
                        }
                    }
                    artifactRequest.addRepository(remoteRepository);

                    ArtifactResult artifactResult = system.resolveArtifact(mvnRepository, artifactRequest);
                    File parentPOM = artifactResult.getArtifact().getFile();
                    pomInformation.parseParentPom(parentPOM);

                    return getPropertyFromPom(propertyName, mvnRepository, system, remoteRepository, pomInformation.getParentPomInformation());
                } else {
                    return null;
                }
            } else {
                return getPropertyFromPom(propertyName, mvnRepository, system, remoteRepository, pomInformation.getParentPomInformation());
            }
        } else {
            return property;
        }
    }

    private BundleInformation installWithDependenciesFromFile(File bundleFile, boolean startBundle) throws IOException {
        JarInformation jarInformation = getJarInformations(bundleFile);
        List<Bundle> bundlesInstalled = new ArrayList<>();
        BundleInformation bundleInformation = null;

        if (jarInformation == null) {
            throw new IOException("Unable to read bundleFile " + bundleFile.getAbsolutePath());
        }

        try {
            List<ArtifactResult> artifactResults = new LinkedList<>();
            bundleWatcherSuspensionService.suspendBundleProcessing();

            for (Dependency dependency : jarInformation.getPomInformation().getDependencies()) {
                artifactResults.addAll(resolveDependencies(dependency, jarInformation.getPomInformation()));
            }

            artifactResults = removeDuplicatedArtifacts(artifactResults);

            bundlesInstalled = installBundlesFromArtifacts(artifactResults);

            final Bundle requestedModule = installBundleFromFile(bundleFile, true, false);
            if (requestedModule != null) {
                if (!isFragmentBundle(requestedModule) && startBundle) {
                    requestedModule.start();
                }
                bundlesInstalled.add(requestedModule);
                bundleInformation = null;
            } else {
                bundleInformation = new BundleInformation(null);
            }
        } catch (BundleException | RepositoryException e) {
            throw new MotechException("Error while installing bundle and dependencies.", e);
        } finally {
            bundleWatcherSuspensionService.restoreBundleProcessing();
        }

        //start bundles after all bundles installed to avoid any dependency resolution problems.
        startBundles(bundlesInstalled, startBundle);
        return bundleInformation;
    }

    private List<Bundle> installBundlesFromArtifacts(List<ArtifactResult> artifactResults) throws BundleException, IOException, DependencyResolutionException {
        List<Bundle> bundlesInstalled = new LinkedList<>();
        for (ArtifactResult artifact : artifactResults) {
            if (isOSGiFramework(artifact)) {
                // skip the framework jar
                continue;
            }

            LOGGER.info("Installing " + artifact);
            final File dependencyBundleFile = artifact.getArtifact().getFile();

            final Bundle bundle = installBundleFromFile(dependencyBundleFile, false, true);
            if (bundle != null) {
                bundlesInstalled.add(bundle);
            }
        }
        return bundlesInstalled;
    }

    private List<ArtifactResult> removeDuplicatedArtifacts(List<ArtifactResult> artifactResults) {
        List<ArtifactResult> results = new LinkedList<>();

        for (ArtifactResult artifactResult : artifactResults) {
            boolean duplicate = false;
            for (ArtifactResult artifact : results) {
                if (artifactResult.getArtifact().equals(artifact.getArtifact())) {
                    duplicate = true;
                }
            }
            if (!duplicate) {
                results.add(artifactResult);
            }
        }

        return results;
    }

    private Bundle installBundleFromFile(File bundleFile, boolean updateExistingBundle,
                                         boolean installInBundlesDirectory) throws IOException, BundleException, DependencyResolutionException {
        LOGGER.info("Starting installation process for: {}", bundleFile);

        JarInformation jarInformation = getJarInformations(bundleFile);

        if (jarInformation == null || jarInformation.isMotechPlatformBundle()) {
            LOGGER.info("Skipping {}", bundleFile);
            return null;
        }

        Bundle bundle = findMatchingBundle(jarInformation);

        try (InputStream bundleInputStream = FileUtils.openInputStream(bundleFile)) {
            if (bundle == null) {
                LOGGER.info("Installing new bundle: {}", bundleFile);
                if (installInBundlesDirectory) {
                    // install in the bundles directory
                    LOGGER.debug("Installing {} in the bundle directory", bundleFile);
                    final File installedBundleFile = bundleDirectoryManager.saveBundleStreamToFile(bundleFile.getName(),
                            bundleInputStream);
                    final String bundleFileLocationAsURL = installedBundleFile.toURI().toURL().toExternalForm();
                    bundle = bundleContext.installBundle(bundleFileLocationAsURL);
                } else {
                    // install from the provided location
                    LOGGER.debug("Installing bundle from file: {}", bundleFile);
                    final String bundleFileLocationAsURL = bundleFile.toURI().toURL().toExternalForm();
                    bundle = bundleContext.installBundle(bundleFileLocationAsURL);
                }
            } else if (updateExistingBundle) {
                // either install from the file provided or install in the bundles directory
                final File installedBundleFile = (installInBundlesDirectory) ?
                        bundleDirectoryManager.saveBundleStreamToFile(bundleFile.getName(), bundleInputStream) :
                        bundleFile;

                LOGGER.info("Updating bundle " + bundle.getSymbolicName() + "|" + bundle.getVersion());
                if (bundle.getState() == Bundle.ACTIVE) {
                    bundle.stop();
                }

                try (InputStream installedInputStream =  FileUtils.openInputStream(installedBundleFile)) {
                    bundle.update(installedInputStream);
                }
            }
        }

        return bundle;
    }

    private boolean isFragmentBundle(Bundle bundle) {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }

    private boolean isValidPluginBundle(JarInformation jarInformation) {
        return isNotBlank(jarInformation.getBundleSymbolicName());
    }

    private JarInformation getJarInformations(File bundleFile) throws IOException {
        JarInformation jarInformation = new JarInformation(bundleFile);
        jarInformation.readPOMInformation(bundleFile);
        if (!isValidPluginBundle(jarInformation)) {
            LOGGER.warn(jarInformation.getFilename() + " is not allowed to install as add-on");
            return null;
        }
        return jarInformation;
    }

    private Bundle getBundle(long bundleId) {
        Bundle bundle = bundleContext.getBundle(bundleId);
        if (bundle == null || !motechBundleFilter.passesCriteria(bundle)) {
            throw new BundleNotFoundException("Bundle with id [" + bundleId + "] not found");
        }
        return bundle;
    }

    private Bundle findMatchingBundle(JarInformation jarInformation) {
        Bundle result = null;

        if (jarInformation != null) {
            for (Bundle bundle : bundleContext.getBundles()) {
                final String symbolicName = bundle.getSymbolicName();
                if (symbolicName != null && symbolicName.equals(jarInformation.getBundleSymbolicName())
                        && bundle.getHeaders().get(JarInformation.BUNDLE_VERSION).equals(jarInformation.getBundleVersion())) {
                    result = bundle;
                    break;
                }
            }
        }
        return result;
    }

    private BundleInformation installFromRepository(Dependency dependency, boolean start)
            throws RepositoryException, IOException, BundleException {
        LOGGER.info("Installing {} from repository", dependency);

        final String featureStrNoVersion = buildFeatureStrNoVersion(dependency);
        List<Bundle> bundlesInstalled = new ArrayList<>();
        BundleInformation bundleInformation = null;

        try {
            bundleWatcherSuspensionService.suspendBundleProcessing();
            List<ArtifactResult> dependencies = resolveDependencies(dependency, null);

            LOGGER.trace("Resolved the following dependencies for {}: {}", dependency, dependencies);

            for (ArtifactResult artifact : dependencies) {
                if (isOSGiFramework(artifact)) {
                    // skip the framework jar
                    continue;
                }

                LOGGER.info("Installing " + artifact);
                final File bundleFile = artifact.getArtifact().getFile();

                boolean isRequestedModule = isRequestedModule(artifact, featureStrNoVersion);

                final Bundle bundle = installBundleFromFile(bundleFile, isRequestedModule, true);
                if (bundle != null) {
                    bundlesInstalled.add(bundle);

                    if (isRequestedModule) {
                        bundleInformation = new BundleInformation(bundle);
                    }
                }
            }
        } finally {
            bundleWatcherSuspensionService.restoreBundleProcessing();
        }

        //start bundles after all bundles installed to avoid any dependency resolution problems.
        startBundles(bundlesInstalled, start);
        return bundleInformation;
    }

    private String buildFeatureStrNoVersion(Dependency dependency) {
        StringBuilder featureId = new StringBuilder(dependency.getArtifact().getGroupId());
        featureId = featureId.append(":").append(dependency.getArtifact().getArtifactId());
        featureId = featureId.append(":").append(dependency.getArtifact().getVersion());
        final int lastColonIndex = featureId.lastIndexOf(":");

        return featureId.substring(0, lastColonIndex);
    }

    private static class HttpWagonProvider implements WagonProvider {
        public Wagon lookup(String roleHint) {
            if ("http".equals(roleHint)) {
                return new LightweightHttpWagon() {
                    @Override
                    protected void openConnectionInternal() throws ConnectionException, AuthenticationException {
                    }
                };
            }
            return null;
        }

        public void release(Wagon wagon) {

        }
    }

    private boolean isOSGiFramework(ArtifactResult artifactResult) {
        Artifact artifact = artifactResult.getArtifact();
        return "org.apache.felix".equals(artifact.getGroupId()) &&
                "org.apache.felix.framework".equals(artifact.getArtifactId());
    }

    private void startBundles(List<Bundle> bundlesInstalled, boolean startBundles) {
        try {
            if (startBundles) {
                LOGGER.info("Starting installed bundles.");
                for (Bundle bundle : bundlesInstalled) {
                    if (bundle.getState() != Bundle.ACTIVE && !isFragmentBundle(bundle)) {
                        LOGGER.trace("Starting bundle: {}", bundle.getSymbolicName());
                        bundle.start();
                    }
                }
            }
        } catch (Exception e) {
            throw new MotechException("Error while starting bundle.", e);
        }
    }

    private boolean isRequestedModule(ArtifactResult artifactResult, String featureStr) {
        Artifact artifact = artifactResult.getArtifact();
        return StringUtils.equals(featureStr, String.format("%s:%s", artifact.getGroupId(), artifact.getArtifactId()));
    }

    @MotechListener(subjects = FILE_CHANGED_EVENT_SUBJECT)
    public void changeMaxUploadSize(MotechEvent event) {
        String uploadSize = configurationService.getPlatformSettings().getUploadSize();

        if (StringUtils.isNotBlank(uploadSize)) {
            commonsMultipartResolver.setMaxUploadSize(Long.valueOf(uploadSize));
        }
    }
}
