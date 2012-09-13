package org.motechproject.appointments.api.mapper;

import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.springframework.stereotype.Component;

@Component
public class VisitResponseMapper {

    public VisitResponse map(Visit visit) {
        return new VisitResponse().setName(visit.name())
                                  .setTypeOfVisit(visit.typeOfVisit())
                                  .setVisitDate(visit.visitDate())
                                  .setMissed(visit.missed())
                                  .setVisitData(visit.getData())
                                  .setOriginalAppointmentDueDate(visit.appointment().originalDueDate())
                                  .setAppointmentDueDate(visit.appointment().dueDate())
                                  .setAppointmentConfirmDate(visit.appointment().confirmedDate());
    }
}
