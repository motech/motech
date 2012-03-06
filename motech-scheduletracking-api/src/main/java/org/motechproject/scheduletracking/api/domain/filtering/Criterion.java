package org.motechproject.scheduletracking.api.domain.filtering;

import org.motechproject.scheduletracking.api.domain.Enrollment;

import java.util.List;

public interface Criterion  {
   List<Enrollment> filter(List<Enrollment> enrollments);
}


