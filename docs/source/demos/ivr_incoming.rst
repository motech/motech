==========================
Demo: IVR - Incoming Calls
==========================

Simple Scenario
===============
You accept phone calls, prompt for and record a code and send the code in an SMS to the caller.

    .. image:: img/ivr_incoming.jpg
        :scale: 100 %
        :alt: IVR Demo - Inbound
        :align: center

Creating a Config
=================

In order for the IVR provider to send us back call status, we need to create a Config record for that IVR provider in the database. CLick on **Modules** / **Data Services**, scroll down to the **IVR Module** section and then click **+ Add** next to **Config**:

    .. image:: img/ivr_incoming_config.png
        :scale: 100 %
        :alt: IVR Demo - Creating a IVR Provider Config for incoming calls
        :align: center

We named ours **voxeo**.

A little VXML
=============

We need a simple VXML file that prompts the user for a code and then sends it to Motech: ::

    <?xml version="1.0" encoding="UTF-8"?>
      <vxml version = "2.1">
      <form id="enterCode">
        <field name="code">
          <grammar xml:lang="en-US" root = "MYRULE" mode="dtmf">
            <rule id="MYRULE" scope = "public">
              <one-of>
                <item> 1 </item>
                <item> 2 </item>
                <item> 3 </item>
                <item> 4 </item>
                <item> 5 </item>
                <item> 6 </item>
                <item> 7 </item>
                <item> 8 </item>
                <item> 9 </item>
                <item> 0 </item>
              </one-of>
            </rule>
          </grammar>
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

Let's Create a Task
===================

We need to create a task where the trigger is an IVR call status where the call status is ``ANSWERED`` and where the trigger is to send an SMS to the original caller with the code she entered in the message:

    .. image:: img/ivr_incoming_task.png
        :scale: 100 %
        :alt: IVR Demo - Creating a task
        :align: center

Note that **code** is extracted from the Motech event payload with the following: ::

    {{trigger.provider_extra_data.code}}

Also note that a **+1** is added to the SMS recipient because our sample SMS provider, `Plivo <http://plivo.com/>`_, needs it.


Et Voila!
=========

Now call your application at the phone number that your IVR provider gave you, then listen to the "Hello! Please pick a number between 0 and 9." prompt, type in a number (say 4). The phone will drop and soon enough you should receive an SMS with the following message: "The code you chose is 4".

Looking at the Logs
===================

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