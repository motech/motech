package org.motechproject.scheduletracking.api.domain.search;

import org.joda.time.DateTime;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.util.DateUtil.inRange;

public class CompletedDuringCriterion implements Criterion {
    private DateTime start;
    private DateTime end;

    public CompletedDuringCriterion(DateTime start, DateTime end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public List<Enrollment> fetch(AllEnrollments allEnrollments, EnrollmentService enrollmentService) {
        return allEnrollments.completedDuring(start, end);
    }

    @Override
    public List<Enrollment> filter(List<Enrollment> enrollments, EnrollmentService enrollmentService) {
        List<Enrollment> filteredEnrollments = new ArrayList<Enrollment>();
        for (Enrollment enrollment : enrollments) {
            if (enrollment.isCompleted() && inRange(enrollment.getLastFulfilledDate(), start, end)) {
                filteredEnrollments.add(enrollment);
            }
        }
        return filteredEnrollments;
    }
}
