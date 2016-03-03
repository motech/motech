package org.motechproject.mds.annotations.internal.vfs;

import org.apache.commons.io.IOUtils;
import org.reflections.vfs.Vfs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Url type used by reflections. Use pax.url in order to resolve urls starting
 * with the protocol equal to {@code mvn}. Underneath, it creates a temporary file handled by
 * {@link org.motechproject.mds.annotations.internal.vfs.TmpDir}.
 */
public class MvnUrlType implements Vfs.UrlType {
    @Override
    public boolean matches(URL url) {
        return "mvn".equals(url.getProtocol());
    }

    @Override
    public Vfs.Dir createDir(URL url) {
        try (InputStream in = url.openStream()) {
            //copy to a temporary file
            File tmpFile = File.createTempFile("vfs-mvn-bundle", ".jar");
            try (OutputStream out = new FileOutputStream(tmpFile)) {
                IOUtils.copy(in, out);
            }
            // return a dir implementation which removes the file when its done
            return new TmpDir(tmpFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to create mvn url for " + url, e);
        }
    }
}
