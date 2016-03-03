package org.motechproject.admin.internal.service;

import org.motechproject.admin.bundles.ExtendedBundleInformation;
import org.motechproject.admin.security.SecurityConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.admin.bundles.BundleInformation;
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
     * Retrieves a list of MOTECH module bundles. Platform bundles required for operation and 3rd party bundles will
     * be hidden from this view.
     * @return a list of {@link BundleInformation} objects representing MOTECH modules in the system.
     */
    @PreAuthorize(SecurityConstants.MANAGE_BUNDLES)
    List<BundleInformation> getBundles();

    /**
     * Retrieves information for a bundle with a given bundle id.
     * @param bundleId the id of the bundle given by the OSGi framework.
     * @return a {@link BundleInformation} object encapsulating bundle details.
     */
    @PreAuthorize(SecurityConstants.MANAGE_BUNDLES)
    BundleInformation getBundleInfo(long bundleId);

    /**
     * Retrieves detailed information about the given bundle.
     * @param bundleId the bundle id of the bundle for which the information should be retrieved.
     * @return an {@link org.motechproject.admin.bundles.ExtendedBundleInformation} object containing detailed
     * information about the given bundle.
     */
    @PreAuthorize(SecurityConstants.MANAGE_BUNDLES)
    ExtendedBundleInformation getBundleDetails(long bundleId);

    /**
     * Stops a bundle with the given bundle id.
     * @param bundleId the id of the bundle that should be stopped.
     * @return a {@link BundleInformation} object encapsulating the stopped bundle details.
     * @throws BundleException when it failed to stop the bundle.
     */
    @PreAuthorize(SecurityConstants.MANAGE_BUNDLES)
    BundleInformation stopBundle(long bundleId) throws BundleException;

    /**
     * Starts the bundle with the given bundle id.
     * @param bundleId the id of the bundle that should be started.
     * @return a {@link BundleInformation} object encapsulating the started bundle details.
     * @throws BundleException when it failed to start the bundle.
     */
    @PreAuthorize(SecurityConstants.MANAGE_BUNDLES)
    BundleInformation startBundle(long bundleId) throws BundleException;

    /**
     * Restarts the bundle with the given bundle id. This is a shorthand for stopping and then starting a bundle.
     * @param bundleId the id of the bundle that should be restarted.
     * @return a {@link BundleInformation} object encapsulating the restarted bundle details.
     * @throws BundleException hen it failed to restart the bundle.
     */
    @PreAuthorize(SecurityConstants.MANAGE_BUNDLES)
    BundleInformation restartBundle(long bundleId) throws BundleException;

    /**
     * Uninstalls the bundle with the given bundle id from the system. The bundle file is also physically removed.
     * @param bundleId the id of the bundle that should be uninstalled.
     * @param removeConfig true if config file for bundle should be removed
     * @throws BundleException when it failed to uninstall the bundle.
     */
    @PreAuthorize(SecurityConstants.MANAGE_BUNDLES)
    void uninstallBundle(long bundleId, boolean removeConfig) throws BundleException;

    /**
     * Installs a bundle in the system. Used to install uploaded bundles. This does not install any dependencies.
     * @param bundleFile the {@link MultipartFile} which should be the actual bundle jar.
     * @param startBundle whether the bundle should be started after installation.
     * @return the {@link BundleInformation} for the newly installed bundle.
     */
    @PreAuthorize(SecurityConstants.MANAGE_BUNDLES)
    BundleInformation installBundle(MultipartFile bundleFile, boolean startBundle);

    /**
     * Installs a feature(module) in the system using the Nexus repository. The requested bundle is downloaded and
     * installed, and so are its dependencies.
     * @param moduleId the id of the feature to be downloaded and installed.
     * @param startBundle whether the module should be started after installation.
     * @return the {@link BundleInformation} for the newly installed bundle
     */
    @PreAuthorize(SecurityConstants.MANAGE_BUNDLES)
    BundleInformation installBundleFromRepository(String moduleId, boolean startBundle);

    /**
     * Changes the max upload size allowed for bundles.
     * @param event the {@link MotechEvent} indicating the configuration change.
     */
    void changeMaxUploadSize(MotechEvent event);
}
