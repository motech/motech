package org.motechproject.admin.service;

import org.motechproject.admin.bundles.BundleIcon;
import org.motechproject.server.osgi.BundleInformation;
import org.osgi.framework.BundleException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ModuleAdminService {

    List<BundleInformation> getBundles();

    BundleInformation getBundleInfo(long bundleId);

    BundleInformation stopBundle(long bundleId) throws BundleException;

    BundleInformation startBundle(long bundleId) throws BundleException;

    BundleInformation restartBundle(long bundleId) throws BundleException;

    void uninstallBundle(long bundleId) throws BundleException;

    BundleIcon getBundleIcon(long bundleId);

    BundleInformation installBundle(MultipartFile bundleFile);
}
