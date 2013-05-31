package org.motechproject.email.model;

public class Mail {
    private String fromAddress;
    private String toAddress;
    private String subject;
    private String text;

    public Mail(String fromAddress, String toAddress, String subject, String text) {
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.subject = subject;
        this.text = text;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }
}
