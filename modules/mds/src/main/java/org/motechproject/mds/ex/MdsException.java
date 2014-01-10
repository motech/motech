package org.motechproject.mds.ex;

/**
 * The <code>MdsException</code> exception is a basic class for all other exceptions defined
 * in the mds module. It contains information about a message key which will be used on UI to
 * present a message in appropriate language.
 */
public class MdsException extends RuntimeException {
    private static final long serialVersionUID = -9150234804159903178L;

    private String messageKey;

    /**
     * Constructs a new mds exception with the specified message key.
     *
     * @param messageKey the message key used later to display message in appropriate
     *                   language on UI.
     */
    public MdsException(String messageKey) {
        this(messageKey, null);
    }

    /**
     * Constructs a new mds exception with the specified message key and the specified cause.
     *
     * @param messageKey the message key used later to display message in appropriate
     *                   language on UI.
     * @param cause      the cause of exception.
     */
    public MdsException(String messageKey, Throwable cause) {
        super(cause);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
