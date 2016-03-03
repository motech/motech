package org.motechproject.mds.exception;

import org.apache.commons.lang.StringUtils;

/**
 * The <code>MdsException</code> exception is a basic class for all other exceptions defined
 * in the mds module. It contains information about a message key which will be used on UI to
 * present a message in appropriate language.
 */
public class MdsException extends RuntimeException {

    private static final long serialVersionUID = -9150234804159903178L;

    private String messageKey;
    private String params;

    public MdsException(String message) {
        super(message);
    }

    /**
     * Constructs a new mds exception with the specified message key and params.
     *
     * @param message the error message for the logs
     * @param cause the cause of the exception
     */
    public MdsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new mds exception with the specified message key and params.
     *
     * @param message the error message for the logs
     * @param cause the cause of the exception
     * @param messageKey the message key used later to display message in appropriate
     *                   language on UI.
     */
    public MdsException(String message, Throwable cause, String messageKey) {
        super(message, cause);
        this.messageKey = messageKey;
    }

    /**
     * Constructs a new mds exception with the specified message key and params.
     *
     * @param message the error message for the logs
     * @param cause the cause of the exception
     * @param messageKey the message key used later to display message in appropriate
     *                   language on UI.
     * @param params     the params used later to change placeholders in the message
     */
    public MdsException(String message, Throwable cause, String messageKey, String params) {
        super(message, cause);
        this.messageKey = messageKey;
        this.params = params;
    }

    /**
     * Constructs a new mds exception with the specified message key and params.
     *
     * @param message the error message for the logs
     * @param cause the cause of the exception
     * @param messageKey the message key used later to display message in appropriate
     *                   language on UI.
     * @param params     the params used later to change placeholders in the message
     */
    public MdsException(String message, Throwable cause, String messageKey, String... params) {
        super(message, cause);
        this.messageKey = messageKey;
        this.params = StringUtils.join(params, ',');
    }

    /**
     * @return the message key used later to display message in appropriate language on UI
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * @return the params used later to change placeholders in the message
     */
    public String getParams() {
        return params;
    }
}
