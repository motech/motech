package org.motechproject.admin.internal.service.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.motechproject.admin.bundles.BundleDirectoryManager;
import org.motechproject.admin.bundles.ExtendedBundleInformation;
import org.motechproject.admin.bundles.ImportExportResolver;
import org.motechproject.admin.bundles.MotechBundleFilter;
import org.motechproject.admin.ex.BundleNotFoundException;
import org.motechproject.admin.internal.service.ModuleAdminService;
import org.motechproject.admin.service.impl.MavenRepositorySystemSession;
import org.motechproject.commons.api.MotechException;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.server.api.BundleIcon;
import org.motechproject.server.api.BundleInformation;
import org.motechproject.server.api.JarInformation;
import org.motechproject.server.config.SettingsFacade;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.connector.wagon.WagonProvider;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.config.core.constants.ConfigurationConstants.FILE_CHANGED_EVENT_SUBJECT;
import static org.motechproject.server.api.BundleIcon.ICON_LOCATIONS;
import static org.springframework.util.CollectionUtils.isEmpty;


/**
 * Implementation of the {@link ModuleAdminService} interface for bundle management.
 */
@Service
public class ModuleAdminServiceImpl implements ModuleAdminService {

    private static final Logger LOG = LoggerFactory.getLogger(ModuleAdminServiceImpl.class);

    private static final String DEFAULT_ICON = "/bundle_icon.png";

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
    private SettingsFacade settingsFacade;

    @Autowired
    private UIFrameworkService uiFrameworkService;

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
            settingsFacade.unregisterProperties(bundle.getSymbolicName());
        }

        try {
            boolean deleted = bundleDirectoryManager.removeBundle(bundle);
            importExportResolver.refreshPackage(bundle);
            if (!deleted) {
                LOG.warn("Failed to delete bundle file: " + bundle.getLocation());
            }
        } catch (IOException e) {
            throw new MotechException("Error while removing bundle file", e);
        }
    }

    @Override
    public BundleIcon getBundleIcon(long bundleId) {
        BundleIcon bundleIcon = null;
        Bundle bundle = getBundle(bundleId);

        for (String iconLocation : ICON_LOCATIONS) {
            URL iconURL = bundle.getResource(iconLocation);
            if (iconURL != null) {
                bundleIcon = loadBundleIcon(iconURL);
                break;
            }
        }

        if (bundleIcon == null) {
            URL defaultIconURL = getClass().getResource(DEFAULT_ICON);
            bundleIcon = loadBundleIcon(defaultIconURL);
        }

        return bundleIcon;
    }

    @Override
    public BundleInformation installBundleFromRepository(String moduleId, boolean startBundle) {
        return installFromRepository(new Dependency(new DefaultArtifact(moduleId), JavaScopes.RUNTIME), startBundle);
    }

    @Override
    public BundleInformation installBundle(MultipartFile bundleFile) {
        return installBundle(bundleFile, true);
    }

    @Override
    public BundleInformation installBundle(MultipartFile bundleFile, boolean startBundle) {
        File savedBundleFile = null;
        try {
            savedBundleFile = bundleDirectoryManager.saveBundleFile(bundleFile);
            return installWithDependenciesFromFile(savedBundleFile, startBundle);
        } catch (Exception e) {
            if (savedBundleFile != null) {
                LOG.error("Removing bundle due to exception", e);
                savedBundleFile.delete();
            }
            throw new MotechException("Cannot install file", e);
        }
    }

    private List<ArtifactResult> resolveDependencies(Dependency dependency, List<RemoteRepository> remoteRepositories) throws DependencyResolutionException {
        org.apache.maven.repository.internal.DefaultServiceLocator locator = new org.apache.maven.repository.internal.DefaultServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
        locator.setServices(WagonProvider.class, new HttpWagonProvider());

        RepositorySystem system = locator.getService(RepositorySystem.class);

        MavenRepositorySystemSession mvnRepository = new MavenRepositorySystemSession();

        mvnRepository.setLocalRepositoryManager(system.newLocalRepositoryManager(new LocalRepository(System.getProperty("java.io.tmpdir") + "/repo")));
        RemoteRepository remoteRepository = new RemoteRepository("central", "default", "http://nexus.motechproject.org/content/repositories/public");

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(dependency);
        collectRequest.addRepository(remoteRepository);

        if (remoteRepositories != null) {
            for (RemoteRepository repository : remoteRepositories) {
                collectRequest.addRepository(repository);
            }
        }

        DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, DependencyFilterUtils.classpathFilter(JavaScopes.RUNTIME));
        return system.resolveDependencies(mvnRepository, dependencyRequest).getArtifactResults();
    }

    private BundleInformation installWithDependenciesFromFile(File bundleFile, boolean startBundle) throws IOException {
        JarInformation jarInformation = getJarInformations(bundleFile);

        try {
            List<ArtifactResult> artifactResults = new LinkedList<>();

            for (Dependency dependency : jarInformation.getDependencies()) {
                artifactResults.addAll(resolveDependencies(dependency, jarInformation.getRepositories()));
            }

            artifactResults = removeDuplicatedArtifacts(artifactResults);

            List<Bundle> bundlesInstalled = installBundlesFromArtifacts(artifactResults);

            final Bundle requestedModule = installBundleFromFile(bundleFile, true, false);
            BundleInformation bundleInformation;
            if (requestedModule != null) {
                if (!isFragmentBundle(requestedModule) && startBundle) {
                    requestedModule.start();
                }
                bundlesInstalled.add(requestedModule);
                bundleInformation = null;
            } else {
                bundleInformation = new BundleInformation(requestedModule);
            }

            //start bundles after all bundles installed to avoid any dependency resolution problems.
            if (startBundle) {
                for (Bundle bundle : bundlesInstalled) {
                    if (bundle.getState() != Bundle.ACTIVE && !isFragmentBundle(bundle)) {
                        LOG.info("Starting bundle: {}", bundle.getSymbolicName());
                        bundle.start();
                    }
                }
            }

            return bundleInformation;
        } catch (Exception e) {
            LOG.error("Error while installing bundle and dependencies ", e);
            throw new MotechException("Cannot install file", e);
        }
    }

    private List<Bundle> installBundlesFromArtifacts(List<ArtifactResult> artifactResults) throws BundleException, IOException, DependencyResolutionException {
        List<Bundle> bundlesInstalled = new LinkedList<>();
        for (ArtifactResult artifact : artifactResults) {
            if (isOSGiFramework(artifact)) {
                // skip the framework jar
                continue;
            }

            LOG.info("Installing " + artifact);
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
        JarInformation jarInformation = getJarInformations(bundleFile);

        if (jarInformation == null || jarInformation != null && jarInformation.isMotechPlatformBundle()) {
            return null;
        }

        Bundle bundle = findMatchingBundle(jarInformation);

        try (InputStream bundleInputStream = FileUtils.openInputStream(bundleFile)) {
            if (bundle == null) {
                if (installInBundlesDirectory) {
                    // install in the bundles directory
                    final File installedBundleFile = bundleDirectoryManager.saveBundleStreamToFile(bundleFile.getName(),
                            bundleInputStream);
                    final String bundleFileLocationAsURL = installedBundleFile.toURI().toURL().toExternalForm();
                    bundle = bundleContext.installBundle(bundleFileLocationAsURL);
                } else {
                    // install from the provided location
                    final String bundleFileLocationAsURL = bundleFile.toURI().toURL().toExternalForm();
                    bundle = bundleContext.installBundle(bundleFileLocationAsURL);
                }
            } else if (updateExistingBundle) {
                // either install from the file provided or install in the bundles directory
                final File installedBundleFile = (installInBundlesDirectory) ?
                        bundleDirectoryManager.saveBundleStreamToFile(bundleFile.getName(), bundleInputStream) :
                        bundleFile;

                LOG.info("Updating bundle " + bundle.getSymbolicName() + "|" + bundle.getVersion());
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
            LOG.warn(jarInformation.getFilename() + " is not allowed to install as add-on");
            return null;
        }
        return jarInformation;
    }

    @Override
    public ExtendedBundleInformation getBundleDetails(long bundleId) {
        Bundle bundle = getBundle(bundleId);

        ExtendedBundleInformation bundleInfo = new ExtendedBundleInformation(bundle);
        importExportResolver.resolveBundleWiring(bundleInfo);

        return bundleInfo;
    }

    private BundleIcon loadBundleIcon(URL iconURL) {
        InputStream is = null;
        try {
            URLConnection urlConn = iconURL.openConnection();
            is = urlConn.getInputStream();

            String mime = urlConn.getContentType();
            byte[] image = IOUtils.toByteArray(is);

            return new BundleIcon(image, mime);
        } catch (IOException e) {
            throw new MotechException("Error loading icon", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
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

    private BundleInformation installFromRepository(Dependency dependency, boolean start) {
        StringBuilder featureId = new StringBuilder(dependency.getArtifact().getGroupId());
        featureId = featureId.append(":").append(dependency.getArtifact().getArtifactId());
        featureId = featureId.append(":").append(dependency.getArtifact().getVersion());
        try {
            List<Bundle> bundlesInstalled = new ArrayList<>();
            BundleInformation bundleInformation = null;
            final int lastColonIndex = featureId.lastIndexOf(":");
            final String featureStrNoVersion = featureId.substring(0, lastColonIndex);

            for (ArtifactResult artifact : resolveDependencies(dependency, null)) {
                if (isOSGiFramework(artifact)) {
                    // skip the framework jar
                    continue;
                }

                LOG.info("Installing " + artifact);
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

            //start bundles after all bundles installed to avoid any dependency resolution problems.
            if (start) {
                for (Bundle bundle : bundlesInstalled) {
                    if (bundle.getState() != Bundle.ACTIVE && !isFragmentBundle(bundle)) {
                        LOG.info("Starting bundle: {}", bundle.getSymbolicName());
                        bundle.start();
                    }
                }
            }

            return bundleInformation;
        } catch (Exception e) {
            LOG.error("Error while installing bundle and dependencies " + featureId.toString(), e);
            throw new MotechException("Cannot install file", e);
        }
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

    private boolean isRequestedModule(ArtifactResult artifactResult, String featureStr) {
        Artifact artifact = artifactResult.getArtifact();
        return StringUtils.equals(featureStr, String.format("%s:%s", artifact.getGroupId(), artifact.getArtifactId()));
    }

    @MotechListener(subjects = FILE_CHANGED_EVENT_SUBJECT)
    public void changeMaxUploadSize(MotechEvent event) {
        String uploadSize = settingsFacade.getPlatformSettings().getUploadSize();

        if (StringUtils.isNotBlank(uploadSize)) {
            commonsMultipartResolver.setMaxUploadSize(Long.valueOf(uploadSize));
        }
    }
}
