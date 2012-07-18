package org.motechproject.mobileforms.api.utils;

import org.motechproject.MotechException;

import java.io.File;

import static ch.lambdaj.Lambda.join;
import static java.util.Arrays.asList;

public class IOUtils {
    public static final String XFORMS_FOLDER = "xforms";

    public String getFileContent(String fileName, String formGroupName) {
        String xformFilePath = join(asList(XFORMS_FOLDER, formGroupName, fileName), File.separator);
        try {
            return org.apache.commons.io.IOUtils.toString(getClass().getClassLoader().getResourceAsStream(xformFilePath));
        } catch (Exception e) {
            throw new MotechException("Encountered error while loading openxdata forms", e);
        }
    }
}
