package org.motechproject.scheduletracking.api.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.MutablePeriod;
import org.joda.time.Period;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.motechproject.commons.date.util.DateUtil.now;

@TypeDiscriminator("doc.type === 'Schedule'")
public class Schedule extends MotechBaseDataObject implements Serializable {
    private static final long serialVersionUID = 5289540619417160322L;
    @JsonProperty
    private String name;
    @JsonProperty
    private List<Milestone> milestones = new ArrayList<>();
    @JsonProperty
    private boolean isBasedOnAbsoluteWindows;

    private Schedule() {
    }

    public Schedule(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    public void addMilestones(Milestone... milestonesList) {
        milestones.addAll(Arrays.asList(milestonesList));
    }

    @JsonIgnore
    public Milestone getFirstMilestone() {
        return milestones.get(0);
    }

    @JsonIgnore
    public List<Milestone> getMilestones() {
        return milestones;
    }

    @JsonIgnore
    public Milestone getMilestone(String milestoneName) {
        for (Milestone milestone : milestones) {
            if (milestone.getName().equals(milestoneName)) {
                return milestone;
            }
        }
        return null;
    }

    @JsonIgnore
    public String getNextMilestoneName(String currentMilestoneName) {
        int currentIndex = milestones.indexOf(getMilestone(currentMilestoneName));
        if (currentIndex < milestones.size() - 1) {
            return milestones.get(currentIndex + 1).getName();
        }
        return null;
    }

    @JsonIgnore
    public Period getDuration() {
        MutablePeriod duration = new MutablePeriod();
        for (Milestone milestone : milestones) {
            duration.add(milestone.getMaximumDuration());
        }
        return duration.toPeriod();
    }

    public boolean hasExpiredSince(DateTime referenceDateTime, String currentMilestoneStr) {
        Milestone currentMilestone = getMilestone(currentMilestoneStr);
        return referenceDateTime.plus(currentMilestone.getMaximumDuration()).isBefore(now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Schedule schedule = (Schedule) o;

        if (name != null ? !name.equals(schedule.name) : schedule.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @JsonIgnore
    public Schedule isBasedOnAbsoluteWindows(boolean value) {
        this.isBasedOnAbsoluteWindows = value;
        return this;
    }

    @JsonIgnore
    public boolean isBasedOnAbsoluteWindows() {
        return this.isBasedOnAbsoluteWindows;
    }

    public Schedule merge(Schedule schedule) {
        this.milestones = schedule.milestones;
        this.isBasedOnAbsoluteWindows = schedule.isBasedOnAbsoluteWindows;
        return this;
    }
}
