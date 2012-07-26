/**
 * \ingroup Outbox
 */
package org.motechproject.outbox.api.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.Date;
import java.util.Map;

/**
 * Holds the details of the outbound message which has to be placed in the partys' outbox.
 */
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

    public OutboundVoiceMessage() {
    }

    public OutboundVoiceMessage(String externalId, VoiceMessageType voiceMessageType, OutboundVoiceMessageStatus status, Date creationTime, Date expirationDate) {
        this.externalId = externalId;
        this.voiceMessageType = voiceMessageType;
        this.status = status;
        this.creationTime = creationTime;
        this.expirationDate = expirationDate;
    }

    public String getExternalId() {
        return externalId;
    }

    /**
     * The unique id of the party to whom this message is designated.
     *
     * @param externalId party's unique id
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * Status of the outbound voice message, ${@link OutboundVoiceMessageStatus} specifies if the message is played, saved
     * etc.
     *
     * @return status of the outbound message
     */
    public OutboundVoiceMessageStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the message
     *
     * @param status ${@link OutboundVoiceMessageStatus} of the message
     */
    public void setStatus(OutboundVoiceMessageStatus status) {
        this.status = status;
    }

    /**
     * Any data to be associated with the message in the form of key, value pairs
     *
     * @return any data that is held with the message
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Sets the additional data that the message can hold in the form of key,value pairs
     *
     * @param parameters a map holding the additional data
     */
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * Gets the time when the message is created
     *
     * @return a Date representing the date message is created on
     */
    public Date getCreationTime() {
        return creationTime;
    }

    /**
     * Sets the creation date for the message
     *
     * @param creationTime a Date when the message is created
     */
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * Gets the message expiry date
     *
     * @return a Date when the message will expire
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the expiry date for the message
     *
     * @param expirationDate a Date when the message expires
     */
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }


    /**
     * Gets the additional meta data of the message.
     *
     * @return ${@link VoiceMessageType} holding the meta data of the message
     */
    public VoiceMessageType getVoiceMessageType() {
        return voiceMessageType;
    }

    /**
     * Sets the metadata ${@link VoiceMessageType} for the message
     *
     * @param voiceMessageType type of the message
     */
    public void setVoiceMessageType(VoiceMessageType voiceMessageType) {
        this.voiceMessageType = voiceMessageType;
    }

    /**
     * Gets the sequence number of the message which represents the order in which the message has to be
     * retrieved from the party's outbox.
     *
     * @return the sequence number of the message
     */
    public long getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the sequence number for the message which represents the order in which the message has to be
     * retrieved from the party's outbox.
     *
     * @param sequenceNumber the sequence number of the message
     */
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
        return new HashCodeBuilder()
                .append(this.externalId)
                .append(this.sequenceNumber)
                .append(this.creationTime)
                .append(this.expirationDate)
                .append(this.status)
                .append(this.parameters)
                .append(this.voiceMessageType)
                .hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof OutboundVoiceMessage)) {
            return false;
        }

        OutboundVoiceMessage that = (OutboundVoiceMessage) o;

        return new EqualsBuilder()
                .append(this.externalId, that.externalId)
                .append(this.sequenceNumber, that.sequenceNumber)
                .append(this.creationTime, that.creationTime)
                .append(this.expirationDate, that.expirationDate)
                .append(this.status, that.status)
                .append(this.parameters, that.parameters)
                .append(this.voiceMessageType, that.voiceMessageType)
                .isEquals();
    }
}
