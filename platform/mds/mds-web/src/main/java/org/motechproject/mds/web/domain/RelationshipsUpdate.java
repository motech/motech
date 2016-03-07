package org.motechproject.mds.web.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the related instances, that have been added or removed on the UI.
 */
public class RelationshipsUpdate {

    private List<Long> removedIds;
    private List<Long> addedIds;
    private List<EntityRecord> addedNewRecords;

    public RelationshipsUpdate() {
        this.removedIds = new ArrayList<>();
        this.addedIds = new ArrayList<>();
        this.addedNewRecords = new ArrayList<>();
    }

    public List<Long> getRemovedIds() {
        return removedIds;
    }

    public void setRemovedIds(List<Long> removedIds) {
        this.removedIds = removedIds;
    }

    public List<Long> getAddedIds() {
        return addedIds;
    }

    public void setAddedIds(List<Long> addedIds) {
        this.addedIds = addedIds;
    }

    public List<EntityRecord> getAddedNewRecords() {
        return addedNewRecords;
    }

    public void setAddedNewRecords(List<EntityRecord> addedNewRecords) {
        this.addedNewRecords = addedNewRecords;
    }
}
