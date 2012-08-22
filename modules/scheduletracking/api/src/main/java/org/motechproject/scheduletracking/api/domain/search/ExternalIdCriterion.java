package org.motechproject.scheduletracking.api.domain.search;

import ch.lambdaj.Lambda;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

public class ExternalIdCriterion implements Criterion {
    private String externalId;

    public ExternalIdCriterion(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public List<Enrollment> fetch(AllEnrollments allEnrollments, EnrollmentService enrollmentService) {
        return allEnrollments.findByExternalId(externalId);
    }

    @Override
    public List<Enrollment> filter(List<Enrollment> enrollments, EnrollmentService enrollmentService) {
        return Lambda.filter(having(on(Enrollment.class).getExternalId(), equalTo(externalId)), enrollments);
    }
}
