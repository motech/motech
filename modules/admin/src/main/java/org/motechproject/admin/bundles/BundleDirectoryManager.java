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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

/**
 * This class is responsible for saving/removing bundle files. Bundles are currently stored in a specified
 * directory. Permanently uninstalling a bundle requires also its removal through this class. It is also responsible
 * for  saving new bundles coming either as a {@link MultipartFile}(UI) or an {@link InputStream}.
 */
public class BundleDirectoryManager {

    private String bundleDir;

    /**
     * Returns the directory used to store Motech bundles
     * @return the bundle directory
     */
    public String getBundleDir() {
        return bundleDir;
    }

    /**
     * Changes the directory used to store Motech bundles.
     * @param bundleDir the directory which is to be used for storing bundles.
     */
    public void setBundleDir(String bundleDir) {
        this.bundleDir = bundleDir;
        if (!this.bundleDir.endsWith(File.separator)) {
            this.bundleDir += File.separator;
        }
    }

    /**
     * Saves a bundle from the given MultipartFile. The bundle is placed in the specified bundle directory.
     * The {@code MultipartFile} objects usually come from the UI(uploaded bundles).
     *
     * @param multipartFile the file representing the bundle.
     * @return the {@link File} created in the filesystem.
     * @throws IOException if it was unable to save the file.
     * @see #getBundleDir()
     * @see #setBundleDir(String)
     */
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

    /**
     * Saves a bundle from the given {@link InputStream}. The bundle is placed in the specified bundle directory.
     * Since the inputstream does not contain a name, the name of the resultant file must also be provided.
     *
     * @param destFileName the name of destination file to which the bundle will be saved.
     * @param in the {@link InputStream} containing the bundle
     * @return the {@link File} created in the filesystem.
     * @throws IOException if it was unable to save the file.
     * @see #getBundleDir()
     * @see #setBundleDir(String)
     */
    public File saveBundleStreamToFile(String destFileName, InputStream in) throws IOException {
        File destFile = new File(bundleDir + destFileName);
        OutputStream os = null;
        try {
            os = FileUtils.openOutputStream(destFile);
            IOUtils.copy(in, os);
            return destFile;
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    /**
     * Removes the given {@link Bundle} from the filesystem. The file to be removed is determined based on
     * the return value of {@link org.osgi.framework.Bundle#getLocation()} from the passed bundle.
     *
     * @param bundle the {@link Bundle} to be removed from the filesystem.
     * @return true if the bundle was removed, false otherwise.
     * @throws MalformedURLException if it was unable to create an {@link URL} to the bundle's location.
     */
    public boolean removeBundle(Bundle bundle) throws MalformedURLException {
        URL location = new URL(bundle.getLocation());

        File bundleFile = FileUtils.toFile(location);

        return FileUtils.deleteQuietly(bundleFile);
    }

    /**
     * Retrieves all {@link File}s from the currently set bundle directory.
     *
     * @return a {@link Collection} of {@link File}s from the currently set bundle directory.
     * @see #getBundleDir()
     * @see #setBundleDir(String)
     */
    public Collection<File> retrieveAllFiles() {
        return FileUtils.listFiles(new File(bundleDir), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    }
}
