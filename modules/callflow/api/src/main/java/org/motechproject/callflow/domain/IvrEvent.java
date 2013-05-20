package org.motechproject.callflow.domain;

import static java.util.Arrays.asList;

/**
 * Typical IVR events triggered from IVR systems to run call flow.
 * getEventSubject() returns the event subject that the corresponding event will be raised with
 */
public enum IvrEvent {

    /**
     * An outbound call is ready and waiting in line
     * supported by - verboice
     */
    Queued("ivr.queued"),
    /**
     * The call is currently ringing
     * supported by - verboice
     */
    Ringing("ivr.ringing"),
    /**
     * The call was answered and is currently in progress
     * supported by - kookoo, verboice
     */
    Initiated("ivr.initiated"),

    /**
     * A Dtmf input was received
     * supported by - kookoo, verboice
     */
    Dtmf("ivr.dtmf"),

    /**
     * The call was answered and has ended by either Hangup or Disconnected
     * supported by - kookoo, verboice
     */
    Answered("ivr.end_of_call.answered"),
    /**
     * The call was answered and was ended by the user
     * supported by - kookoo, verboice
     */
    Hangup("ivr.end_of_call.answered.hangup"),
    /**
     * The call was answered and was ended by Motech
     * supported by - kookoo, verboice
     */
    Disconnected("ivr.end_of_call.answered.disconnected"),

    /**
     * The call was not received by the user, one of missed or busy
     * supported by - kookoo, verboice
     */
    Unanswered("ivr.end_of_call.unanswered"),
    /**
     * The call was ringing but was not received by the user
     * supported by - verboice
     */
    Missed("ivr.end_of_call.unanswered.missed"),
    /**
     * The caller received a busy signal
     * supported by - verboice
     */
    Busy("ivr.end_of_call.unanswered.busy"),

    /**
     * The call could not be connected.
     * supported by - verboice
     */
    Failed("ivr.failed"),

    /**
     * A call connect was initiated
     * supported by - verboice
     */
    DialInitiated("ivr.dial"),
    /**
     * A connected call is about to be recorded
     * supported by - kookoo
     */
    DialRecord("ivr.dial.record"),

    /**
     * A connected call was answered and has ended
     * supported by - verboice
     */
    DialAnswered("ivr.dial.answered"),

    /**
     * A connected call was not answered either because it was not received or the line was busy
     * supported by - verboice
     */
    DialUnanswered("ivr.dial.unanswered"),
    /**
     * A connected call was not answered because it was not received the user
     * supported by - verboice
     */
    DialMissed("ivr.dial.unanswered.missed"),
    /**
     * A connected call was not answered because the line was busy
     * supported by - verboice
     */
    DialBusy("ivr.dial.unanswered.busy"),

    /**
     * A connected call could not be placed because of a connection failure
     * supported by - verboice
     */
    DialFailed("ivr.dial.failed");

    private String value;

    private IvrEvent(String value) {
        this.value = value;
    }

    public String getEventSubject() {
        return value;
    }

    public boolean isEndOfCall() {
        if (asList(Answered, Hangup, Disconnected, Unanswered, Missed, Busy, Failed).contains(this)) {
            return true;
        }
        return false;
    }
}
