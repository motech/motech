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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={"/testApplicationContext.xml"})

public class SpringQuartzTest {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

     String groupName = "group1";


    @Test
    public void scheduleUnscheduleTest() throws Exception{

        String uuidStr = UUID.randomUUID().toString();

        JobDetail job = new JobDetail(uuidStr, groupName, MotechScheduledJob.class);
        job.getJobDataMap().put("eventType", "PillReminder");
        job.getJobDataMap().put("patientId", "001");

        Trigger trigger = new SimpleTrigger(uuidStr, groupName, new Date(new Date().getTime()+3000));

        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        scheduler.scheduleJob(job, trigger);

        scheduler = null;

        scheduler = schedulerFactoryBean.getScheduler();


        String[] jobNames = scheduler.getJobNames(groupName);
        assertEquals(1, jobNames.length);

        String[] triggerNames = scheduler.getTriggerNames(groupName);
        assertEquals(1, triggerNames.length);


        scheduler.unscheduleJob(uuidStr, groupName);
        scheduler.deleteJob(uuidStr, groupName);

        jobNames = scheduler.getJobNames(groupName);
        assertEquals(0, jobNames.length);

        triggerNames = scheduler.getTriggerNames(groupName);
        assertEquals(0, triggerNames.length);

    }
}
