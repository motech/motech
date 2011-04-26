/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.appointments.api.dao.impl;

import org.ektorp.CouchDbConnector;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.dao.VisitsDAO;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VisitsCouchDBDAOImpl extends MotechAuditableRepository<Visit> implements VisitsDAO
{
    @Autowired
    private EventRelay eventRelay;

    @Autowired
    public VisitsCouchDBDAOImpl(@Qualifier("appointmentsDatabase") CouchDbConnector db) {
        super(Visit.class, db);
        initStandardDesignDocument();
    }

    @Override
    public void addVisit(Visit visit)
    {
        db.create(visit);

        eventRelay.sendEventMessage(getSkinnyEvent(visit, EventKeys.VISIT_CREATED_SUBJECT));
    }

    @Override
    public void updateVisit(Visit visit)
    {
        db.update(visit);

        eventRelay.sendEventMessage(getSkinnyEvent(visit, EventKeys.VISIT_UPDATED_SUBJECT));
    }

    @Override
    public void removeVisit(String visitId)
    {
        Visit visit = getVisit(visitId);

        removeVisit(visit);
    }

    @Override
    public void removeVisit(Visit visit)
    {
        MotechEvent event = getSkinnyEvent(visit, EventKeys.VISIT_DELETED_SUBJECT);

        db.delete(visit);

        eventRelay.sendEventMessage(event);
    }

    @Override
    public Visit getVisit(String visitId)
    {
        Visit visit = db.get(Visit.class, visitId);
        return visit;
    }

    @Override
    public List<Visit> getVisitsByAppointmentId(String appointmentId)
    {
        List<Visit> ret = queryView("by_appointmentId", appointmentId);
        if (null == ret) {
            ret = Collections.<Visit>emptyList();
        }
        return ret;
    }

    @Override
    public List<Visit> getVisitsByExternalId(String externalId)
    {
        List<Visit> ret = queryView("by_externalId", externalId);
        if (null == ret) {
            ret = Collections.<Visit>emptyList();
        }
        return ret;
    }

    private MotechEvent getSkinnyEvent(Visit visit, String subject) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.APPOINTMENT_ID_KEY, visit.getAppointmentId());
        parameters.put(EventKeys.VISIT_ID_KEY, visit.getId());

        MotechEvent event = new MotechEvent(subject, parameters);

        return event;
    }
}
