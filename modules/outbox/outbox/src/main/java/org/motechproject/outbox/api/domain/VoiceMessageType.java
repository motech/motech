package org.motechproject.outbox.api.domain;

/**
 * Identifies the type of the message.
 */
public class VoiceMessageType {
    private String voiceMessageTypeName;
    private String templateName;
    private boolean canBeSaved;
    private boolean canBeReplayed;

    /**
     * Identifies the type of the message.
     *
     * @return a String specifying the type of the message.
     */
    public String getVoiceMessageTypeName() {
        return voiceMessageTypeName;
    }

    /**
     * Property to categorize the message
     *
     * @param voiceMessageTypeName a string specifying the category/type of the message
     */
    public void setVoiceMessageTypeName(String voiceMessageTypeName) {
        this.voiceMessageTypeName = voiceMessageTypeName;
    }

    /**
     * Identifies the template to be used to build the message.
     *
     * @return a String identifying the template
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Name of the template to be used to build this message.
     *
     * @param templateName a string specifying the template
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * Indicates if the message can be saved or not in the party's outbox
     *
     * @return a boolean
     */
    public boolean isCanBeSaved() {
        return canBeSaved;
    }

    /**
     * This property will let messages to be saved by the party in their outbox
     *
     * @param canBeSaved a boolean specifying that the message can be saved
     */
    public void setCanBeSaved(boolean canBeSaved) {
        this.canBeSaved = canBeSaved;
    }

    /**
     * Indicates if the message can be replayed by the party or not.
     *
     * @return a boolean
     */
    public boolean isCanBeReplayed() {
        return canBeReplayed;
    }

    /**
     * This property will let the messages to be replayed after the message has been played once
     *
     * @param canBeReplayed a boolean specifying that the message can be replayed
     */
    public void setCanBeReplayed(boolean canBeReplayed) {
        this.canBeReplayed = canBeReplayed;
    }


    @Override
    public String toString() {
        return "VoiceMessageType{" +
                "voiceMessageTypeName='" + voiceMessageTypeName + '\'' +
                ", templateName='" + templateName + '\'' +
                ", canBeSaved=" + canBeSaved +
                ", canBeReplayed=" + canBeReplayed +
                '}';
    }
}
