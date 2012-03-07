package org.motechproject.scheduletracking.api.service;

import org.joda.time.DateTime;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.domain.exception.InvalidQueryException;
import org.motechproject.scheduletracking.api.domain.filtering.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class EnrollmentsQuery {

    private List<Criterion> criteria = new ArrayList<Criterion>();

    public EnrollmentsQuery() {
    }

    public EnrollmentsQuery havingExternalId(String externalId) {
        criteria.add(new ExternalIdCriterion(externalId));
        return this;
    }

    public EnrollmentsQuery havingSchedule(String scheduleName) {
        criteria.add(new ScheduleCriterion(scheduleName));
        return this;
    }

    public EnrollmentsQuery havingWindowStartingDuring(WindowName windowName, DateTime start, DateTime end) {
        criteria.add(new StartOfWindowCriterion(windowName, start, end));
        return this;
    }

    public EnrollmentsQuery havingWindowEndingDuring(WindowName windowName, DateTime start, DateTime end) {
        criteria.add(new EndOfWindowCriterion(windowName, start, end));
        return this;
    }

    public EnrollmentsQuery currentlyInWindow(WindowName... windowNames) {
        criteria.add(new InWindowCriterion(asList(windowNames)));
        return this;
    }

    public EnrollmentsQuery havingState(String... states) {
        criteria.add(new StatusCriterion(toEnum(asList(states))));
        return this;
    }

    public EnrollmentsQuery completedDuring(DateTime start, DateTime end) {
        criteria.add(new CompletedDuringCriterion(start, end));
        return this;
    }

    private List<EnrollmentStatus> toEnum(List<String> values) {
        List<EnrollmentStatus> statuses = new ArrayList<EnrollmentStatus>();
        for (String value : values) {
            try {
                statuses.add(EnrollmentStatus.valueOf(value.toUpperCase()));
            } catch (Exception e) {
                throw new InvalidQueryException("Invalid enrollment status: " + value);
            }
        }
        return statuses;
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }
}
