package org.motechproject.mds.exception.action;

import org.motechproject.mds.exception.MdsException;

/**
 * The <code>ActionHandlerException</code> exception signals a situation in which there were some
 * problems with executing action in <code>ActionHandlerService</code>.
 *
 * @see org.motechproject.mds.service.ActionHandlerService
 */
public class ActionHandlerException extends MdsException {

    private static final long serialVersionUID = 3133537786275424601L;

    public ActionHandlerException(String message) {
        super(message);
    }

    public ActionHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
