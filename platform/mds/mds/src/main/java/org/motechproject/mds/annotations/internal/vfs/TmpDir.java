package org.motechproject.mds.annotations.internal.vfs;

import org.apache.commons.io.FileUtils;
import org.reflections.vfs.Vfs;
import org.reflections.vfs.ZipDir;

import java.io.File;
import java.io.IOException;

/**
 * Encapsulates {@link org.reflections.vfs.ZipDir} created from a temporary file.
 * The file is created when the directory is closed.
 */
public class TmpDir implements Vfs.Dir {

    private File tmpDirFile;
    private ZipDir zipDir;

    public TmpDir(File tmpDirFile) throws IOException {
        this.tmpDirFile = tmpDirFile;
        this.zipDir = new ZipDir(FileUtils.toURLs(new File[]{tmpDirFile})[0]);
    }

    @Override
    public String getPath() {
        return zipDir.getPath();
    }

    @Override
    public Iterable<Vfs.File> getFiles() {
        return zipDir.getFiles();
    }

    @Override
    public void close() {
        zipDir.close();
        FileUtils.deleteQuietly(tmpDirFile);
    }
}
