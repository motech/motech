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

import org.motechproject.model.MotechEvent;
import org.motechproject.model.CronSchedulableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Main class that can bootstrap a Motech Scheduler
 *
 * @author Igor (iopushnyev@2paths.com)
 */
public class MotechScheduler {

    private final static Logger log = LoggerFactory.getLogger(MotechSchedulerServiceImpl.class);

    private final static String SCHEDULE_TEST_INPUT_PARAM = "-t";
    private final static String UNSCHEDULE_TEST_INPUT_PARAM = "-c";

    private final static String TEST_EVENT_NAME = "testEvent";
    public static final String SUBJECT = "test";

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        AbstractApplicationContext ctx
                = new ClassPathXmlApplicationContext(new String[]{"/applicationPlatformScheduler.xml"});

        // add a shutdown hook for the above context...
        ctx.registerShutdownHook();

        log.info("Motech Scheduler started...");

        try {
            if (args.length > 0) {
                MotechScheduler motechScheduler = ctx.getBean(MotechScheduler.class);
                if (SCHEDULE_TEST_INPUT_PARAM.equals(args[0])) {
                    motechScheduler.scheduleTestEvent();
                } else if (UNSCHEDULE_TEST_INPUT_PARAM.equals(args[0])) {
                    motechScheduler.unscheduleTestEvent();
                } else {
                    log.warn("Unknown parameter: " + args[0] + "- ignored");
                }
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }


     @Autowired
     private MotechSchedulerService schedulerService;

    private void scheduleTestEvent() {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(MotechSchedulerService.JOB_ID_KEY, TEST_EVENT_NAME);
        MotechEvent motechEvent = new MotechEvent(SUBJECT, params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, "0/5 * * * * ?");

        try {
            log.info("Scheduling test job: " + cronSchedulableJob);
            schedulerService.scheduleJob(cronSchedulableJob);
        } catch (Exception e) {
            log.warn("Can not schedule test job. " + e.getMessage());
        }
    }

    private void unscheduleTestEvent() {
        try {
            log.info("Unscheduling the test job: " + TEST_EVENT_NAME);
            schedulerService.unscheduleJob(SUBJECT, TEST_EVENT_NAME);
        } catch (Exception e) {
            log.warn("Can not unschedule the test job: " + TEST_EVENT_NAME +" " + e.getMessage());
        }
    }

}
