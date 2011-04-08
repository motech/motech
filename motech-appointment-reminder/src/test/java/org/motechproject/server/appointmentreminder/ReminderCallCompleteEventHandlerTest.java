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
package org.motechproject.server.appointmentreminder;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.metrics.MetricsAgent;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.appointmentreminder.service.AppointmentReminderService;
import org.springframework.test.context.ContextConfiguration;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml" })
public class ReminderCallCompleteEventHandlerTest
{

    @InjectMocks
    ReminderCallCompleteEventHandler handler = new ReminderCallCompleteEventHandler();

    @Mock
    private AppointmentReminderService appointmentReminderService;

    @Mock
    private MetricsAgent metricsAgent;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
     }

    @Test
    public void testHandle() throws Exception {

        String appointmentId = "1a";
        Date callDate = DateUtils.truncate(new Date(), Calendar.DATE);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, appointmentId);
        params.put(EventKeys.CALL_DATE_KEY, callDate);

        MotechEvent motechEvent = new MotechEvent("", "", params);

        handler.handle(motechEvent);

        verify(appointmentReminderService, times(1)).reminderCallCompleted(appointmentId, callDate);

    }

    @Test
    public void testHandle_InvalidAppointmentIdType() throws Exception {
        Date callDate = DateUtils.truncate(new Date(), Calendar.DATE);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, new Integer(0));
        params.put(EventKeys.CALL_DATE_KEY, callDate);

        MotechEvent motechEvent = new MotechEvent("", "", params);
        boolean exceptionThrown = false;

        try {
            handler.handle(motechEvent);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
        verify(appointmentReminderService, times(0)).reminderCallCompleted(anyString(), Matchers.<Date>anyObject());
    }

    @Test
    public void testHandle_NoAppointmentId() throws Exception {
        Date callDate = DateUtils.truncate(new Date(), Calendar.DATE);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.CALL_DATE_KEY, callDate);
        boolean exceptionThrown = false;

        MotechEvent motechEvent = new MotechEvent("", "", params);

        try {
            handler.handle(motechEvent);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);

        verify(appointmentReminderService, times(0)).reminderCallCompleted(anyString(), Matchers.<Date>anyObject());

    }

        @Test
    public void testHandle_InvalidCallDateType() throws Exception {
        String appointmentId = "1a";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, appointmentId);
        params.put(EventKeys.CALL_DATE_KEY, "Date");

        MotechEvent motechEvent = new MotechEvent("", "", params);
        boolean exceptionThrown = false;

        try {
            handler.handle(motechEvent);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
        verify(appointmentReminderService, times(0)).reminderCallCompleted(anyString(), Matchers.<Date>anyObject());
    }

    @Test
    public void testHandle_NoCallDate() throws Exception {
        String appointmentId = "1a";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventKeys.APPOINTMENT_ID_KEY, appointmentId);
        boolean exceptionThrown = false;

        MotechEvent motechEvent = new MotechEvent("", "", params);

        try {
            handler.handle(motechEvent);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);

        verify(appointmentReminderService, times(0)).reminderCallCompleted(anyString(), Matchers.<Date>anyObject());
    }

}
