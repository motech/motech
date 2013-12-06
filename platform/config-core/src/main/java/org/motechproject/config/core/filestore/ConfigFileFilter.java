package org.motechproject.config.core.filestore;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang.ArrayUtils;
import org.motechproject.config.core.constants.ConfigurationConstants;

import java.io.File;

import static org.motechproject.config.core.constants.ConfigurationConstants.CONFIG_MODULE_DIR_PREFIX;
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
        return isPlatformCoreConfigFile(file) || isModuleConfigFile(file);
    }

    private static boolean isModuleConfigFile(File file) {
        if (file == null) {
            return false;
        }
        return (ArrayUtils.contains(SUPPORTED_FILE_EXTNS, FilenameUtils.getExtension(file.getName()))
                && file.getParentFile().getName().startsWith(CONFIG_MODULE_DIR_PREFIX));
    }

    public static boolean isPlatformCoreConfigFile(File file) {
        if (file == null) {
            return false;
        }
        return (file.getName().equals(ConfigurationConstants.SETTINGS_FILE_NAME) &&
                !file.getParentFile().getName().startsWith(CONFIG_MODULE_DIR_PREFIX));
    }
}
