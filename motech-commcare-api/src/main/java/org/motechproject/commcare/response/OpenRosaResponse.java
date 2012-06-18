package org.motechproject.commcare.response;

public class OpenRosaResponse {

    private int status = 0;
    private String messageNature = null;
    private String messageText = null;

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
