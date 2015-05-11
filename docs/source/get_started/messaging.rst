============================
Messaging in MOTECH Overview
============================

.. contents:: Table of Contents
    :depth: 3

Introduction
############

Messaging in MOTECH is independent from the scheduling or message campaigns that fire using the event system.
It is up to the implementer to connect that (or any other for that matter) logic messaging, either by writing code or :doc:`creating tasks <using_tasks/using_tasks>`.
This document is a short overview of the messaging options provided by MOTECH: email, SMS and IVR. Each of these has its own module, which can be used by implementers
for sending messages through different channels.


The Email Module
################

MOTECH provides an Email module which allows to easily send email messages after connecting to an SMTP server. This module is a part of
the platform, so it will be always available. The module is capable of parsing Velocity templates into HTML that will be sent as messages.
More information on configuring and using the Email module can be found in the :doc:`Email module documentation </modules/email>`.

The SMS Module
##############

SMS messaging can be utilised thanks to the SMS module. This module is optional, so you have to install it in your
MOTECH instance if you wish to use it. After connecting to an SMS provider, the module allows both sending and receiving SMS messages.
Sending messages is done through HTTP requests to the provider, receiving messages is similarly done by listening for HTTP requests
from the provider. MOTECH provides a predefined list of configuration templates for different providers, it also allows you to use your
own custom templates. More information on configuring and using the SMS the module can be found in the :std:ref:`SMS module documentation <sms-module>`.

The IVR module
##############

MOTECH also provides a module that allows making and receiving calls by integrating with an IVR provider. This module is optional, so you have to install it in your
MOTECH instance if you wish to use it. Similarly to the SMS module, the IVR also relies on making and receiving HTTP calls from the IVR system. The module
allows you to upload Velocity templates, that will be processed and served to the provider. These are generally VoiceXML or a similar format, but the module can operate
on any format the provider supports. More information on configuring and using the IVR module can be found in the :std:ref:`IVR module documentation <ivr-module>`.
