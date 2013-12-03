package org.motechproject.config.core.filestore;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang.ArrayUtils;

import java.io.File;

import static org.motechproject.config.core.constants.ConfigurationConstants.SUPPORTED_FILE_EXTNS;

/**
 * FileFilter implementation to filter configuration files.
 */
public class ConfigFileFilter extends FileFileFilter {

    @Override
    public boolean accept(File file) {
        return isFileSupported(file);
    }

    public static boolean isFileSupported(File file) {
        if (!file.getParentFile().getName().startsWith("org.motechproject.")) {
            return false;
        }
        if (!ArrayUtils.contains(SUPPORTED_FILE_EXTNS, FilenameUtils.getExtension(file.getName()))) {
            return false;
        }
        return true;
    }
}
