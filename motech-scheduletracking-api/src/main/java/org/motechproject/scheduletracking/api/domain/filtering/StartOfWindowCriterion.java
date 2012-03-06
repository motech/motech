package org.motechproject.scheduletracking.api.domain.filtering;

import org.joda.time.DateTime;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class StartOfWindowCriterion implements Criterion {

    private WindowName windowName;
    private DateTime start;
    private DateTime end;
    private EnrollmentService enrollmentService;

    public StartOfWindowCriterion(WindowName windowName, DateTime start, DateTime end, EnrollmentService enrollmentService) {
        this.windowName = windowName;
        this.start = start;
        this.end = end;
        this.enrollmentService = enrollmentService;
    }

    @Override
    public List<Enrollment> filter(List<Enrollment> enrollments) {
        List<Enrollment> filteredEnrollments = new ArrayList<Enrollment>();
        for (Enrollment enrollment : enrollments) {
            DateTime startOfWindowForCurrentMilestone = enrollmentService.getStartOfWindowForCurrentMilestone(enrollment, windowName);
            if (DateUtil.inRange(startOfWindowForCurrentMilestone, start, end))
                filteredEnrollments.add(enrollment);
        }
        return filteredEnrollments;
    }
}
