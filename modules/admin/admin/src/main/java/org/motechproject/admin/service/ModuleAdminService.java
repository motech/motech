package org.motechproject.admin.service;

import org.motechproject.admin.bundles.ExtendedBundleInformation;
import org.motechproject.server.api.BundleIcon;
import org.motechproject.server.api.BundleInformation;
import org.osgi.framework.BundleException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ModuleAdminService {

    @PreAuthorize("hasRole('manageBundles')")
    List<BundleInformation> getBundles();

    BundleInformation getBundleInfo(long bundleId);

    @PreAuthorize("hasRole('stopBundle')")
    BundleInformation stopBundle(long bundleId) throws BundleException;

    @PreAuthorize("hasRole('startBundle')")
    BundleInformation startBundle(long bundleId) throws BundleException;

    @PreAuthorize("hasRole('startBundle') and hasRole('stopBundle')")
    BundleInformation restartBundle(long bundleId) throws BundleException;

    @PreAuthorize("hasRole('uninstallBundle')")
    void uninstallBundle(long bundleId) throws BundleException;

    BundleIcon getBundleIcon(long bundleId);

    @PreAuthorize("hasAnyRole('manageBundles', 'installBundle')")
    BundleInformation installBundle(MultipartFile bundleFile);

    @PreAuthorize("hasRole('manageBundles') and hasRole('bundleDetails')")
    ExtendedBundleInformation getBundleDetails(long bundleId);

    @PreAuthorize("hasAnyRole('manageBundles', 'installBundle')")
    BundleInformation installBundle(MultipartFile bundleFile, boolean startBundle);
}
