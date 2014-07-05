package org.motechproject.hub.model;

public enum DistributionStatusLookup {

    SUCCESS(1, "success"), FAILURE(2, "failure");

    private final String status;
    private final int id;

    public int getId() {
        return id;
    }

    private DistributionStatusLookup(int id, String status) {
        this.status = status;
        this.id = id;
    }

    @Override
    public String toString() {
        return this.status;
    }

}
