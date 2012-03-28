package org.motechproject.appointments.api.service;

import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.appointments.api.contract.VisitsQuery;
import org.motechproject.appointments.api.repository.AllAppointmentCalendars;
import org.motechproject.appointments.api.mapper.VisitResponseMapper;
import org.motechproject.appointments.api.model.search.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VisitsQueryService {

    @Autowired
    AllAppointmentCalendars allAppointmentCalendars;

    private VisitResponseMapper visitResponseMapper = new VisitResponseMapper();

    public List<VisitResponse> search(VisitsQuery query) {
        
        List<Criterion> criteria = query.getCriteria();
        if(criteria.size()==0)
            return new ArrayList<VisitResponse>();

        Criterion primaryCriterion = criteria.get(0);
        List<VisitResponse> visitResponses = primaryCriterion.fetch(allAppointmentCalendars);
        criteria.remove(primaryCriterion);

        for (Criterion criterion : query.getCriteria())
            visitResponses = criterion.filter(visitResponses);

        return visitResponses;
    }

}
