package org.motechproject.scheduletracking.api.service.impl;

import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.events.DefaultmentCaptureEvent;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.Defaulted;
import static org.motechproject.scheduletracking.api.events.constants.EventSubjects.DEFAULTMENT_CAPTURE;

@Component
public class EndOfMilestoneListener {
    private AllEnrollments allEnrollments;

    @Autowired
    public EndOfMilestoneListener(AllEnrollments allEnrollments) {
        this.allEnrollments = allEnrollments;
    }

    @MotechListener(subjects = DEFAULTMENT_CAPTURE)
    public void handle(MotechEvent motechEvent) {
        DefaultmentCaptureEvent event = new DefaultmentCaptureEvent(motechEvent);
        Enrollment enrollment = allEnrollments.get(event.getEnrollmentId());
        enrollment.setStatus(Defaulted);
        allEnrollments.update(enrollment);
    }
}
