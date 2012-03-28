package org.motechproject.appointments.api.contract;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.model.search.Criterion;
import org.motechproject.appointments.api.model.search.DueDateInCriterion;
import org.motechproject.appointments.api.model.search.ExternalIdCriterion;
import org.motechproject.appointments.api.model.search.UnvisitedCriterion;

import java.util.ArrayList;
import java.util.List;

public class VisitsQuery {

    private List<Criterion> criteria = new ArrayList<Criterion>();

    public VisitsQuery() {
    }

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

    public List<Criterion> getCriteria() {
        return criteria;
    }
}
