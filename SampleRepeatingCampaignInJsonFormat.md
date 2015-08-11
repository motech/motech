# Sample repeating campaign in JSON format #

http://motechdocumentation.wikispaces.com/file/view/repeatingcampaignexample.PNG/256097226/repeatingcampaignexample.PNG

## Fields in the JSON document: ##

_name_ - The name of the campaign. Campaign requests use this parameter to associate a user with a given campaign.<br />
_type_ - Specifies that this campaign is a repeating campaign.<br />
_maxDuration_ - This determines how long the repeating campaign is valid for. Messages will not be scheduled beyond this point.<br />
_messages_ - an array of messages of type REPEATING.

**In messages array:**<br />
_name_ - The name of the campaign message.<br />
_formats_ - The format(s) for the message, in this case, SMS and/or IVR.<br />
_languages_ - The language(s) for the message, in this case, en (English).<br />
_messageKey_ - A key uniquely identifying this message.<br />
_repeatInterval_ - An interval that determines how many times a message is sent, based upon the maxDuration. For example, this campaign will schedule 12 messages. 5 for Message #1, 4 for message #2, and 3 for message #3.

[Back to Message Campaign Documentation](http://code.google.com/p/motech/wiki/MessageCampaignModule)