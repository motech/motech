=========
Demo: IVR
=========

Introduction
============
This Interactive Voice Response (IVR) demo is in fact two demos. An application that receives (inbound) calls and another that makes (initiates outbound) calls.

Initial Setup
=============
In addition to the Motech platform, and the IVR module, the following two modules are required:

    - SMS
    - CMS Lite

Server Settings
---------------

Also be sure that the server.url property is properly set to your server's URL and that your server is publicly reachable from the internet. If your server's config source is file based, locate the ``motech-settings.properties`` file and make sure the ``server.url`` is set:

::

    server.url=http://zebra.motechcloud.org:8080/motech-platform-server

Confirm the setting (or set it, if your Motech settings are done through the UI) by clicking **Admin** / **Settings**:

    .. image:: img/ivr_server_url.png
        :scale: 100 %
        :alt: IVR Demo - Confirming server.url is set
        :align: center

SMS Module Config
-----------------

To send and receive SMS you need a valid SMS config:

    .. image:: img/ivr_sms_config.png
        :scale: 100 %
        :alt: IVR Demo - SMS config
        :align: center


In these demos we're using `Plivo <http://plivo.com/>`_.


IVR Provider
------------

You need an access to an IVR provider, for these examples we used `Voxeo <http://evolution.voxeo.com/>`_, a generic VXML/CCXML provider.

Incoming Calls
==============


Simple Scenario
---------------
You accept phone calls, prompt for and record a code, and then send the code in an SMS to the caller.

    .. image:: img/ivr_incoming.jpeg
        :scale: 100 %
        :alt: IVR Demo - Inbound
        :align: center

Creating a Config
-----------------

In order for the IVR provider to send us back call status, we need to create a Config record for that IVR provider in the database. CLick on **Modules** / **Data Services**, scroll down to the **IVR Module** section and then click **+ Add** next to **Config**:

    .. image:: img/ivr_incoming_config.png
        :scale: 100 %
        :alt: IVR Demo - Creating a IVR Provider Config for incoming calls
        :align: center

    We named ours **voxeo**.

A little VXML
-------------

We need a simple VXML file that prompts the user for a code and then sends it to Motech:

    ::

        <?xml version="1.0" encoding="UTF-8"?>
          <vxml version = "2.1">
          <form id="enterCode">
            <field name="code" type="digits?minlength=1;maxlength=1">
              <prompt>
                Hello! Please pick a number between 0 and 9.
              </prompt>
              <noinput>
                <prompt>
                  I did not hear you. Please try again.
                </prompt>
                <reprompt/>
                </noinput>
              <nomatch>
                <prompt>
                  Is that a number? Please try again.
                </prompt>
                <reprompt/>
              </nomatch>
            </field>
            <filled>
              <prompt>
                You said <value expr="code" />.
              </prompt>
              <assign name="from" expr="session.callerid" />
              <assign name="providerCallId" expr="session.sessionid" />
              <assign name="callStatus" expr="'ANSWERED'" />
              <data name="sendCode" src="http://zebra.motechcloud.org:8080/motech-platform-server/module/ivr/status/voxeo" namelist="code from providerCallId callStatus" method="get" />
            </filled>
          </form>
        </vxml>

That script sends **code** to Motech (at the call status URL for the **voxeo** config) as a parameter using the VXML ``<data>`` element. Since **code** is not a standard property, it will be added to the ``CallDetailRecord``'s ``providerExtraData`` map property. Note that the **call status**, the **caller id** and the **session id** are sent as the ``callStatus``, ``from`` and ``providerCallId`` parameters.

Let's Create a :doc:`Task<../modules/tasks>`
--------------------------------------------

We need to create a task where the trigger is an IVR call status where the call status is ``ANSWERED`` and the action is to send an SMS to the original caller with the code she entered in the message:

    .. image:: img/ivr_incoming_task.png
        :scale: 100 %
        :alt: IVR Demo - Creating a task
        :align: center

    Note that **code** is extracted from the Motech event payload with the following: ::

    {{trigger.provider_extra_data.code}}

Also note that a **+1** is added to the SMS recipient because our sample SMS provider, `Plivo <http://plivo.com/>`_, needs it.


Et Voila!
---------

Now call your application at the phone number that your IVR provider gave you, then listen to the "Hello! Please pick a number between 0 and 9." prompt, type in a number (say 4). The phone will drop and soon enough you should receive an SMS with the following message: "The code you chose is 4".

Looking at the Logs
-------------------

In addition to the obvious sign that you're receiving an SMS from your SMS provider, there are other ways you can check your application works.

You can look at the Tasks module's **Recent task activity** list to see if your task was executed, or you can look at your task's history:

    .. image:: img/ivr_incoming_task_history.png
        :scale: 100 %
        :alt: IVR Demo - Task history
        :align: center

    You can also browse the IVR CallDetailRecord entity in the database using the MDS Data Browser:

    .. image:: img/ivr_incoming_cdr.png
        :scale: 100 %
        :alt: IVR Demo - CallDetailRecord
        :align: center

    Note that our simple VXML application did not bother to set the CallDirection nor many other fields in its status callback to Motech.

    Another way to see how your application would be to be to look at the SMS log or, for even more details, the Server Log.

Outgoing Calls
==============

Simple Scenario
---------------

Upon receiving an SMS, call the sender back and speak the content of the SMS.

    .. image:: img/ivr_outgoing.jpeg
        :scale: 100 %
        :alt: IVR Demo - Inbound
        :align: center

Creating a Config
-----------------

In order for the IVR provider to initiate a call amd send us back call status, we need to create a Config record for that IVR provider in the database. CLick on **Modules** / **Data Services**, scroll down to the **IVR Module** section and then click **+ Add** next to **Config**:

    .. image:: img/ivr_outgoing_config.png
        :scale: 100 %
        :alt: IVR Demo - Creating a IVR Provider Config for outgoing calls
        :align: center

    We named ours **voxeo**. Note that it's a bit different than the one we created in the :doc:`IVR - Incoming Calls Demo<ivr_incoming>`, we need to tell the IVR module how to reach the IVR provider by settings the ``outgoingCallUriTemplate`` and ``outgoingCallMethod`` properties.


The VXML
--------

We need a simple VXML script that will say something that was passed to the IVR provider via the outgoing call initiation HTTP request:

    ::

        <?xml version="1.0" encoding="UTF-8"?>
        <vxml version = "2.1" >
        <form>
            <block>
                <prompt>
                    <value expr="session.connection.ccxml.values.say" />
                </prompt>
            </block>
        </form>
        </vxml>

Note that ``session.connection.ccxml.values.say`` implies Motech will have to add a parameter named ``say`` to the HTTP request querystring to the IVR provider.


Gluing things together with the :doc:`Tasks Module<../modules/tasks>`
---------------------------------------------------------------------

Let's create a task which will, upon receipt of an SMS, initiate an outgoing call and pass a message for the VXML script to say:

    .. image:: img/ivr_outgoing_task.png
        :scale: 100 %
        :alt: IVR Demo - Task: IVR call on SMS receipt
        :align: center

    Note we specify the number to call (in this case the sender of the SMS) and what do say (the content of the SMS) using a map notation in the action ``Parameters`` field.

Drum roll...
------------

Now send an SMS with a simple 'hello' to your SMS application's phone number (given to you by your SMS provider). Wait a few seconds. You should receive a 'hello' voice call from your IVR provider application. Et voila!

Notes
-----

As in the previous example, you can check the **Recent tasks activity** pane on the Tasks module, or check the SMS or the IVR log to see what happened.

It's important to note that this very crude & simple demo does not return very useful call status, so the IVR CallDetailRecord log will not be very useful.
