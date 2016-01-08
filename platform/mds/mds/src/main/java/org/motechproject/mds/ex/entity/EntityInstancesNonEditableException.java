package org.motechproject.mds.ex.entity;

import org.motechproject.mds.ex.MdsException;

/**
 * The <code>EntityInstancesNonEditableException</code> exception signals a situation in which an user
 * try to edit an instance from nonEditable Entity.
 */
public class EntityInstancesNonEditableException extends MdsException {
    private static final long serialVersionUID = -7816428477739342897L;

    public EntityInstancesNonEditableException() {
        super("mds.error.entityIsReadOnly");
    }
}
