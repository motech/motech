package org.motechproject.model;

/**
 *
 */
public class VoiceMessageType {

    private String voiceMessageTypeName;
    private MessagePriority priority;
    private String vXmlUrl;
    private boolean canBeSaved; // indicates if this type of messages allowed to be saved by patients in they voice outbox
}
