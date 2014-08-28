==========================
Demo: IVR - Outgoing Calls
==========================

Simple Scenario
===============

Upon receiving an SMS, call the sender back and speak the content of the SMS.

    .. image:: img/ivr_outgoing.jpeg
        :scale: 100 %
        :alt: IVR Demo - Inbound
        :align: center

Creating a Config
=================

In order for the IVR provider to initiate a call amd send us back call status, we need to create a Config record for that IVR provider in the database. CLick on **Modules** / **Data Services**, scroll down to the **IVR Module** section and then click **+ Add** next to **Config**:

    .. image:: img/ivr_outgoing_config.png
        :scale: 100 %
        :alt: IVR Demo - Creating a IVR Provider Config for outgoing calls
        :align: center

We named ours **voxeo**. Note that it's a bit different than the one we created in the :doc:`IVR - Incoming Calls Demo<ivr_incoming>`, we need to tell the IVR module how to reach the IVR provider by settings the ``outgoingCallUriTemplate`` and ``outgoingCallMethod`` properties.


The VXML
========

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
=====================================================================

Let's create a task which will, upon receipt of an SMS, initiate an outgoing call and pass a message for the VXML script to say:

    .. image:: img/ivr_outgoing_task.png
        :scale: 100 %
        :alt: IVR Demo - Task: IVR call on SMS receipt
        :align: center

Note we specify the number to call (in this case the sender of the SMS) and what do say (the content of the SMS) using a map notation in the action ``Parameters`` field.

Drum roll...
============

Now send an SMS with a simple 'hello' to your SMS application's phone number (given to you by your SMS provider). Wait a few seconds. You should receive a 'hello' voice call from your IVR provider application. Et voila!

Notes
=====

As in the previous example, you can check the **Recent tasks activity** pane on the Tasks module, or check the SMS or the IVR log to see what happened.

It's important to note that this very crude & simple demo does not return very useful call status, so the IVR CallDetailRecord log will not be very useful.