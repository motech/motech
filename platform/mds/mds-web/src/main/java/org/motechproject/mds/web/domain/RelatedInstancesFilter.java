package org.motechproject.mds.web.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the related instances, that have been added or removed on the UI.
 */
public class RelatedInstancesFilter {

    private List<Long> removedIds;
    private List<Long> addedIds;
    private List<EntityRecord> addedRecords;

    public RelatedInstancesFilter() {
        this.removedIds = new ArrayList<>();
        this.addedIds = new ArrayList<>();
        this.addedRecords = new ArrayList<>();
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

    public List<EntityRecord> getAddedRecords() {
        return addedRecords;
    }

    public void setAddedRecords(List<EntityRecord> addedRecords) {
        this.addedRecords = addedRecords;
    }
}
