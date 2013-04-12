package org.motechproject.admin.service.impl;

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
import org.motechproject.admin.service.ModuleAdminService;
import org.motechproject.commons.api.MotechException;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.server.api.BundleIcon;
import org.motechproject.server.api.BundleInformation;
import org.motechproject.server.api.JarInformation;
import org.motechproject.server.config.monitor.ConfigFileMonitor;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.connector.wagon.WagonProvider;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;
import org.sonatype.aether.util.filter.DependencyFilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.server.api.BundleIcon.ICON_LOCATIONS;


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
    private PlatformSettingsService platformSettingsService;

    @Override
    public List<BundleInformation> getBundles() {
        List<BundleInformation> bundles = new ArrayList<>();

        List<Bundle> motechBundles = motechBundleFilter.filter(bundleContext.getBundles());

        for (Bundle bundle : motechBundles) {
            bundles.add(new BundleInformation(bundle));
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
    public void uninstallBundle(long bundleId) throws BundleException {
        Bundle bundle = getBundle(bundleId);
        bundle.uninstall();

        try {
            boolean deleted = bundleDirectoryManager.removeBundle(bundle);
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
    public BundleInformation installBundle(MultipartFile bundleFile) {
        return installBundle(bundleFile, true);
    }

    @Override
    public BundleInformation installBundle(MultipartFile bundleFile, boolean startBundle) {
        File savedBundleFile = null;
        try {
            savedBundleFile = bundleDirectoryManager.saveBundleFile(bundleFile);
            return new BundleInformation(installBundleFromFile(savedBundleFile, startBundle, true));
        } catch (Exception e) {
            if (savedBundleFile != null) {
                LOG.error("Removing bundle due to exception", e);
                savedBundleFile.delete();
            }
            throw new MotechException("Cannot install file", e);
        }
    }

    private Bundle installBundleFromFile(File savedBundleFile, boolean startBundle, boolean updateExistingBundle) throws IOException, BundleException {
        InputStream bundleInputStream = null;
        try {
            bundleInputStream = FileUtils.openInputStream(savedBundleFile);

            JarInformation jarInformation = new JarInformation(savedBundleFile);
            if (!isValidPluginBundle(jarInformation)) {
                LOG.warn(jarInformation.getFilename() + " is not allowed to install as add-on");
                return null;
            }
            Bundle bundle = findMatchingBundle(jarInformation);

            if (bundle == null) {
                final String bundleFileLocationAsURL = savedBundleFile.toURI().toURL().toExternalForm();
                bundle = bundleContext.installBundle(bundleFileLocationAsURL, bundleInputStream);
            } else if (updateExistingBundle) {
                LOG.info("Updating bundle " + bundle.getSymbolicName() + "|" + bundle.getVersion());
                bundle.update(bundleInputStream);
            }

            if (!isFragmentBundle(bundle) && startBundle) {
                bundle.start();
            }

            return bundle;
        } finally {
            IOUtils.closeQuietly(bundleInputStream);
        }
    }

    private boolean isFragmentBundle(Bundle bundle) {
        return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }

    private boolean isValidPluginBundle(JarInformation jarInformation) {
        if (isBlank(jarInformation.getBundleSymbolicName())) {
            return false;
        }
        if (jarInformation.getBundleSymbolicName().contains("org.motechproject.motech-platform-")) {
            return false; // disallow installation of core bundles from UI.
        }
        return true;
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
        for (Bundle bundle : bundleContext.getBundles()) {
            final String symbolicName = bundle.getSymbolicName();
            if (symbolicName != null && symbolicName.equals(jarInformation.getBundleSymbolicName())
                    && bundle.getHeaders().get(JarInformation.BUNDLE_VERSION).equals(jarInformation.getBundleVersion())) {
                result = bundle;
                break;
            }
        }
        return result;
    }

    @Override
    public BundleInformation installFromRepository(String featureId, boolean start) {

        org.apache.maven.repository.internal.DefaultServiceLocator locator = new org.apache.maven.repository.internal.DefaultServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
        locator.setServices(WagonProvider.class, new HttpWagonProvider());

        RepositorySystem system = locator.getService(RepositorySystem.class);

        //RepositorySystem system = null;
        try {
            //system = new DefaultPlexusContainer().lookup(RepositorySystem.class);

            MavenRepositorySystemSession mvnRepository = new MavenRepositorySystemSession();

            mvnRepository.setLocalRepositoryManager(system.newLocalRepositoryManager(new LocalRepository(System.getProperty("java.io.tmpdir") + "/repo")));


            CollectRequest collectRequest = new CollectRequest();
            collectRequest.setRoot(new Dependency(new DefaultArtifact(featureId), JavaScopes.RUNTIME));
            collectRequest.addRepository(new RemoteRepository("central", "default", "http://nexus.motechproject.org/content/repositories/public"));


            DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, DependencyFilterUtils.classpathFilter(JavaScopes.RUNTIME));

            BundleInformation bundleInformation = null;
            List<ArtifactResult> artifactResults = system.resolveDependencies(mvnRepository, dependencyRequest).getArtifactResults();

            List<Bundle> bundlesInstalled = new ArrayList<>();

            for (ArtifactResult artifact : artifactResults) {
                LOG.info("Installing " + artifact);
                final File bundleFile = artifact.getArtifact().getFile();
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(bundleFile);
                    final File bundleFileToInstall = bundleDirectoryManager.saveBundleStreamToFile(bundleFile.getName(), fileInputStream);
                    final Bundle bundle = installBundleFromFile(bundleFileToInstall, false, false);
                    if (bundle != null) {
                        bundlesInstalled.add(bundle);
                    }
                } finally {
                    IOUtils.closeQuietly(fileInputStream);
                }
            }

            //start bundles after all bundles installed to avoid any dependency resolution problems.
            if (start) {
                for (Bundle bundle : bundlesInstalled) {
                    if (!isFragmentBundle(bundle)) {
                        bundle.start();
                    }
                }
            }

            return bundleInformation;
        } catch (Exception e) {
            LOG.error("Error while installing bundle and dependencies " + featureId, e);
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

    @MotechListener(subjects = ConfigFileMonitor.FILE_CHANGED_EVENT_SUBJECT)
    public void changeMaxUploadSize(MotechEvent event) {
        String uploadSize = platformSettingsService.getPlatformSettings().getUploadSize();

        if (StringUtils.isNotBlank(uploadSize)) {
            commonsMultipartResolver.setMaxUploadSize(Long.valueOf(uploadSize));
        }
    }
}
