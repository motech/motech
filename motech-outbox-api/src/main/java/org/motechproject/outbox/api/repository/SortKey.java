package org.motechproject.outbox.api.repository;

public enum SortKey {
    SequenceNumber, CreationTime;

    public boolean isCreationTime() {
        return this.equals(SortKey.CreationTime);
    }
}
