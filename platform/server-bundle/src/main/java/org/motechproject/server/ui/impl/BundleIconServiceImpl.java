package org.motechproject.server.ui.impl;

import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.server.ui.BundleIconService;
import org.motechproject.server.web.dto.BundleIcon;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static org.eclipse.gemini.blueprint.util.OsgiStringUtils.nullSafeSymbolicName;
import static org.motechproject.server.web.dto.BundleIcon.ICON_LOCATIONS;

/**
 * Implementation of the <code>BundleIconService</code> interface. Provides access to bundle icons by its id or name.
 * If couldn't find any icon, returns default icon.
 */

@Service("bundleService")
public class BundleIconServiceImpl implements BundleIconService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleIconServiceImpl.class);

    private static final String DEFAULT_PATH = "/webapp/img/";
    private static final String DEFAULT_ICON = "bundle_icon.png";

    @Autowired
    private BundleContext bundleContext;

    @Override
    public BundleIcon getBundleIconById(long bundleId, String defaultIcon) {
        Bundle bundle = getBundleById(bundleId);

        return getBundleIcon(bundle, defaultIcon);
    }

    @Override
    public BundleIcon getBundleIconByName(String bundleName, String defaultIcon) {
        Bundle bundle = getBundleByName(bundleName);

        return getBundleIcon(bundle, defaultIcon);
    }

    private BundleIcon getBundleIcon(Bundle bundle, String defaultIcon) {
        BundleIcon bundleIcon = null;

        if (bundle != null) {
            for (String iconLocation : ICON_LOCATIONS) {
                URL iconURL = bundle.getResource(iconLocation);
                if (iconURL != null) {
                    bundleIcon = loadBundleIcon(iconURL);
                    break;
                }
            }
        }

        if (bundleIcon == null) {
            URL defaultIconURL;
            if (defaultIcon == null) {
                defaultIconURL = getClass().getResource(DEFAULT_PATH + DEFAULT_ICON);
            } else {
                defaultIconURL = getClass().getResource(DEFAULT_PATH + defaultIcon);
            }
            bundleIcon = loadBundleIcon(defaultIconURL);
        }

        return bundleIcon;
    }

    private Bundle getBundleById(long bundleId) {
        Bundle bundle = bundleContext.getBundle(bundleId);

        if (bundle == null) {
            LOGGER.warn("Bundle with id = " + bundleId + " cannot be found.");
        }

        return bundle;
    }

    private Bundle getBundleByName(String bundleName) {
        for (Bundle bundle : bundleContext.getBundles()) {
            if (nullSafeSymbolicName(bundle).equalsIgnoreCase(bundleName)) {
                return bundle;
            }
        }

        LOGGER.warn("Bundle with name = " + bundleName + "cannot be found.");

        return null;
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
            throw new MotechException("Error loading icon.", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
