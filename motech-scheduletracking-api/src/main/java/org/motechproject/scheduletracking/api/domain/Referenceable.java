package org.motechproject.scheduletracking.api.domain;

public abstract class Referenceable {
    private Milestone next;

    public Referenceable(Milestone next) {
        this.next = next;
    }

    protected Milestone getNext() {
        return next;
    }
}
