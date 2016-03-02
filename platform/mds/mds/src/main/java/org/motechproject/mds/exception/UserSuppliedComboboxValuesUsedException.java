package org.motechproject.mds.exception;

/**
 * Exception indicating that user supplied value is used in an instance.
 */
public class UserSuppliedComboboxValuesUsedException extends MdsException {

    private static final long serialVersionUID = -7371335286597754778L;


    public UserSuppliedComboboxValuesUsedException(String comboboxName, String value) {
        super(String.format("\"Allow user supplied\" setting of field \"%s\" cannot be unchecked as user supplied value \"%s\" is used in one of the instances.", comboboxName, value),
                null, "mds.error.userSuppliedValueUsed", comboboxName, value);
    }
}
