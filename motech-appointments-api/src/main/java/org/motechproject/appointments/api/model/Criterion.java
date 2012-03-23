package org.motechproject.appointments.api.model;

import java.util.List;

public interface Criterion {
    List<Visit> filter(List<Visit> visits);
}
