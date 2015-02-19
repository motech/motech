package org.motechproject.mds.ex.field;

/**
 * The <code>FieldReadOnlyException</code> exception signals an attempt to edit read only field.
 */
public class FieldReadOnlyException extends RuntimeException {

    private static final long serialVersionUID = 8964737006637613242L;

    public FieldReadOnlyException(String message) {
        super(message);
    }
}
