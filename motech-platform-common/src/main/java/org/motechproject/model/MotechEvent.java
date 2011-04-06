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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Motech Scheduled Event data carrier class,
 * Instance of this class with event specific data will be send by Motech Scheduler when a scheduled event is fired
 *
 * This class is immutable
 *
 * @author Igor (iopushnyev@2paths.com)
 * Date: 16/02/11
 *
 */
public final class MotechEvent implements Serializable{

    private static final long serialVersionUID = 2L;

    public static final String EVENT_TYPE_KEY_NAME = "eventType";
	public static final String SCHEDULE_PATIENT_ID_KEY_NAME = "patientId";
	public static final String SCHEDULE_APPOINTMENT_ID_KEY_NAME = "appointmentId";

    private String jobId;
    private String eventType;
    private Map<String, Object> parameters;

    /**
     * Constructor
     * @param jobId - ID of the scheduled job that generated this event.
     * @param eventType - event type: Pill Reminder, Appointment Reminder ...
     * @param parameters - a Map<String, Object> of additional parameters
     *
     * @throws IllegalArgumentException if given jobId or entityType is null
     */
    public MotechEvent(String jobId, String eventType, Map<String, Object> parameters) {

        if (jobId == null) {
            throw new IllegalArgumentException("jobId can not be null");
        }

        if (eventType == null) {
            throw new IllegalArgumentException("eventType can not be null");
        }

        this.jobId = jobId;
        this.eventType = eventType;
        this.parameters = parameters;
    }

    public String getJobId() {
        return jobId;
    }

    public String getEventType() {
        return eventType;
    }

    public Map<String, Object> getParameters() {
        if (parameters == null ) {
            return new HashMap<String, Object>();
        }
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MotechEvent that = (MotechEvent) o;

        if (!eventType.equals(that.eventType)) return false;
        if (!jobId.equals(that.jobId)) return false;
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = jobId.hashCode();
        result = 31 * result + eventType.hashCode();
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MotechEvent{" +
                "jobId='" + jobId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
