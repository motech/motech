package org.motechproject.mds.exception.entity;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals that there were problems with the relation in the data model, due to incorrect
 * entity settings.
 */
public class InvalidEntitySettingsException extends MdsException {

    private static final long serialVersionUID = 7537641564252815662L;

    /**
     * @param className class name of the entity
     * @param relatedClassName the class name of the related entity
     */
    public InvalidEntitySettingsException(String className, String relatedClassName) {
        super(className + " and " + relatedClassName +
                " have invalid history tracking settings.\nRelated entity must not have more strict history tracking settings. In case of bi-directional " +
                "relationships, both entities must have the same history tracking settings. Fix your data " +
                "model by setting correct history tracking option for related entities.");
    }
}
