package org.motechproject.appointments.api.service;

import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.appointments.api.contract.VisitsQuery;
import org.motechproject.appointments.api.dao.AllAppointmentCalendars;
import org.motechproject.appointments.api.mapper.VisitResponseMapper;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Criterion;
import org.motechproject.appointments.api.model.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VisitsQueryService {

    @Autowired
    AllAppointmentCalendars allAppointmentCalendars;

    private VisitResponseMapper visitResponseMapper = new VisitResponseMapper();

    public List<VisitResponse> search(VisitsQuery query) {
        List<AppointmentCalendar> appointmentCalendars = new ArrayList<AppointmentCalendar>();
        if (query.hasExternalIdCriterion()) {
            AppointmentCalendar appointmentCalendar = allAppointmentCalendars.findByExternalId(query.getExternalIdCriterion());
            if (appointmentCalendar != null)
                appointmentCalendars.add(appointmentCalendar);
        } else {
            appointmentCalendars = allAppointmentCalendars.getAll();
        }

        Map<Visit, String> externalIdFromVisit = new HashMap<Visit, String>();
        List<Visit> visits = new ArrayList<Visit>();
        for (AppointmentCalendar calendar : appointmentCalendars) {
            List<Visit> calendarVisits = calendar.visits();
            visits.addAll(calendarVisits);
            for (Visit visit : calendarVisits)
                externalIdFromVisit.put(visit, calendar.externalId());  // visit must be uniquely identifiable!
        }

        for (Criterion criterion : query.getCriteria())
            visits = criterion.filter(visits);

        return mapVisitsToVisitResponses(visits, externalIdFromVisit);
    }

    private List<VisitResponse> mapVisitsToVisitResponses(List<Visit> visits, Map<Visit, String> externalIdFromVisit) {
        List<VisitResponse> visitResponses = new ArrayList<VisitResponse>();
        for (Visit visit : visits) {
            VisitResponse visitResponse = visitResponseMapper.map(visit);
            visitResponse.setExternalId(externalIdFromVisit.get(visit));
            visitResponses.add(visitResponse);
        }
        return visitResponses;
    }
}
