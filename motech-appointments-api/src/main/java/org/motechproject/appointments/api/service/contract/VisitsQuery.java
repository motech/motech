package org.motechproject.appointments.api.service.contract;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.model.search.*;

import java.util.ArrayList;
import java.util.List;

public class VisitsQuery {
    private List<Criterion> criteria = new ArrayList<Criterion>();

    public VisitsQuery withDueDateIn(DateTime start, DateTime end) {
        criteria.add(new DueDateInCriterion(start, end));
        return this;
    }

    public VisitsQuery unvisited() {
        criteria.add(new UnvisitedCriterion());
        return this;
    }

    public VisitsQuery havingExternalId(String externalId) {
        criteria.add(new ExternalIdCriterion(externalId));
        return this;
    }

    /** Builds query to filter visits by given metadata key value pair
     * @param property  Property of the metadata
     * @param value     Value of the property to be matched
     * @return currently built query
     */
    public VisitsQuery havingMetadata(String property, String value) {
        criteria.add(new MetadataCriterion(property, value));
        return this;
    }

    public List<Criterion> getCriteria() {
        return new ArrayList<Criterion>(criteria);
    }
}
