package org.motechproject.admin.service.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.motechproject.MotechException;
import org.motechproject.admin.bundles.BundleDirectoryManager;
import org.motechproject.admin.bundles.BundleIcon;
import org.motechproject.admin.bundles.ExtendedBundleInformation;
import org.motechproject.admin.ex.BundleNotFoundException;
import org.motechproject.admin.service.ModuleAdminService;
import org.motechproject.server.osgi.BundleInformation;
import org.motechproject.server.osgi.BundleLoader;
import org.motechproject.server.osgi.JarInformation;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@Service
public class ModuleAdminServiceImpl implements ModuleAdminService {

    private static final Logger LOG = LoggerFactory.getLogger(ModuleAdminServiceImpl.class);

    private static final String[] ICON_LOCATIONS = new String[] { "icon.gif", "icon.jpg", "icon.png" };
    private static final String DEFAULT_ICON = "/bundle_icon.png";

    @Autowired
    private BundleContext bundleContext;

    @Autowired
    private BundleDirectoryManager bundleDirectoryManager;

    @Resource(name = "bundleLoaders")
    List<BundleLoader> bundleLoaders;

    @Override
    public List<BundleInformation> getBundles() {
        List<BundleInformation> bundles = new ArrayList<>();
        for (Bundle bundle : bundleContext.getBundles()) {
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
        InputStream bundleInputStream = null;
        try {
            savedBundleFile = bundleDirectoryManager.saveBundleFile(bundleFile);
            bundleInputStream = FileUtils.openInputStream(savedBundleFile);

            JarInformation jarInformation = new JarInformation(savedBundleFile);
            Bundle bundle = findMatchingBundle(jarInformation);

            if (bundle == null) {
                bundle = bundleContext.installBundle(savedBundleFile.getAbsolutePath(), bundleInputStream);
            } else {
                bundle.update(bundleInputStream);
            }

            runBundleLoaders(bundle);

            if (startBundle) {
                bundle.start();
            } else {
                bundle.stop();
            }

            return new BundleInformation(bundle);
        }  catch (Exception e) {
            if (savedBundleFile != null) {
                LOG.error("Removing bundle due to exception", e);
                savedBundleFile.delete();
            }
            throw new MotechException("Error saving file", e);
        } finally {
            IOUtils.closeQuietly(bundleInputStream);
        }
    }

    @Override
    public ExtendedBundleInformation getBundleDetails(long bundleId) {
        Bundle bundle = getBundle(bundleId);
        return new ExtendedBundleInformation(bundle);
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
        if (bundle == null) {
            throw new BundleNotFoundException("Bundle with id [" + bundleId + "] not found");
        }
        return bundle;
    }

    private void runBundleLoaders(Bundle bundle) throws Exception {
        for (BundleLoader loader : bundleLoaders) {
            loader.loadBundle(bundle);
        }
    }

    private Bundle findMatchingBundle(JarInformation jarInformation) {
        Bundle result = null;
        for (Bundle bundle : bundleContext.getBundles()) {
            if (bundle.getSymbolicName().equals(jarInformation.getBundleSymbolicName())
                    && bundle.getHeaders().get(JarInformation.BUNDLE_VERSION).equals(jarInformation.getBundleVersion())) {
                result = bundle;
                break;
            }
        }
        return result;
    }
}
