============================================
Generic VXML IVR Provider Demo: Making Calls
============================================

    Upon receiving an SMS, call the sender back and speak the content of the SMS.

    The details:

    #. You send an SMS to the number provided to you by your SMS provider [#]_.
    #. The SMS module receives a ``/incoming`` HTTP request from the SMS provider and sends a corresponding
       ``inbound_sms`` Motech Event.
    #. The `tasks`_ listens to the ``inbound_sms`` Motech event and triggers [#]_ an outbound IVR call,
       passing the text of the SMS as a parameter named ``message``.
    #. Your IVR provider receives the outbound call request.
    #. Your IVR provider then asks Motech for the VXML template [#]_, executes the VXML.
    #. You receive a phone call, pick up, hear the IVR computer voice speak the content of your SMS.

    .. [#] You must also tell your SMS provider what to do when they receive an SMS, `remember <ivr-sms-config>`_?
    .. [#] By issuing an HTTP request to an URL provided by your IVR provider.
    .. [#] At the URI you told your provider to find the VXML for outgoing calls from your number.


Creating a Config
-----------------

    In order for the IVR provider to initiate a call, we need to create a Config,
    click **Modules** / **IVR** / **Settings**:

    .. image:: img/out_config.png
        :scale: 100 %
        :alt: IVR Demo - Creating a IVR Provider Config for outgoing calls
        :align: center

    .. note::
        We named ours **voxeo**. Note that it's a bit different than the one we created in the
        :std:ref:`incoming-calls` demo, we need to tell the IVR module how to reach the IVR provider by settings the
        ``outgoingCallUriTemplate`` and ``outgoingCallMethod`` properties.


The VXML
--------

    We need a simple VXML script that will say something that was passed to the IVR provider via the outgoing call
    initiation HTTP request:

        ::

            <?xml version="1.0" encoding="UTF-8"?>
            <vxml version = "2.1" >
            <form>
                <block>
                    <prompt>
                        <value expr="session.connection.ccxml.values.message" />
                    </prompt>
                </block>
            </form>
            </vxml>

    .. note::
        ``session.connection.ccxml.values.message`` implies Motech will have to add a parameter named ``message`` to
        the HTTP request querystring to the IVR provider.

    We'll name this template ``say``:

    .. image:: img/out_template.png
        :scale: 100 %
        :alt: IVR Demo - The ``say`` VXML template
        :align: center


Gluing things together with the `tasks`_
----------------------------------------

    Let's create a task which, upon receipt of an SMS, initiates an outgoing call and passes a message for the VXML
    script to say:

    .. image:: img/out_task.png
        :scale: 100 %
        :alt: IVR Demo - Task: IVR call on SMS receipt
        :align: center

    .. note::
        we specify the number to call (in this case the sender of the SMS) and what do say (the content of the SMS)
        using a map notation in the action ``Parameters`` field.

Drum roll...
------------

    Now send an SMS with a simple 'hello world'. Wait a few seconds [#]_. You should receive a 'hello world' voice call
    from your IVR provider. Et voila!

    .. [#] Crossing your fingers always helps

Notes
-----

    As in the previous example, you can check the **Recent tasks activity** pane on the Tasks module,
    or check the SMS or the IVR log to see what happened.

    It's important to note that this very crude & simple demo does not deal with call status,
    so the IVR CallDetailRecord log will not be very useful.