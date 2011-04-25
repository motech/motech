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
package org.motechproject.server.outbox;

import org.motechproject.context.Context;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.EventKeys;
import org.motechproject.event.EventListener;
import org.motechproject.gateway.MotechSchedulerGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class UnscheduleOutboxExecutionHandler implements EventListener {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public final static String UNSCHEDULE_OUTBOX_EXECUTION = "UnscheduleOutboxExecutionReminder";

    private MotechSchedulerGateway schedulerGateway = Context.getInstance().getMotechSchedulerGateway();

	@Override
	public void handle(MotechEvent event) {

        String jobId = EventKeys.getScheduleJobIdKey(event);
        if (jobId == null) {
            logger.error("Can not handle Event: " + event.getSubject() +
                     ". The event is invalid - missing the " + EventKeys.SCHEDULE_JOB_ID_KEY + " parameter");
            return;
        }

		schedulerGateway.unscheduleJob(jobId);
	}

	@Override
	public String getIdentifier() {
		return UNSCHEDULE_OUTBOX_EXECUTION;
	}
}
