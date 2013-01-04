package org.motechproject.server.voxeo.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Objects;

/**
 * Phone/IVR Event in IVR system, represents context of call such as caller id, call status etc.
*/
public class PhoneCallEvent {
    /**
     * Call status
     */
    public enum Status {
        ALERTING, CONNECTED, DIALOG_STARTED, DIALOG_EXIT, DISCONNECTED, PROGRESSING, FAILED, UNKNOWN;
    }

    /**
     * Call failure reasons.
     */
    public enum Reason {
        BAD_NUMBER("badnumber"), BUSY("busy"), REJECTED("rejected"), TIMEOUT("timeout"), UNKNOWN("unknown"), UNREACHABLE("unreachable"), UNAUTHORIZED("unauthorized");

        private String text;

        Reason(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public static Reason fromString(String text) {
            if (text != null) {
                for (Reason b : Reason.values()) {
                    if (text.equalsIgnoreCase(b.text)) {
                      return b;
                    }
                }
            }
            return null;
        }
    }

    @JsonProperty
    private Status status;
    @JsonProperty
    private Reason reason;
    @JsonProperty
    private String callerId;
    @JsonProperty
    private Long timestamp;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PhoneCallEvent phoneCallEvent = (PhoneCallEvent) o;

        return Objects.equals(callerId, phoneCallEvent.callerId) && Objects.equals(reason, phoneCallEvent.reason) &&
                Objects.equals(status, phoneCallEvent.status) && Objects.equals(timestamp, phoneCallEvent.timestamp);
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (callerId != null ? callerId.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}
