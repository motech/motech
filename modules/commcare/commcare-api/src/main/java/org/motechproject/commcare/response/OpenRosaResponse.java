package org.motechproject.commcare.response;

/**
 * An object representing the Open Rosa response from CommCareHQ when posting
 * case XML.
 */
public class OpenRosaResponse {

    private int status;
    private String messageNature;
    private String messageText;

    public String getMessageNature() {
        return messageNature;
    }

    public void setMessageNature(String messageNature) {
        this.messageNature = messageNature;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
