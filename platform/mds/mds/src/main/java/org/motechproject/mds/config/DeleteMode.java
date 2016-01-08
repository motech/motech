package org.motechproject.mds.config;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * The <code>DeleteMode</code> presents what should happen with objects when they are deleted.
 * They can be deleted permanently {@link #DELETE} or moved to the trash {@link #TRASH}. This
 * enum is related with the property
 * {@link org.motechproject.mds.util.Constants.Config#MDS_DELETE_MODE}.
 * <p/>
 * The {@link #UNKNOWN} value should not be used in code as appropriate value. It was added to
 * ensure that the {@link #fromString(String)} method will not return {@value null} value.
 */
public enum DeleteMode {
    DELETE, TRASH, UNKNOWN;

    /**
     * Converts the given string to appropriate delete mode. This method will never return
     * {@value null} value. If the appropriate mode doesn't exists then the {@link #UNKNOWN} mode
     * will be returned.
     *
     * @param string the string representation of the delede mode.
     * @return the appropriate delete mode if exists; otherwize {@link #UNKNOWN}
     */
    public static DeleteMode fromString(String string) {
        DeleteMode result = UNKNOWN;

        if (isNotBlank(string)) {
            for (DeleteMode mode : DeleteMode.values()) {
                if (mode.name().equalsIgnoreCase(string)) {
                    result = mode;
                    break;
                }
            }
        }

        return result;
    }
}
