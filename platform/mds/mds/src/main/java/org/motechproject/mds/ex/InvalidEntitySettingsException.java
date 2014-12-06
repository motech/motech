package org.motechproject.mds.ex;

/**
 * Signals that there were problems with the relation in the data model, due to incorrect
 * entity settings.
 */
public class InvalidEntitySettingsException extends RuntimeException {

    private static final long serialVersionUID = 7537641564252815662L;

    public InvalidEntitySettingsException(String message) {
        super(message + "\nRelated entity must not have more strict history tracking settings. In case of bi-directional " +
                "relationships, both entities must have the same history tracking settings. Fix your data " +
                "model by setting correct history tracking option for related entities.");
    }
}
