package org.motechproject.outbox.api.domain;

public enum SortKey {
    SequenceNumber, CreationTime;

    public boolean isCreationTime() {
        return this.equals(SortKey.CreationTime);
    }
}
