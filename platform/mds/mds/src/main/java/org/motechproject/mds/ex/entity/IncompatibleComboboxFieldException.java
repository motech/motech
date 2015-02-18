package org.motechproject.mds.ex.entity;

import org.motechproject.mds.ex.MdsException;

/**
 * Throw when one of the combobox fields is not compatible (some instances are using multiple values for that field)
 * with single-select.
 */
public class IncompatibleComboboxFieldException extends MdsException {

    public IncompatibleComboboxFieldException(String messageKey, String... params) {
        super(messageKey, params);
    }
}
