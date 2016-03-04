package org.motechproject.mds.exception.entity;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals that there were problems with the relations in the data model.
 */
public class InvalidRelationshipException extends MdsException {

    private static final long serialVersionUID = -6881864414053573403L;

    /**
     * @param relatedClass the name of the related class
     * @param fieldClassName the class name of the field
     */
    public InvalidRelationshipException(String relatedClass, String fieldClassName) {
        super("Invalid relationship found between entities: " + relatedClass + " and " + fieldClassName + "." +
                "\nUnresolvable, circular relationship has been detected between entities. This means that your " +
                "data model contains an error. Please check the mentioned entities and fix the problem. " +
                "If you have meant to create a bi-directional relationship, please use the @javax.jdo.annotations.Persistent " +
                "annotation, with the mappedBy parameter, on exactly ONE related field.");
    }
}
