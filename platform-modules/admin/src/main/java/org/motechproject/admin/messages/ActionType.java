package org.motechproject.admin.messages;

/**
 * Represents an action which should be taken for a notification, in particular which message channel
 * should be used to communicate the notification. Currently the two channels supported are sms and email.
 */
public enum ActionType {

    SMS, EMAIL
}
