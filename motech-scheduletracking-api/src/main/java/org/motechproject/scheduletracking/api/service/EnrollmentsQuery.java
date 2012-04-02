package org.motechproject.scheduletracking.api.service;

import org.joda.time.DateTime;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.domain.search.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class EnrollmentsQuery {
    private List<Criterion> criteria = new ArrayList<Criterion>();

    public EnrollmentsQuery havingExternalId(String externalId) {
        criteria.add(new ExternalIdCriterion(externalId));
        return this;
    }

    public EnrollmentsQuery havingSchedule(String scheduleName) {
        criteria.add(new ScheduleCriterion(scheduleName));
        return this;
    }

    public EnrollmentsQuery havingCurrentMilestone(String milestoneName) {
        criteria.add(new MilestoneCriterion(milestoneName));
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

    public EnrollmentsQuery havingState(EnrollmentStatus enrollmentStatus) {
        criteria.add(new StatusCriterion(enrollmentStatus));
        return this;
    }

    public EnrollmentsQuery completedDuring(DateTime start, DateTime end) {
        criteria.add(new CompletedDuringCriterion(start, end));
        return this;
    }

    public EnrollmentsQuery havingMetadata(String key, String value) {
        criteria.add(new MetadataCriterion(key, value));
        return this;
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }

    public Criterion getPrimaryCriterion() {
        return (criteria.size() > 0) ? criteria.get(0) : null;
    }

    public List<Criterion> getSecondaryCriteria() {
        return (criteria.size() > 1) ? criteria.subList(1, criteria.size()) : new ArrayList<Criterion>();
    }
}
