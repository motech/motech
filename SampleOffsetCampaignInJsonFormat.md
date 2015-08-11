# Sample offset campaign in JSON format #

http://motechdocumentation.wikispaces.com/file/view/offsetcampaignexample.PNG/256096934/offsetcampaignexample.PNG

## Fields in the JSON document: ##

_name_ - The name of the campaign. Campaign requests use this parameter to associate a user with a given campaign.<br />
_type_ - Specifies that this campaign is an offset campaign.<br />
_messages_ - an array of messages of type OFFSET.

**In messages array:**<br />
_name_ - The name of the campaign message.<br />
_formats_ - The format(s) for the message, in this case, IVR or SMS, depending on the message. _languages_ - The language(s) for the message, in this case, en (English).<br />
_messageKey_ - A key uniquely identifying this message.<br />
_date_ - How long from the reference date to send the message.

[Back to Message Campaign Documentation](http://code.google.com/p/motech/wiki/MessageCampaignModule)