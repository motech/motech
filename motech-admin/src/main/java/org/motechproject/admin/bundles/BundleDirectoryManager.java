package org.motechproject.admin.bundles;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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

    public File saveBundleFile(MultipartFile bundleFile) throws IOException {
        String destFileName = bundleFile.getOriginalFilename();
        File destFile = new File(bundleDir + destFileName);

        OutputStream os = null;
        InputStream is = null;
        try {
            is = bundleFile.getInputStream();
            os = FileUtils.openOutputStream(destFile);
            IOUtils.copy(is, os);
            return destFile;
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    public Collection<File> retrieveAllFiles() {
        return FileUtils.listFiles(new File(bundleDir), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    }
}
