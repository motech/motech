package org.motechproject.scheduletracking.api.domain.search;

import org.joda.time.DateTime;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class EndOfWindowCriterion implements Criterion {
    private WindowName windowName;
    private DateTime start;
    private DateTime end;

    public EndOfWindowCriterion(WindowName windowName, DateTime start, DateTime end) {
        this.windowName = windowName;
        this.start = start;
        this.end = end;
    }

    @Override
    public List<Enrollment> fetch(AllEnrollments allEnrollments, EnrollmentService enrollmentService) {
        return filter(allEnrollments.getAll(), enrollmentService);
    }

    @Override
    public List<Enrollment> filter(List<Enrollment> enrollments, EnrollmentService enrollmentService) {
        List<Enrollment> filteredEnrollments = new ArrayList<Enrollment>();
        for (Enrollment enrollment : enrollments) {
            DateTime endOfWindowForCurrentMilestone = enrollmentService.getEndOfWindowForCurrentMilestone(enrollment, windowName);
            if (DateUtil.inRange(endOfWindowForCurrentMilestone, start, end)) {
                filteredEnrollments.add(enrollment);
            }
        }
        return filteredEnrollments;
    }
}
