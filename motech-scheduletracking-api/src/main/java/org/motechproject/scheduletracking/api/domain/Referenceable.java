package org.motechproject.scheduletracking.api.domain;

public abstract class Referenceable {
    private Referenceable next;

    public Referenceable(Referenceable next) {
        this.next = next;
    }

    protected Referenceable getNext() {
        return next;
    }
}
