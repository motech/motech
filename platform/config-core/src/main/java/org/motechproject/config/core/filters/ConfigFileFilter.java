package org.motechproject.config.core.filters;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.motechproject.config.core.constants.ConfigurationConstants;

import java.io.File;

import static org.motechproject.config.core.constants.ConfigurationConstants.CONFIG_MODULE_DIR_PREFIX;
import static org.motechproject.config.core.constants.ConfigurationConstants.PROPERTIES_EXTENSION;
import static org.motechproject.config.core.constants.ConfigurationConstants.JSON_EXTENSION;
import static org.motechproject.config.core.constants.ConfigurationConstants.RAW_DIR;

/**
 * FileFilter implementation to filter configuration files.
 */
public class ConfigFileFilter extends FileFileFilter {

    public static final FileFileFilter PLATFORM_CORE_CONFIG_FILTER = new FileFileFilter() {
        @Override
        public boolean accept(File file) {
            return isPlatformCoreConfigFile(file);
        }
    };

    @Override
    public boolean accept(File file) {
        return isFileSupported(file);
    }

    /**
     * Checks whether given file is supported.
     *
     * @param file  the file to be checked
     * @return true if file is supported, else otherwise
     */
    public static boolean isFileSupported(File file) {
        return isPlatformCoreConfigFile(file) || isModuleConfigFile(file);
    }

    private static boolean isModuleConfigFile(File file) {
        if (file == null) {
            return false;
        }

        // a .json or .properties file in a org.motechproject.*/ dir?
        if ((PROPERTIES_EXTENSION.equals(FilenameUtils.getExtension(file.getName())) || JSON_EXTENSION.equals
                (FilenameUtils.getExtension(file.getName()))) &&
                file.getParentFile().getName().startsWith(CONFIG_MODULE_DIR_PREFIX)) {
            return true;
        }
        // a .json file in a org.motechproject.*/raw/ dir?
        return (JSON_EXTENSION.equals(FilenameUtils.getExtension(file.getName())) &&
                file.getParentFile().getName().equals(RAW_DIR) &&
                file.getParentFile().getParentFile().getName().startsWith(CONFIG_MODULE_DIR_PREFIX));
    }

    /**
     * Checks whether given file is platform core configuration file.
     *
     * @param file  the file to be checked, null returns false
     * @return true if file is platform core configuration, false otherwise
     */
    public static boolean isPlatformCoreConfigFile(File file) {
        if (file == null) {
            return false;
        }
        return (file.getName().equals(ConfigurationConstants.SETTINGS_FILE_NAME) &&
                !file.getParentFile().getName().startsWith(CONFIG_MODULE_DIR_PREFIX));
    }
}
