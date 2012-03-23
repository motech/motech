package org.motechproject.appointments.api.model;

import ch.lambdaj.Lambda;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

public class UnvisitedCriterion implements Criterion {

    @Override
    public List<Visit> filter(List<Visit> visits) {
        return Lambda.filter(having(on(Visit.class).visitDate(), equalTo(null)), visits);
    }
}
