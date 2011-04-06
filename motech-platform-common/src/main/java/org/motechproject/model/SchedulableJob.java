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
 * “AS IS” AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
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
package org.motechproject.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Schedulable Job - a data carrier class for a scheduled job that can be fired unlimited number of times
 *  as specified with the cron expression
 *
 * @author Igor (iopushnyev@2paths.com)
 * Date: 16/02/11
 * Time: 1:43 PM
 *
 */
public class SchedulableJob implements Serializable {

    private static final long serialVersionUID = 1L;

    private MotechEvent motechEvent;
    private String cronExpression;
	private Date startTime;
    private Date endTime;

    public SchedulableJob(MotechEvent motechEvent, String cronExpression,
			Date startTime, Date endTime) {
    	this(motechEvent,cronExpression);
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public SchedulableJob(MotechEvent motechEvent, String cronExpression) {

         if (motechEvent == null) {
            throw new IllegalArgumentException("MotechEvent can not be null");
        }

        if (cronExpression == null || cronExpression.isEmpty()) {
            throw new IllegalArgumentException("Cron Expression can not be null or empty");
        }

        this.motechEvent = motechEvent;
        this.cronExpression = cronExpression;
    }

    public Date getStartTime() {
    	return startTime;
    }
    
    public Date getEndTime() {
    	return endTime;
    }
    
    public MotechEvent getMotechEvent() {
        return motechEvent;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SchedulableJob that = (SchedulableJob) o;

        if (!cronExpression.equals(that.cronExpression)) return false;
        if (!motechEvent.equals(that.motechEvent)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = motechEvent.hashCode();
        result = 31 * result + cronExpression.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SchedulableJob{" +
                "motechEvent=" + motechEvent +
                ", cronExpression='" + cronExpression + '\'' +
                '}';
    }
}
