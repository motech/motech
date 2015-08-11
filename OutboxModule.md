# Outbox Module #

### Description ###



---


The outbox module acts as a repository of pending and saved voice messages for the client. The module provides a service for the retrieval and manipulation of messages and information from a party's outbox. A party could be a patient, nurse, doctor, etc. The module provides event handling that allows for the scheduling or unscheduling of voice messages for a specific time of day. End users may configure some of the details regarding the outbox's limits (for example, the maximum number of messages in the outbox may be configured).

### Information for implementation ###



---


The outbox module presents end users with the following functionality:

  * The retrieval and manipulation of information and messages from a party's outbox using a voice outbox service
  * Configuration of the outbox's properties
  * Scheduling and unscheduling voice messages using events
  * Specification of the outbox voice message menu templates

The outbox module is as an OSGi module.

Pending and saved OutboundVoiceMessages are stored in CouchDB.

### Retrieval and manipulation of information and messages from an outbox ###



---


The VoiceOutBoxService interface provides methods for the retrieval and manipulation of information and messages from a party's outbox.

![http://motechdocumentation.wikispaces.com/file/view/voiceoutboxservice.png/257036328/voiceoutboxservice.png](http://motechdocumentation.wikispaces.com/file/view/voiceoutboxservice.png/257036328/voiceoutboxservice.png)

**addMessage** - Adds the OutboundVoiceMessage to the party's outbox. The party's ID is retrieved from the OutboundVoiceMessage.

**getNextPendingMessage** - Returns the next OutboundVoiceMessage in the party's outbox. The party's ID is retrieved from the String parameter. Returns null if there are no pending messages. Pending messages contain the message status PENDING and their retrieval is based upon creation date and priority (LOW, MEDIUM, HIGH).

**getNextSavedMessage** - Returns the next saved OutboundVoiceMessage in the party's outbox. Returns null if there are no saved messages. The party's ID is retrieved from the String parameter. Saved messages contain the message status SAVED.

**getMessageById** - Returns an OutboundVoiceMessage based on its message ID passed in as a string.

**removeMessage** - Removes the corresponding OutboundVoiceMessage from the outbox, based on the message ID passed in as a string.

**setMessageStatus** - Sets the voice message status of the message, based on the message ID passed in as a string. Message status can be PENDING, PLAYED, SAVED, EXPIRED or FAILED.

**saveMessage** - Saves the OutboundVoiceMessage by its message ID. Its message status is changed to SAVED. The length of time to save the message is specified in the configuration. The date of saving + the length of time to save the message determines the date the message is saved until.

**getNumberPendingMessages** - Returns the number of pending messages (messages with PENDING message status) based on the party's ID, which is retrieved from the string parameter.

The setters and getters for configurable properties of the outbox are excluded from the details. They are described in the following section.

Several methods of the service require OutboundVoiceMessages. [See here](http://code.google.com/p/motech/wiki/OutboxVoiceMessages) for more information about OutboundVoiceMessages.

### Configuration of outbox properties ###



---


In the applicationOutboxAPI.xml configuration file, the number of days to keep saved messages and the maximum number of pending messages can be set through Spring setter injection:

&lt;property name=&quot;numDayskeepSavedMessages&quot; value=&quot;10&quot;/&gt;

&lt;property name=&quot;maxNumberOfPendingMessages&quot; value=&quot;15&quot;/&gt;

The value specifies the number of messages for each respective property.

### Outbox voice message menu ###



---


The outbox module includes vm template files that are used for a voice message menu system for a user's outbox. These vm files may be edited to contain different audio files or synthesized speech.

Example: nomsg.vm

http://motechdocumentation.wikispaces.com/file/view/vm.PNG/258749224/vm.PNG

Above is a voice XML form using Apache velocity for dynamic content. Prompts define the audio or voice message to be played for the recipient. The audio src tag specifies a wav file to be played for the user when there are no messages. After the hello.wav file is played, a synthesized voice will tell the user &quot;There are no pending messages in your outbox&quot;. The goto next tag redirects the user to the url that was set in the preceding line, which is the main menu for Tama. The redirection includes the user's party Id in the request.

The text or audio files in the vm templates may be changed and specified by end users.

### Scheduling and unscheduling voice messages ###



---


The OutBoxExecutionHandler is capable of handling three different event emissions with the following keys:<br />
EventKeys.EXECUTE\_OUTBOX\_SUBJECT (org.motechproject.server.outbox.execute)<br />
EventKeys.SCHEDULE\_EXECUTION\_SUBJECT (org.motechproject.server.outbox.schedule-execution)<br />
EventKeys.UNSCHEDULE\_EXECUTION\_SUBJECT (org.motechproject.server.outbox.unschedule-execution)

Implementers may emit events for scheduling and unscheduling voice messages. The initiation of the voice message calls is handled by the execute method in the handler.

_Schedule event_:<br />
Scheduling requires a call hour, call minute, party Id and phone number. The handled event's payload is added to the event emitted by this scheduling. A cron schedulable job is scheduled with the call hour and call minute.

When executing a scheduled call, the next pending message will be used for the reminder call.

_Unschedule event_:<br />
Unscheduling an event requires that the jobId of the event to unschedule is included in the unschedule event's parameters.

_Execute event_:<br />
This event is automatically created and scheduled by the schedule method.

See event handling for a more detailed description regarding the payloads of these events.

### Outbox as an OSGi module ###



---


The Outbox acts as an OSGi module that exposes (exports) several of its constituent classes. VxmlController and OutboxExecutionHandler are not exported and are private to the module. These are accessed with HTTP requests and event emissions, respectively. The characteristics of the module are specified with a plugin in the Maven pom.xml.

**Relevant publicly exposed classes exposed by the module**

VoiceOutBoxService<br />
EventKeys<br />
OutboundVoiceMessage<br />
VoiceMessageType<br />
OutboundVoiceMessageStatus<br />
MessagePriority<br />
OutboxContext