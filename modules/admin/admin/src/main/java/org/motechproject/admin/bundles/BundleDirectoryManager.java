package org.motechproject.admin.bundles;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.osgi.framework.Bundle;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;

public class BundleDirectoryManager {

    private String bundleDir;

    public String getBundleDir() {
        return bundleDir;
    }

    public void setBundleDir(String bundleDir) {
        this.bundleDir = bundleDir;
        if (!this.bundleDir.endsWith(File.separator)) {
            this.bundleDir += File.separator;
        }
    }

    public File saveBundleFile(MultipartFile multipartFile) throws IOException {
        String destFileName = multipartFile.getOriginalFilename();
        InputStream is = null;
        try {
            is = multipartFile.getInputStream();
            return saveBundleStreamToFile(destFileName, is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public File saveBundleStreamToFile(String destFileName, InputStream is) throws IOException {
        File destFile = new File(bundleDir + destFileName);
        OutputStream os = null;
        try {
            os = FileUtils.openOutputStream(destFile);
            IOUtils.copy(is, os);
            return destFile;
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    public boolean removeBundle(Bundle bundle) throws IOException {
        URL location = new URL(bundle.getLocation());

        File bundleFile = FileUtils.toFile(location);

        return FileUtils.deleteQuietly(bundleFile);
    }

    public Collection<File> retrieveAllFiles() {
        return FileUtils.listFiles(new File(bundleDir), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    }
}
