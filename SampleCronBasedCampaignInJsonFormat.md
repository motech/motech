# Sample cron based campaign in JSON format #

http://motechdocumentation.wikispaces.com/file/view/cronbasedcampaignexample.PNG/256097512/cronbasedcampaignexample.PNG

## Fields in the JSON document: ##

_name_ - The name of the campaign. Campaign requests use this parameter to associate a user with a given campaign.<br />
_type_ - Specifies that this campaign is a cron based campaign.<br />
_messages_ - an array of messages of type CRON.

**In messages array:**<br />
_name_ - The name of the campaign message.<br />
_formats_ - The format(s) for the message, in this case, IVR and SMS. _languages_ - The language(s) for the message, in this case, en (English).<br />
_messageKey_ - A key uniquely identifying this message.<br />
_cron_ - A valid cron expression that specifies the scheduling of the messages.

[Back to Message Campaign Documentation](http://code.google.com/p/motech/wiki/MessageCampaignModule)