=====================
Demo: IVR with KooKoo
=====================

Introduction
============
`KooKoo <http://www.kookoo.in/>`_ is a popular IVR provider from India. They are not a standard CCXML/VXML provider,
but instead offer a language that's somewhat similar to a very simplified version of VXML,
named `KooKoo Tunes <http://www.kookoo.in/index.php/kookoo-docs/kookoo-tunes>`_.

This demo is similar to the :std:ref:`incoming-calls` demo but uses KooKoo's simplified XML language instead of standard VXML.

Initial Setup
=============
In addition to the Motech platform, and the IVR module, you'll need to setup a KooKoo account.

Server Settings
---------------

Be sure that the server.url property is properly set to your server's URL and that your server is publicly reachable
from the internet. If your server's config source is file based, locate the ``motech-settings.properties`` file and
make sure the ``server.url`` is set:

::

    server.url=http://zebra.motechcloud.org:8080/motech-platform-server

Confirm the setting (or set it, if your Motech settings are done through the UI) by clicking **Admin** / **Settings**:

    .. image:: img/ivr_server_url.png
        :scale: 100 %
        :alt: IVR KooKoo Demo - Confirming server.url is set
        :align: center

IVR Config
----------

In this demo we're only receiving calls (to the phone number provided to us by KooKoo) so we only need create a minimal
Config, click **Modules** / **IVR**:

    .. image:: img/ivr_kookoo_config.png
        :scale: 100 %
        :alt: IVR KooKoo Demo - Config
        :align: center

.. note:: We're mapping three of the parameters sent back to Motech by KooKoo: ``sid`` will be mapped to ``provider_call_id``, ``cid`` will be mapped to ``from`` (the number of the phone placing the call) and ``called_number`` will be mapped to ``to`` (the number of the phone receiving the phone, in this case the number assigned to you by KooKoo)

Two KooKoo Tunes
----------------

The funny people at KooKoo call an XML file a "Tune", we're creating two which we will add as IVR Templates using the
MDS Data Browser. The first one says "Hello from KooKoo, please type a number from 0 to 9":

    ::

        <?xml version="1.0" encoding="UTF-8"?>
        <Response>
            <playtext>Hello from KooKoo</playtext>
            <collectdtmf l="1">
                <playtext>Please type a number from 0 to 9</playtext>
            </collectdtmf>
            <gotourl>http://kifh.mooo.com:8081/motech-platform-server/module/ivr/template/kookoo/goodbye</gotourl>
        </Response>

It then sends the DTMF response (what the caller types on her cellphone keypad) and fetches the next thing to do from
another XML template (or KooKoo tune) which simply hang up:

    ::

        <?xml version="1.0" encoding="UTF-8"?>
        <response>
            <hangup></hangup>
        </response>


The first script sends an additional query parameter named **data** containing the key pressed during the call to
Motech. Since **data** is not a standard property, it will be added to the ``CallDetailRecord``'s
``providerExtraData`` map property. Actually KooKoo sends yet another query parameter named **event** and with the
value ``GotDTMF``, we'll use **event** to filter the callback from KooKoo such that we pick the one which contains the
 **data** parameter.

Let's Create a :doc:`Task<../modules/tasks>`
--------------------------------------------

We need to create a task where the trigger is an IVR template request and where the **event** key in the
``providerExtraData`` map field is equal to ``GotDTMF``. We also want the action to send an SMS to the original
caller with the code she entered in the message:

    .. image:: img/ivr_kookoo_task.png
        :scale: 100 %
        :alt: IVR KooKoo Demo - Creating a task
        :align: center

The filter source is partially obscured, here it is in full: ::

    {{trigger.provider_extra_data.event}}

.. note:: **data** *(the parameter containing the key pressed, remember?)* is extracted from the Motech event payload with ``{{trigger.provider_extra_data.data}}``



Et Voila!
---------

Now call your application at the phone number that your KooKoo gave you, then listen to the lady say "Hello from
KooKoo, please type a number from 0 to 9", type in a number (say 4). The phone will drop and soon enough
you should receive an SMS with the following message: "You chose 4".
