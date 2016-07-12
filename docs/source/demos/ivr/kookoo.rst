=====================================
Proprietary IVR Provider Demo: KooKoo
=====================================

Introduction
------------
    `KooKoo <http://www.kookoo.in/>`_ is a popular IVR provider from India. They are not a standard CCXML/VXML provider,
    but instead offer a language that's somewhat similar to a very simplified version of VXML,
    named `KooKoo Tunes <http://www.kookoo.in/index.php/kookoo-docs/kookoo-tunes>`_.

    This demo is similar to the `Incoming SMS Demo <incoming>`_ but uses KooKoo's simplified XML language instead of
    standard VXML. We'll only explain the IVR specific parts here, to create the full demo that sends you an SMS,
    please see `the demo <incoming>`_.

Initial Setup
-------------
    You'll need to setup a KooKoo account.

IVR Config
----------

    In this demo we're only receiving calls (to the phone number provided to us by KooKoo) so we only need create a
    minimal config, click **Modules** / **IVR**:

    .. image:: img/kookoo_config.png
        :scale: 100 %
        :alt: IVR KooKoo Demo - Config
        :align: center

    .. note::
        We're mapping three of the parameters sent back to Motech by KooKoo: ``sid`` will be mapped to
        ``provider_call_id``, ``cid`` will be mapped to ``from`` (the number of the phone placing the call) and
        ``called_number`` will be mapped to ``to`` (the number of the phone receiving the phone,
        in this case the number assigned to you by KooKoo)

Two KooKoo Tunes
----------------

    The funny people at KooKoo call an XML file a "Tune". We're creating two by going to **Modules** / **IVR** and
    clicking on **+ Add Template**. The first one says "Hello from KooKoo, please type a number from 0 to 9" and then
    sends the response back in a ``data`` parameter and requests the next thing to do from ``goodbye`` template:

    ::

        <?xml version="1.0" encoding="UTF-8"?>
        <Response>
            <playtext>Hello from KooKoo</playtext>
            <collectdtmf l="1">
                <playtext>Please type a number from 0 to 9</playtext>
            </collectdtmf>
            <gotourl>http://yourserver.com/motech-platform-server/ivr/template/kookoo/goodbye</gotourl>
        </Response>

    Name it **helloworld** and copy/paste the XML above in the value text area:

    .. image:: img/kookoo_templates.png
        :scale: 100 %
        :alt: IVR KooKoo Demo - Config
        :align: center

    The ``goodbye`` template simply says "Thank you" and hangs up:

    ::

        <?xml version="1.0" encoding="UTF-8"?>
        <response>
            <playtext>Thank you</playtext>
            <hangup></hangup>
        </response>


    The first script sends an additional query parameter named ``data`` containing the key pressed during the call to
    Motech. Since ``data`` is not a standard property, it will be added to the ``CallDetailRecord``'s
    ``providerExtraData`` map property. Actually KooKoo sends yet another query parameter named ``event`` with the value
    ``GotDTMF``, we'll use ``event`` to filter the callback from KooKoo such that we pick the one which contains the
    ``data`` parameter.

Let's Create a `Task <tasks>`_
------------------------------

    We need to create a task where the trigger is an IVR template request and where the **event** key in the
    ``providerExtraData`` map field is equal to ``GotDTMF``. We also want the action to send an SMS to the original
    caller with the code she entered in the message:

    .. image:: img/kookoo_task.png
        :scale: 100 %
        :alt: IVR KooKoo Demo - Creating a task
        :align: center

    .. note:: The filter source is partially obscured, here it is in full: ``{{trigger.provider_extra_data.event}}``

    .. note::
        **data** *(the parameter containing the key pressed, remember?)* is extracted from the Motech event payload with
        ``{{trigger.provider_extra_data.data}}``



Et Voila!
---------

    Now call your application at the phone number that  KooKoo gave you, then listen to the lady [#]_ say 'Hello from
    KooKoo, please type a number from 0 to 9', type in a number (say 4). She'll say 'Thank you' and will hang up. Soon
    enough you should receive an SMS with the following message: 'You chose 4'.

    .. [#] The default might not be a lady's voice on your IVR provider, it was on mine.