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
package org.motechproject.tama.api.model;


public class Preferences {
	
    public enum Language {
    	en("English"), fr("French");
        private String text;

        Language(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }
        
        public static Language fromString(String text) {
            if (text != null) {
                for (Language b : Language.values()) {
                    if (text.equalsIgnoreCase(b.text)) {
                      return b;
                    }
                }
            }
            return null;
        }
    }

	private Integer bestTimeToCallHour;
	private Integer bestTimeToCallMinute;
    private Boolean appointmentReminderEnabled; // Is the appointment reminder enabled
    private String ivrCallJobId;
    private Language language = Language.en;

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Integer getBestTimeToCallHour() {
		return bestTimeToCallHour;
	}

	public void setBestTimeToCallHour(Integer bestTimeToCallHour) {
		this.bestTimeToCallHour = bestTimeToCallHour;
	}

	public Integer getBestTimeToCallMinute() {
		return bestTimeToCallMinute;
	}

	public void setBestTimeToCallMinute(Integer bestTimeToCallMinute) {
		this.bestTimeToCallMinute = bestTimeToCallMinute;
	}
	
    public Boolean getAppointmentReminderEnabled()
    {
        return appointmentReminderEnabled;
    }

    public void setAppointmentReminderEnabled(Boolean appointmentReminderEnabled)
    {
        this.appointmentReminderEnabled = appointmentReminderEnabled;
    }

    public String getIvrCallJobId()
    {
        return ivrCallJobId;
    }

    public void setIvrCallJobId(String ivrCallJobId)
    {
        this.ivrCallJobId = ivrCallJobId;
    }

    @Override
    public String toString() {
        return "best time to call hour = " + this.bestTimeToCallHour + ", best time to call minute = " + this.bestTimeToCallMinute;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Preferences that = (Preferences) o;

        if (appointmentReminderEnabled != null ? !appointmentReminderEnabled.equals(that.appointmentReminderEnabled) : that.appointmentReminderEnabled != null)
        {
            return false;
        }
        if (bestTimeToCallHour != null ? !bestTimeToCallHour.equals(that.bestTimeToCallHour) : that.bestTimeToCallHour != null)
        {
            return false;
        }
        if (bestTimeToCallMinute != null ? !bestTimeToCallMinute.equals(that.bestTimeToCallMinute) : that.bestTimeToCallMinute != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = bestTimeToCallHour != null ? bestTimeToCallHour.hashCode() : 0;
        result = 31 * result + (bestTimeToCallMinute != null ? bestTimeToCallMinute.hashCode() : 0);
        result = 31 * result + (appointmentReminderEnabled != null ? appointmentReminderEnabled.hashCode() : 0);
        return result;
    }
}
