# Events consumed and emitted by the message campaign module #

## Consumed events: ##

CampaignMessageHandler handles and consumes events with the key EventKeys.MESSAGE\_CAMPAIGN\_SEND\_EVENT\_SUBJECT

The payload for event subject EventKeys.Message\_CAMPAIGN\_SEND\_EVENT\_SUBJECT (org.motechproject.server.messagecampaign.send-campaign-message)

Parameters/Payload (originating from a CampaignRequest): EventKeys.CAMPAIGN\_NAME\_KEY (CampaignName) EventKeys.MESSAGE\_KEY (MessageKey) EventKeys.SCHEDULE\_JOB\_ID\_KEY (JobID) EventKeys.EXTERNAL\_ID\_KEY (ExternalID)

## Emitted events: ##

CampaignMessageHandler emits events with the key EventKeys.MESSAGE\_CAMPAIGN\_FIRED\_EVENT\_SUBJECT after handling EventKeys.MESSAGE\_CAMPAIGN\_SEND\_EVENT\_SUBJECT

The payload of event subject EventKeys.Message\_MESSAGE\_CAMPAIGN\_FIRED\_EVENT\_SUBJECT (org.motechproject.server.messagecampaign.fired-campaign-message)

Parameters/Payload EventKeys.CAMPAIGN\_NAME\_KEY (CampaignName) - used to find the campaign message, from the CAMPAIGN\_SEND\_EVENT\_SUBJECT event EventKeys.MESSAGE\_KEY (MessageKey) - used from the CampaignRequest to find the message, from the CAMPAIGN\_SEND\_EVENT\_SUBJECT event EventKeys.SCHEDULE\_JOB\_ID\_KEY (JobID) - from the CAMPAIGN\_SEND\_EVENT\_SUBJECT event EventKeys.EXTERNAL\_ID\_KEY (ExternalID) - user ID, from the CAMPAIGN\_SEND\_EVENT\_SUBJECT event EventKeys.MESSAGE\_NAME\_KEY (MessageName) - retrieved from the campaign message's name EventKeys.MESSAGE\_FORMATS (MessageFormats) - list retrieved from the campaign message's formats field EventKeys.MESSAGE\_LANGUAGES (MessageLanguages) - list retrieved from the campaign message's languages field

## Additional information: ##

The message key uniquely identifies campaign messages (even repeating messages have their own unique message key). external ID is a user ID, identifying a patient, nurse, lab, facility, etc, from the campaign request.

The Job ID is a string constructed from EventKeys.BASE\_SUBJECT, campaign name, external ID, and the message key, the latter three are from the campaign request If the campaign name is Campaign1, externalID is 123 and the message key is MessageKey4, then the Job ID will be: org.motechproject.server.messagecampaign.Campaign1.123.MessageKey4

Note that some characteristics of a campaign message (for example, repeatInterval from repeating messages) are not sent as part of an event payload.

## Graphical representation of the event flow through the message campaign module: ##

![http://motechdocumentation.wikispaces.com/file/view/campaignmessageflow.png/256634880/campaignmessageflow.png](http://motechdocumentation.wikispaces.com/file/view/campaignmessageflow.png/256634880/campaignmessageflow.png)