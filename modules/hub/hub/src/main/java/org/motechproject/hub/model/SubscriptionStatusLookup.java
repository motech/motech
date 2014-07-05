package org.motechproject.hub.model;

public enum SubscriptionStatusLookup {

    ACCEPTED(1, "accepted"), 
    INTENT_FAILED(2, "intent_failed"), 
    INTENT_VERIFIED(3, "intent_verified");

    private final String status;
    private final Integer id;

    public Integer getId() {
        return id;
    }

    private SubscriptionStatusLookup(Integer id, String status) {
        this.status = status;
        this.id = id;
    }

    @Override
    public String toString() {
        return this.status;
    }

}
