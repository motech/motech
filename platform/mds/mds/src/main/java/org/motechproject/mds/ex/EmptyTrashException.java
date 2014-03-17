package org.motechproject.mds.ex;

/**
 * The <code>EmptyTrashException</code> exception signals a situation that there were some
 * problems with cleaning the module trash.
 */
public class EmptyTrashException extends MdsException {
    private static final long serialVersionUID = -2828421402322775000L;

    /**
     * Constructs a new EmptyTrashException with <i>mds.error.emptyTrashException</i> as
     * a message key.
     *
     * @param cause the cause of exception.
     */
    public EmptyTrashException(Throwable cause) {
        super("mds.error.emptyTrashException", cause);
    }
}
