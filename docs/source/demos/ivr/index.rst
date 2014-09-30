=========
IVR Demos
=========

First of all, just in case you didn't know, IVR (or `Interactive Voice Response <http://en.wikipedia
.org/wiki/Interactive_voice_response>`_) is a system that enables computers to interact with humans using a
phone. The computer interacts with the human using pre-recorded or synthesized messages and the human either speaks
back or uses her phone keypad (also called `DTMF <http://en.wikipedia.org/wiki/Dual-tone_multi-frequency_signaling>`_)
to interact with the computer.

Initial Setup
=============

In addition to the Motech platform, and the IVR module, you'll also need to build and/or install the `sms-module`_.

Server Settings
---------------

Also be sure that the ``server.url`` property is properly set to your server's URL and that your server is publicly
reachable from the internet. If your server's config source is file based, locate the ``motech-settings.properties``
file and make sure the ``server.url`` is set. If your server's config is done through the UI,
the navigate to **Admin** / **Settings** and set the ``server.url`` property there.

So, for example, if  your server's public address was ``zebra.motechcloud.org`` and it was accessible on port 8080,
then you should see:

::

    server.url=http://zebra.motechcloud.org:8080/motech-platform-server

Confirm the setting (or set it [#]) by clicking **Admin** / **Settings**:

    .. image:: img/server_url.png
        :scale: 100 %
        :alt: IVR Demo - Confirming server.url is set
        :align: center

.. _ivr-sms-config:

SMS Module Config
-----------------

For the demos that have you send or receive SMS, you need a valid SMS config. You'll need to establish an account
with an SMS provider and then configure the `sms-module`_ accordingly. In these demos we're using `Plivo
<http://plivo.com/>`_. To confirm your SMS Settings, click **Modules** / **SMS** / **Settings**:

    .. image:: img/sms_config.png
        :scale: 100 %
        :alt: IVR Demo - SMS config
        :align: center


The Demos, Finally...
=====================

There are three IVR demos:

.. toctree::
    :maxdepth: 1

    incoming
    outgoing
    kookoo
