package org.motechproject.mds.ex;

/**
 * The <code>MdsException</code> exception is a basic class for all other exceptions defined
 * in the mds module. It contains information about a message key which will be used on UI to
 * present a message in appropriate language.
 */
public class MdsException extends RuntimeException {
    private static final long serialVersionUID = -9150234804159903178L;

    private String messageKey;
    private String params;

    /**
     * Constructs a new mds exception with the specified message key.
     *
     * @param messageKey the message key used later to display message in appropriate
     *                   language on UI.
     */
    public MdsException(String messageKey) {
        this(messageKey, null, null);
    }



    /**
     * Constructs a new mds exception with the specified message key and params.
     *
     * @param messageKey the message key used later to display message in appropriate
     *                   language on UI.
     * @param params     the params used later to change placeholders in the message
     */
    public MdsException(String messageKey, String params) {
        this(messageKey, params, null);
    }

    /**
     * Constructs a new mds exception with the specified message key and the specified cause.
     *
     * @param messageKey the message key used later to display message in appropriate
     *                   language on UI.
     * @param cause      the cause of exception.
     */
    public MdsException(String messageKey, Throwable cause) {
        this(messageKey, null, cause);
    }

    /**
     * Constructs a new mds exception with the specified message key and the specified cause.
     *
     * @param messageKey the message key used later to display message in appropriate
     *                   language on UI.
     * @param params     the params used later to change placeholders in the message
     * @param cause      the cause of exception.
     */
    public MdsException(String messageKey, String params, Throwable cause) {
        super(cause);
        this.messageKey = messageKey;
        this.params = params;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getParams() {
        return params;
    }
}
