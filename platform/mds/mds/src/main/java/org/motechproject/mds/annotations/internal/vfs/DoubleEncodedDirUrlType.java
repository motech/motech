package org.motechproject.mds.annotations.internal.vfs;

import org.apache.commons.io.FileUtils;
import org.reflections.vfs.SystemDir;
import org.reflections.vfs.Vfs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * Fixes an issue with reflections not properly parsing directories
 * with spaces in their names on Windows and encoding urls twice. See MOTECH-1188.
 * This can possibly be removed if update the version of reflections.
 */
public class DoubleEncodedDirUrlType implements Vfs.UrlType {

    // normally space is %20, but here % gets encoded as %25
    public static final String DOUBLE_ENCODED_SPACE = "%2520";
    public static final String ENCODED_SPACE = "%20";
    public static final String FILE = "file";

    @Override
    public boolean matches(URL url) {
        return isDoubleEncodedUrl(url) && FILE.equals(url.getProtocol()) && toFile(url).isDirectory();
    }

    @Override
    public Vfs.Dir createDir(URL url) {
        URL fixedUrl = fixURL(url);
        File file = FileUtils.toFile(fixedUrl);
        return new SystemDir(file);
    }

    private boolean isDoubleEncodedUrl(URL url) {
        return url.toString().contains(DOUBLE_ENCODED_SPACE);
    }

    private File toFile(URL url) {
        try {
            return Paths.get(fixURL(url).toURI()).toFile();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Unable to parse URL to URI: " + url, e);
        }
    }

    private URL fixURL(URL url) {
        String fixedUrlStr = url.toString().replace(DOUBLE_ENCODED_SPACE, ENCODED_SPACE);
        try {
            return new URL(fixedUrlStr);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to parse URL. Before fixing double encoded spaces: " + url
                + " after: " + fixedUrlStr, e);
        }
    }
}
