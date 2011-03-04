/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
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
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.motechproject.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Run Once Schedulable Job - a data carrier class for a job scheduled in the future that can be fired only once
 *
 * This class is immutable
 *
 * User: Igor (iopushnyev@2paths.com)
 * Date: 16/02/11
 * Time: 1:43 PM
 *
 */
public final class RunOnceSchedulableJob implements Serializable {

    private static final long serialVersionUID = 1L;

    private MotechScheduledEvent motechScheduledEvent;
    private Date startDate;

    /**
     * Constructor
     *
     * @param motechScheduledEvent - event data message that will be send by Motech Scheduler when this job is fired
     * @param startDate - date and time when the job fill be fired
     *
     * @throws IllegalArgumentException if motechScheduledEvent or startDate is null or startDate is in past
     */
    public RunOnceSchedulableJob(MotechScheduledEvent motechScheduledEvent, Date startDate) {

        if (motechScheduledEvent == null) {
            throw new IllegalArgumentException("MotechScheduledEvent can not be null");
        }

        if (startDate == null ) {
             throw new IllegalArgumentException("Start date can not be null");
        }
        Date currentDate = new Date();
        if (startDate.before(currentDate) ) {
             throw new IllegalArgumentException("Sstart date can not be in the past. \n" +
                                                " Start date: " + startDate.toString() +
                                                " now:" + currentDate.toString());
        }

        this.motechScheduledEvent = motechScheduledEvent;
        this.startDate = startDate;
    }

    public MotechScheduledEvent getMotechScheduledEvent() {
        return motechScheduledEvent;
    }

    public Date getStartDate() {
        return startDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RunOnceSchedulableJob that = (RunOnceSchedulableJob) o;

        if (!motechScheduledEvent.equals(that.motechScheduledEvent)) return false;
        if (!startDate.equals(that.startDate)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = motechScheduledEvent.hashCode();
        result = 31 * result + startDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RunOnceSchedulableJob{" +
                "motechScheduledEvent=" + motechScheduledEvent +
                ", startDate=" + startDate +
                '}';
    }
}

