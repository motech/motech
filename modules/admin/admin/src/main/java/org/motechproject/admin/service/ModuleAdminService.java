package org.motechproject.admin.service;

import org.motechproject.admin.bundles.ExtendedBundleInformation;
import org.motechproject.event.MotechEvent;
import org.motechproject.server.api.BundleIcon;
import org.motechproject.server.api.BundleInformation;
import org.osgi.framework.BundleException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service used by the view layer to manage bundles in the system. Supports retrieval of bundle information,
 * managing bundle state, bundle installation/uninstallation, etc. Adequate admin permissions are required to use methods
 * from this service.
 */
public interface ModuleAdminService {

    /**
     * Retrieves a list of Motech module bundles. Platform bundles required for operation and 3rd party bundles will
     * be hidden from this view.
     * @return a list of {@link BundleInformation} objects representing Motech modules in the system.
     */
    @PreAuthorize("hasRole('manageBundles')")
    List<BundleInformation> getBundles();

    /**
     * Retrieves information for a bundle with a given bundle id.
     * @param bundleId the id of the bundle given by the OSGi framework.
     * @return a {@link BundleInformation} object encapsulating bundle details.
     */
    @PreAuthorize("hasRole('manageBundles')")
    BundleInformation getBundleInfo(long bundleId);

    /**
     * Retrieves a {@link org.motechproject.server.api.BundleIcon} for the bundle with the given bundle id. The icon is
     * loaded from the bundle.
     * @param bundleId the bundle id of the bundle for which the icon should be retrieved.
     * @return the icon retrieved for the bundle. If no icon is available, the default icon is returned.
     */
    BundleIcon getBundleIcon(long bundleId);

    /**
     * Retrieves detailed information about the given bundle.
     * @param bundleId the bundle id of the bundle for which the information should be retrieved.
     * @return an {@link org.motechproject.admin.bundles.ExtendedBundleInformation} object containing detailed
     * information about the given bundle.
     */
    @PreAuthorize("hasRole('manageBundles') and hasRole('bundleDetails')")
    ExtendedBundleInformation getBundleDetails(long bundleId);

    /**
     * Stops a bundle with the given bundle id.
     * @param bundleId the id of the bundle that should be stopped.
     * @return a {@link BundleInformation} object encapsulating the stopped bundle details.
     * @throws BundleException when it failed to stop the bundle.
     */
    @PreAuthorize("hasRole('stopBundle')")
    BundleInformation stopBundle(long bundleId) throws BundleException;

    /**
     * Starts the bundle with the given bundle id.
     * @param bundleId the id of the bundle that should be started.
     * @return a {@link BundleInformation} object encapsulating the started bundle details.
     * @throws BundleException when it failed to start the bundle.
     */
    @PreAuthorize("hasRole('startBundle')")
    BundleInformation startBundle(long bundleId) throws BundleException;

    /**
     * Restarts the bundle with the given bundle id. This is a shorthand for stopping and then starting a bundle.
     * @param bundleId the id of the bundle that should be restarted.
     * @return a {@link BundleInformation} object encapsulating the restarted bundle details.
     * @throws BundleException hen it failed to restart the bundle.
     */
    @PreAuthorize("hasRole('startBundle') and hasRole('stopBundle')")
    BundleInformation restartBundle(long bundleId) throws BundleException;

    /**
     * Uninstalls the bundle with the given bundle id from the system. The bundle file is also physically removed.
     * @param bundleId the id of the bundle that should be uninstalled.
     * @throws BundleException when it failed to uninstall the bundle.
     */
    @PreAuthorize("hasRole('uninstallBundle')")
    void uninstallBundle(long bundleId) throws BundleException;

    /**
     * Installs a bundle in the system. The installed bundle is then automatically started. Used to install uploaded
     * bundles. This does not install any dependencies.
     * @param bundleFile the {@link MultipartFile} which should be the actual bundle jar.
     * @return the {@link BundleInformation} for the newly installed bundle.
     */
    @PreAuthorize("hasAnyRole('manageBundles', 'installBundle')")
    BundleInformation installBundle(MultipartFile bundleFile);

    /**
     * Installs a bundle in the system. Used to install uploaded bundles. This does not install any dependencies.
     * @param bundleFile the {@link MultipartFile} which should be the actual bundle jar.
     * @param startBundle whether the bundle should be started after installation.
     * @return the {@link BundleInformation} for the newly installed bundle.
     */
    @PreAuthorize("hasAnyRole('manageBundles', 'installBundle')")
    BundleInformation installBundle(MultipartFile bundleFile, boolean startBundle);

    /**
     * Installs a feature(module) in the system using the Nexus repository. The requested bundle is downloaded and
     * installed, and so are its dependencies.
     * @param featureId the id of the feature to be downloaded and installed.
     * @param start whether the module should be started after installation.
     * @return the {@link BundleInformation} for the newly installed bundle
     */
    @PreAuthorize("hasAnyRole('manageBundles', 'installBundle')")
    BundleInformation installFromRepository(String featureId, boolean start);

    /**
     * Changes the max upload size allowed for bundles.
     * @param event the {@link MotechEvent} indicating the configuration change.
     */
    void changeMaxUploadSize(MotechEvent event);
}
