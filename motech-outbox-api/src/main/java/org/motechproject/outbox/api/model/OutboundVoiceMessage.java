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
package org.motechproject.outbox.api.model;

import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class OutboundVoiceMessage extends MotechAuditableDataObject {

	private static final long serialVersionUID = 3598927460690914607L;
	@TypeDiscriminator
	private String partyId;
	private VoiceMessageType voiceMessageType;
	private OutboundVoiceMessageStatus status;
	private Map<String, Object> parameters;
	private Date creationTime;
	private Date expirationDate;
	
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	public OutboundVoiceMessageStatus getStatus() {
		return status;
	}
	public void setStatus(OutboundVoiceMessageStatus status) {
		this.status = status;
	}
	public Map<String, Object> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	public VoiceMessageType getVoiceMessageType() {
		return voiceMessageType;
	}
	public void setVoiceMessageType(VoiceMessageType voiceMessageType) {
		this.voiceMessageType = voiceMessageType;
	}
	@Override
	public String toString() {
		return "OutboundVoiceMessage [partyId=" + partyId
				+ ", voiceMessageType=" + voiceMessageType + ", status="
				+ status + ", parameters=" + parameters + ", creationTime="
				+ creationTime + ", expirationDate=" + expirationDate + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((creationTime == null) ? 0 : creationTime.hashCode());
		result = prime * result
				+ ((expirationDate == null) ? 0 : expirationDate.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((partyId == null) ? 0 : partyId.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime
				* result
				+ ((voiceMessageType == null) ? 0 : voiceMessageType.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OutboundVoiceMessage other = (OutboundVoiceMessage) obj;
		if (creationTime == null) {
			if (other.creationTime != null)
				return false;
		} else if (!creationTime.equals(other.creationTime))
			return false;
		if (expirationDate == null) {
			if (other.expirationDate != null)
				return false;
		} else if (!expirationDate.equals(other.expirationDate))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (partyId == null) {
			if (other.partyId != null)
				return false;
		} else if (!partyId.equals(other.partyId))
			return false;
		if (status != other.status)
			return false;
		if (voiceMessageType == null) {
			if (other.voiceMessageType != null)
				return false;
		} else if (!voiceMessageType.equals(other.voiceMessageType))
			return false;
		return true;
	}

}
