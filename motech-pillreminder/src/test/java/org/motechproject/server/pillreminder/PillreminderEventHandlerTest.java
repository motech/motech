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
package org.motechproject.server.pillreminder;


import static org.mockito.Mockito.*;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.gateway.MotechSchedulerGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.model.Time;
import org.motechproject.pillreminder.api.EventKeys;
import org.motechproject.pillreminder.api.PillReminderService;
import org.motechproject.pillreminder.api.model.PillReminder;
import org.motechproject.pillreminder.api.model.Schedule;

/**
 * @author yyonkov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PillReminderEventHandlerTest {
	
	private static final String PILLREMINDER_ID = "001";

	@InjectMocks
	PillReminderEventHandler pillreminderEventHandler = new PillReminderEventHandler();
	
    @Mock
    private MotechSchedulerGateway schedulerGateway;
    
    @Mock
    private PillReminderService pillReminderService;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PillReminder reminder = new PillReminder();
        reminder.setId(PILLREMINDER_ID);		
		reminder.setStartDate(new DateTime(2011, 3, 1, 0, 0, 0, 0).toDate());
		reminder.setEndDate(new DateTime(2011, 3, 31, 0, 0, 0, 0).toDate());
		
		Schedule schedule = new Schedule();
		schedule.setStartCallTime(new Time(7,0));
		schedule.setEndCallTime(new Time(9,0));
		schedule.setRepeatCount(5);
		schedule.setRepeatInterval(5*60);		
		reminder.getSchedules().add(schedule);
		
		schedule = new Schedule();

		schedule.setStartCallTime(new Time(11,0));
		schedule.setEndCallTime(new Time(13,0));
		schedule.setRepeatCount(5);
		schedule.setRepeatInterval(5*60);
		reminder.getSchedules().add(schedule);
		
		when(pillReminderService.getPillReminder(PILLREMINDER_ID)).thenReturn(reminder);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSchedulePillReminder() {
		MotechEvent event = new MotechEvent(EventKeys.PILLREMINDER_CREATED_SUBJECT);
		event.getParameters().put(EventKeys.PILLREMINDER_ID_KEY, PILLREMINDER_ID);
		pillreminderEventHandler.schedulePillReminder(event);
		verify(schedulerGateway, times(60)).scheduleRepeatingJob(any(RepeatingSchedulableJob.class));
	}
}
