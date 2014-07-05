package org.motechproject.hub.model;

public enum Modes {

    SUBSCRIBE("subscribe"), UNSUBSCRIBE("unsubscribe"), PUBLISH("publish");

    private final String mode;

    private Modes(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return this.mode;
    }

}
