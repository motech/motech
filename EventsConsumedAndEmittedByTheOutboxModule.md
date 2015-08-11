# Events consumed and emitted by the outbox module #

**Consumed events:**


---


OutboxExecutionHandler handles and consumes the following keyed events:

EventKeys.EXECUTE\_OUTBOX\_SUBJECT (execute method)<br />
Parameters/Payload:<br />
EventKeys.PARTY\_ID\_KEY (PartyID) - String<br />
EventKeys.PHONE\_NUMBER\_KEY (PhoneNumber) - String<br />
EventKeys.LANGUAGE\_KEY (Language) - String<br />
And the parameters inherited from the SCHEDULE\_EXECUTION\_SUBJECT

EventKeys.SCHEDULE\_EXECUTION\_SUBJECT (schedule method)<br />
Parameters/Payload:<br />
EventKeys.CALL\_HOUR\_KEY (CallHour) - Integer<br />
EventKeys.CALL\_MINUTE\_KEY (CallMinute) - Integer<br />
EventKeys.PARTY\_ID\_KEY (PartyID) - String<br />
EventKeys.PHONE\_NUMBER\_KEY (PhoneNumber) - String<br />
EventKeys.LANGUAGE\_KEY (Language) - String<br />
And any other parameters inherited from the emitted schedule event.

EventKeys.UNSCHEDULE\_EXECUTION\_SUBJECT (unschedule method)<br />
Parameters/Payload:<br />
EventKeys.SCHEDULE\_JOB\_ID\_KEY (JobID) - String

**Emitted events:**



---


OutboxExecutionHandler emits the following keyed events:

EventKeys.EXECUTE\_OUTBOX\_SUBJECT (schedule method)<br />
This event is emitted by the scheduler. It originates from the schedule method found in the OutboxExecutionHandler.<br />
Parameters/Payload:<br />
EventKeys.PARTY\_ID\_KEY (PartyID) - String<br />
EventKeys.PHONE\_NUMBER\_KEY (PhoneNumber) - String<br />
EventKeys.LANGUAGE\_KEY (Language) - String<br />
And the parameters inherited from the SCHEDULE\_EXECUTION\_SUBJECT

EventKeys.INCOMPLETE\_OUTBOX\_CALL\_SUBJECT (execute method) This event is emitted if the execution of a call fails<br />
Parameters/Payload: EventKeys.PARTY\_ID\_KEY (PartyID) - String

EventKeys.COMPLETED\_OUTBOX\_CALL\_SUBJECT (execute method)<br />
This event is emitted if the execution of a call is successful<br />
Parameters/Payload:<br />
EventKeys.PARTY\_ID\_KEY(PartyID) - String

VoiceOutBoxServiceImpl emits the following keyed event(s):

EventKeys.OUTBOX\_MAX\_PENDING\_MESSAGES\_EVENT\_SUBJECT<br />
This event is emitted if the maximum number of messages in a party's outbox has been reached<br />
Parameters/Payload:<br />
EventKeys.PARTY\_ID\_KEY(PartyID) - String