# Schedule Tracking Module Documentation #

## Description ##

The schedule tracking module allows a client to be enrolled in a clearly defined schedule consisting of specified milestones. Milestones represent windows of time that a certain criteria, treatment, course, etc should be fulfilled before moving on to the next milestone. Alerts are sent to the enrolled clients when they are due, late or past on the period of fulfillment. Clients may be enrolled in unlimited schedules, but can only be enrolled once in any given schedule.

## Information for implementation ##

Schedules define a set of milestones that should be fulfilled in specified windows of time. Implementers can use the module to accomplish the following:

  * Use a schedule tracking service that allows a specific client to be enrolled into a specific schedule for its duration
  * Send alerts to clients that have been enrolled in a schedule
  * Define JSON documents that specify a schedule and its set of milestones

Enrollments are stored in CouchDB.

## Specifying a schedule and its milestones ##

Specifying the characteristics of a schedule and its milestones is typically accomplished using a JSON (JavaScript Object Notation) document. The scheduletracking.properties file specifies the location and name of the JSON schedule definitions file. Schedules have a name, total duration and list of milestones. Milestones have names, reference dates, schedule windows, data and custom alerts (which may be defined, but are currently unused). Below is an example of a JSON schedule definition.

## Enrolling a user in a campaign ##

The schedule tracking module exposes a ScheduleTrackingService with one method, enroll. To enroll a user in a schedule, an EnrollRequest must be passed to the enroll method in the service. EnrollRequests consist of the following information:

_externalId_ (String) - A unique ID of the client.<br />
_scheduleName_ (String) - The name of the schedule defined in the JSON document to be enrolled in.<br />
_enrolledInMilestone_ (String) - Currently unused by the module.<br /> _enrolledAt_ (int) - Currently unused by the module.<br />
_preferredAlertTime_ (Time) - The time of day, in hours and minutes, the user should be sent alerts.

When enroll is invoked, the service determines whether that client is already enrolled in the schedule. If they are, the service does not attempt to schedule any new jobs for them and returns. If they are not already enrolled in the schedule, a new enrollment for that user is created and added to the database. Daily jobs for that user-schedule relationship are scheduled to fire every day at the preferred time. When the job is triggered and the event is fired, the system checks if there are any applicable alerts to send out. If there are, the system sends an alert event that needs to be handled.

## Example ##

Below is an example JSON definition file for a single schedule. The schedule has a name, IPTI Schedule, and a total duration of 52 weeks. There are two distinct milestones specified within the schedule. The first, IPTI 1, will spend its first 92 days in waiting status, until it enters the &quot;earliest&quot; window. One week will be spent in upcoming status, and two weeks will be spent in due status. Due to no maximum period being defined, as soon as the late period is entered, the milestone's status will be set to past. While the milestone is in due, late or past status, one alert per day will be sent to the client at the preferred time they were enrolled with.

The second milestone spends eight days waiting, a week in upcoming, and a week in due. After that, there is once again no late period, so the milestone's status is set to past. The entire schedule runs for 52 weeks. When 52 weeks have elapsed since enrollment, alerts are stopped.

Both milestones define data that is included in the scheduled payload. The first milestone has custom alerts, which currently are not used for raising alerts.

![http://motechdocumentation.wikispaces.com/file/view/exampleschedulejson.png/261368102/exampleschedulejson.png](http://motechdocumentation.wikispaces.com/file/view/exampleschedulejson.png/261368102/exampleschedulejson.png)