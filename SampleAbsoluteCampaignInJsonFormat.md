# Sample absolute campaign in JSON format #

http://motechdocumentation.wikispaces.com/file/view/absolutecampaignexample.PNG/256094144/absolutecampaignexample.PNG

## Fields in the JSON document: ##

_name_ - The name of the campaign. Campaign requests use this parameter to associate a user with a given campaign.<br />
_type_ - Specifies that this campaign is an absolute campaign.<br />
_messages_ - an array of messages of type ABSOLUTE.

**In messages array:**<br />
_name_ - The name of the campaign message.<br />
_formats_ - The format(s) for the message, in this case, IVR and SMS.<br />
_languages_ - The language(s) for the message, in this case, en (English).<br />
_messageKey_ - A key uniquely identifying this message.<br />
_date_ - The date for this absolute message to be sent on.

[Back to Message Campaign Documentation](http://code.google.com/p/motech/wiki/MessageCampaignModule)