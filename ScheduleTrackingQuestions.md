# Schedule Tracking Questions #

Alert configurations are specified in the JSON document and properly read into MilestoneWindows, as expected. However, the way alerts are currently sent does not incorporate these configurations whatsoever. Regardless of any custom alerts that have been defined, one alert will be sent per day during the Due, Late and Past periods of a milestone, until it is fulfilled or the schedule's duration has elapsed.


---


The service provides only one functionality, enroll. It seems there should be, at the very least, an unenroll method, and possibly reenroll.


---


When alerts are raised, an event with ENROLLED\_ENTITY\_MILESTONE\_ALERT as its key is emitted to the outbound gateway. It appears that nothing in the platform handles this method. Where should this responsibility lie; the schedule tracking modules, another module, or defined by an implementer?


---


A minor point: MilestoneEvents and EnrolledEntityAlertEvents have their keys defined within the class, rather than in the EventKeys class. This seems to run contrary to the design found in other modules.

Another minor point: EnrollmentFactory is an empty class. Is there a future for this, or is it a relic?


---


There are some other issues (no ability for an end user to specify when a milestone has been fulfilled) but these are captured in Mingle stories.