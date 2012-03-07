package org.motechproject.scheduletracking.api.domain.filtering;

import org.joda.time.DateTime;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;

import java.util.ArrayList;
import java.util.List;

public class InWindowCriterion implements Criterion {

    private List<WindowName> windowNames;

    public InWindowCriterion(List<WindowName> windowNames) {
        this.windowNames = windowNames;
    }

    @Override
    public List<Enrollment> filter(List<Enrollment> enrollments, EnrollmentService enrollmentService) {
        List<Enrollment> filteredEnrollments = new ArrayList<Enrollment>();
        DateTime now = DateTime.now();
        for (Enrollment enrollment : enrollments) {
            if (windowNames.contains(enrollmentService.getCurrentWindowAsOf(enrollment, now)))
                filteredEnrollments.add(enrollment);
        }
        return filteredEnrollments;
    }
}
