package org.motechproject.server.ui;

import org.motechproject.server.web.dto.BundleIcon;

/**
 * The <code>BundleIconService</code> interface provides methods for getting icons from the bundles.
 */
public interface BundleIconService {

    /**
     * Retrieves a {@link BundleIcon} for the bundle with the given bundle id. The icon is
     * loaded from the bundle.
     * @param bundleId the bundle id of the bundle for which the icon should be retrieved.
     * @param defaultIcon name of the default icon stored in platform-server-bundle.
     * @return the icon retrieved for the bundle. If no icon is available, the default icon is returned.
     */
    BundleIcon getBundleIconById(long bundleId, String defaultIcon);

    /**
     * Retrieves a {@link BundleIcon} for the bundle with the given bundle id. The icon is
     * loaded from the bundle.
     * @param bundleName the name of the bundle for which the icon should be retrieved.
     * @param defaultIcon name of the default icon stored in platform-server-bundle.
     * @return  the icon retrieved for the bundle. If no icon is available, the default icon is returned.
     */
    BundleIcon getBundleIconByName(String bundleName, String defaultIcon);
}
