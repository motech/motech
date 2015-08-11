# Message Campaign Module #

## Description ##

The MoTeCH message campaign module is used to enroll users into messaging campaigns. A user may be a patient, worker, lab, facility, or other desired recipient of the information to be disseminated. A campaign is a course of informational messages that are sent to the target user on an assigned date or schedule. A user can be entered into a campaign at any point during its duration. Users may be removed or re-enrolled into campaigns. The languages and formats of the campaign messages can be specified (IVR, SMS, English, etc). Campaign messages follow a clearly defined schedule and are automatically triggered by the scheduling system.

## Information for implementation ##

Campaigns are for the dissemination of information defined by their campaign messages. There are three key concerns for end users:

  * Defining the campaign's characteristics
  * Enrolling, re-enrolling or un-enrolling a user in a campaign
  * Providing the content for campaign messages

## Defining the campaign's characteristics ##

Specifying the characteristics of a campaign and its messages is typically accomplished using a JSON (JavaScript Object Notation) document. The messageCampaign.properties file specifies the location and name of the JSON message campaign file. Campaigns have a name and type in addition to other fields depending on the message type.

**Types** Campaigns contain a field for type which determines the type of message that will apply to the campaign. All campaign messages that are part of a campaign must be of the same campaign type. The module contains four different campaign types: absolute, cron based, offset and repeating. All campaigns contain a field for the name of the campaign and its type.

**Absolute** This type of campaign is specified as ABSOLUTE. Also included is a list of absolute messages with fields for the message name, formats, languages, message key and date. The date represents the specific date in which to send the message.

A sample absolute campaign

**Offset** This type of campaign is specified as OFFSET and also includes a field for max duration. There is a list of absolute messages with fields for the message name, formats, languages, message key and offset. The offset determines how many days from the reference date that the job (message) will be scheduled.

A sample offset campaign

**Repeating** This type of campaign is specified as REPEATING and also includes the maximum duration for the messages to be repeated. There is a list of absolute messages with fields for the message name, formats, languages, message key and repeat interval. The repeat interval determines how many messages will be scheduled, based upon the max duration of the campaign.

A sample repeating campaign

**Cron-based** This type of campaign is specified as CRON. There is a list of cron messages with fields for the message name, formats, languages, message key and cron expression. The cron expression determines the schedule the messages will follow.

A sample cron-based campaign

## Enrolling a user into a campaign ##

The message-campaign module exposes a MessageCampaignService interface with three methods: startFor, restartFor and stopFor, each accepting a CampaignRequest object. The startFor method schedules the message or messages as jobs in the scheduler. The restartFor method unschedules the campaign jobs and schedules them again based on the reference date and reminder time of the campaign request. The stopFor method unschedules all jobs associated with the user ID and campaign name.

A campaign request associates a user (patient, lab, etc) with a campaign name, reference date and reminder time. The campaign name determines which campaign will be retrieved from the JSON document or unscheduled. The reference date determines the calendar date that the campaign will begin for that user. If no reference date is supplied, then the current date upon enrollment is used in its place. The reminder time specifies what time of day in hours and minutes that the message will be sent.

![http://motechdocumentation.wikispaces.com/file/view/messagecampaignservice.png/256089824/messagecampaignservice.png](http://motechdocumentation.wikispaces.com/file/view/messagecampaignservice.png/256089824/messagecampaignservice.png)

## Content ##

The content for messages is provided through the CMSlite module.

## Example ##

Below is an example of a JSON document that includes two campaigns, each with a number of campaign messages. Each campaign object has three fields: name, type and an array of messages. Each message contains fields determined by their type. Campaign requests associate the user with these campaigns. A reference to the campaign's name, such as &quot;Absolute Dates Message Program&quot; must be included in the CampaignRequest. This allows the system to schedule jobs for the user based on the associated campaign. In the below example, if a user is enrolled in &quot;Absolute Dates Message Program&quot;, two separate jobs (messages) will be scheduled. In the &quot;Relative Parameterized Dates Message Program&quot;, twelve jobs will be scheduled for the user. The number of repeating messages is determined by the repeat intervals compared with the maximum duration. In the example below, Repeating Message #2 would have four scheduled messages due to a maximum duration of five weeks and repeat intervals of nine days.

![http://motechdocumentation.wikispaces.com/file/view/demonstration%27.png/256085096/demonstration%27.png](http://motechdocumentation.wikispaces.com/file/view/demonstration%27.png/256085096/demonstration%27.png)