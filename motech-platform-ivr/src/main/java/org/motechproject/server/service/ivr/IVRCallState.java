package org.motechproject.server.service.ivr;

public enum IVRCallState {
    AUTH_SUCCESS,
    COLLECT_PIN;

    public boolean isCollectPin() {
        return this.equals(COLLECT_PIN);
    }
}
