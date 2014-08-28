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

:doc:`ivr_incoming`
===================

:doc:`ivr_outgoing`
===================
