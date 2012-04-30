package org.motechproject.outbox.api.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.Date;
import java.util.Map;

public class OutboundVoiceMessage extends MotechBaseDataObject {
    private static final long serialVersionUID = 3598927460690914607L;

    @TypeDiscriminator
    private String externalId;
    private VoiceMessageType voiceMessageType;
    private OutboundVoiceMessageStatus status;
    private Map<String, Object> parameters;
    private Date creationTime;
    private Date expirationDate;
    private long sequenceNumber;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
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

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public String toString() {
        return "OutboundVoiceMessage{" +
                "externalId='" + externalId + '\'' +
                ", voiceMessageType=" + voiceMessageType +
                ", status=" + status +
                ", parameters=" + parameters +
                ", creationTime=" + creationTime +
                ", expirationDate=" + expirationDate +
                ", sequenceNumber=" + sequenceNumber +
                '}';
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
        result = prime * result + ((externalId == null) ? 0 : externalId.hashCode());
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
        if (externalId == null) {
            if (other.externalId != null)
                return false;
        } else if (!externalId.equals(other.externalId))
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
