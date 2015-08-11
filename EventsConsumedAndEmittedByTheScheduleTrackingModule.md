# Events consumed and emitted by the schedule tracking module #

## Consumed events ##

ScheduleTrackingEventService handles and consumes the following keyed event(s):

EventKeys.ENROLLED\_ENTITY\_REGULAR\_ALERT (raiseAlertForEnrolledEntity method) Parameters/Payload: SCHEDULE\_NAME\_KEY (scheduleName) - String ENROLLMENT\_ID (externalId) - String JOB\_ID\_KEY (externalId) - String

## Emitted events ##

ScheduleTrackingEventService emits the following keyed event(s):

EventKeys.ENROLLED\_ENTITY\_MILESTONE\_ALERT This event is emitted by the tracking event service when an alert needs to be sent out and handled. Checks for alerts are made per user, per schedule on a daily basis. Parameters/Payload: WINDOW\_NAME (alert.windowName) MILESTONE\_NAME (alert.milestoneName) Also included are all the data parameters from the alert as specified in the JSON document.

ScheduleTrackingServiceImpl emits the following keyed event(s):

EventKeys.ENROLLED\_ENTITY\_REGULAR\_ALERT This event is emitted by the scheduler. It originates from the enroll method in the schedule tracking service implementation. Parameters/Payload: SCHEDULE\_NAME\_KEY (scheduleName) - String ENROLLMENT\_ID (externalId) - String JOB\_ID\_KEY (externalId) - String