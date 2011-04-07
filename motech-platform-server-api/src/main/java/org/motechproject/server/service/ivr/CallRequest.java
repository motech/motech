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
package org.motechproject.server.service.ivr;

import org.motechproject.model.MotechEvent;

import java.io.Serializable;

/**
 * This class is used to request a call from the IVR system
 *
 * To receive events related to this call provide this class with the events to raise when the following
 * events within the IVR system happen.  The supplied event will be augmented with {@link CallDetailRecord}
 * if one is available
 *
 * onSuccessEvent - Following the successful completion of the call
 * onBusyEvent - If the IVR system is unable to place the call because the line is busy
 * onNoAnswerEvent - If the IVR system is unable to complete the call because of no answer
 * onFailureEvent - It failed.  I'm sorry for you.  Why?  Not sure. Your guess is as good as mine,
 */
public class CallRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private long messageId;
    private String phone;
    private int timeOut; //how long IVR will wait for the channel to be answered before its considered to have failed (in ms)
    private String vxmlUrl;

    private MotechEvent onSuccessEvent;
    private MotechEvent onBusyEvent;
    private MotechEvent onNoAnswerEvent;
    private MotechEvent onFailureEvent;

    /**
     * Generate a call request for the IVR system
     *
     * @param messageId
     * @param phone
     * @param timeOut
     * @param vxmlUrl
     */
    public CallRequest(long messageId, String phone, int timeOut, String vxmlUrl) {

         if (phone == null) {
            throw new IllegalArgumentException("phone can not be null");
        }

        if (vxmlUrl == null) {
            throw new IllegalArgumentException("vxmlUrl can not be null");
        }

        this.messageId = messageId;
        this.phone = phone;
        this.timeOut = timeOut;
        this.vxmlUrl = vxmlUrl;

        this.onSuccessEvent = null;
        this.onBusyEvent = null;
        this.onNoAnswerEvent = null;
        this.onFailureEvent = null;
    }

    public long getMessageId() {
        return messageId;
    }

    public String getPhone() {
        return phone;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public String getVxmlUrl() {
        return vxmlUrl;
    }

    public MotechEvent getOnSuccessEvent()
    {
        return onSuccessEvent;
    }

    public void setOnSuccessEvent(MotechEvent onSuccessEvent)
    {
        this.onSuccessEvent = onSuccessEvent;
    }

    public MotechEvent getOnBusyEvent()
    {
        return onBusyEvent;
    }

    public void setOnBusyEvent(MotechEvent onBusyEvent)
    {
        this.onBusyEvent = onBusyEvent;
    }

    public MotechEvent getOnNoAnswerEvent()
    {
        return onNoAnswerEvent;
    }

    public void setOnNoAnswerEvent(MotechEvent onNoAnswerEvent)
    {
        this.onNoAnswerEvent = onNoAnswerEvent;
    }

    public MotechEvent getOnFailureEvent()
    {
        return onFailureEvent;
    }

    public void setOnFailureEvent(MotechEvent onFailureEvent)
    {
        this.onFailureEvent = onFailureEvent;
    }
}
