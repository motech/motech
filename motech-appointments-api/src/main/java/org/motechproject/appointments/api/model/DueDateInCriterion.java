package org.motechproject.appointments.api.model;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.util.DateUtil.inRange;

public class DueDateInCriterion implements Criterion {

    private DateTime start;
    private DateTime end;

    public DueDateInCriterion(DateTime start, DateTime end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public List<Visit> filter(List<Visit> visits) {
        List<Visit> filteredVisits = new ArrayList<Visit>();
        for (Visit visit : visits) {
            DateTime dueDate = visit.appointment().dueDate();
            if (inRange(dueDate, start, end))
                filteredVisits.add(visit);
        }
        return filteredVisits;
    }
}
