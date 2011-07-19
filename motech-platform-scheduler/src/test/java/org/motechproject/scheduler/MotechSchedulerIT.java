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
package org.motechproject.scheduler;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.EventListenerRegistry;
import org.motechproject.server.event.annotations.MotechListenerEventProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/testApplicationContext.xml"})

public class MotechSchedulerIT {

    @Autowired
    private MotechSchedulerService motechScheduler;

    @Autowired
    private EventListenerRegistry eventListenerRegistry;

    private long scheduledHour;
    private long scheduledMinute;
    private long scheduledSecond;

    private boolean executed;

    @Before
    public void setUp() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(1, Calendar.MINUTE);
        Date date = cal.getTime();

        long currentHour = DateUtils.getFragmentInHours(date, Calendar.DATE);
        long currentMinute = DateUtils.getFragmentInMinutes(date, Calendar.HOUR_OF_DAY);
        long currentSecond = DateUtils.getFragmentInSeconds(date, Calendar.MINUTE);

        scheduledHour = currentHour;
        scheduledMinute = currentMinute + 1;
        scheduledSecond = currentSecond;
        if (scheduledMinute == 59) {
            scheduledHour = (scheduledHour + 1) % 24;
            scheduledMinute = 0;
        }
    }

    @Test
    @Ignore
    public void scheduleTest() throws Exception{

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", UUID.randomUUID().toString());
        String testSubject = "testEvent";
        MotechEvent motechEvent = new MotechEvent(testSubject, params);

        Method handlerMethod = ReflectionUtils.findMethod(MotechSchedulerIT.class, "handleEvent", MotechEvent.class);
        eventListenerRegistry.registerListener(new MotechListenerEventProxy("handleEvent", this, handlerMethod), testSubject);

        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent,  String.format("%d %d %d * * ?", scheduledSecond, scheduledMinute, scheduledHour));
        motechScheduler.scheduleJob(cronSchedulableJob);

        while (!executed){
            Thread.sleep(1000);
        }
    }

    public void handleEvent(MotechEvent motechEvent) {
        System.out.println(motechEvent.getSubject());
        executed = true;
    }
}
