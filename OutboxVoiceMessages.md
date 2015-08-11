# Outbox Voice Messages #

![http://motechdocumentation.wikispaces.com/file/view/outboundvoicemessage.png/259081916/outboundvoicemessage.png](http://motechdocumentation.wikispaces.com/file/view/outboundvoicemessage.png/259081916/outboundvoicemessage.png)

**OutboundVoiceMessage**<br />
_partyId_: A unique identifier for the party.<br />
_creationTime_: When the message was created.<br />
_expirationTime_: When the message should expire.<br />
_Parameters_: Used for additional content in voice messages. For example, if an OutboundVoiceMessage is created with a VoiceMessageType of &quot;appointmentReminder&quot;, the appointmentReminder.vm file will be used for the scheduled voice message. In this case, the appointmentReminder plays a hello wav file, followed by a synthesized voice stating &quot;Please fix your appointment&quot;, followed by dynamic retreivel of the &quot;message&quot; key in the parameters map. Whatever text was keyed with that message will be spoken by the synthesized voice. If the &quot;message&quot; key was paired with a string of &quot;with doctor Johnson&quot;, the voice message would say &quot;(Hello.wav), please fix your appointment with doctor Johnson&quot; and then redirect the user to the outbox main menu (which gives the message recipient the option to repeat a message, save a message, play the next message or return to the main TAMA menu).

**OutboundVoiceMessageStatus**<br />
An enumeration that determines whether a voice message is pending, played, saved, expired or failed. Messages should initially be created as PENDING.

**VoiceMessageType**<br />
_voiceMessageTypeName_ - Used if no templateName is specified.<br />
_templateName_ - Used to determine which VXML document to associate with the voice message.<br />
_canBeSaved_ - Allows the party to save the message.<br />
_canBeReplayed_ - Allows the party to replay a message.

**MessagePriority**<br />
Specifies the priority of a voice message. Messages with higher priority will be played first.