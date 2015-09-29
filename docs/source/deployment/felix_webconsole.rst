===================================
Using Felix Web Console with Motech
===================================

.. contents:: Table of Contents
   :depth: 2

Overview
========

MOTECH is built on top of the `Felix OSGi Framework <http://felix.apache.org/>`_. Thanks to this, users can install the Felix Web Console
on their installations and then use it for monitoring the server.

Benefits of using the Web Console
=================================

The Felix Web Console allows viewing details of all bundles installed in the System. It also allows installing and
uninstalling modules. Generally speaking the only benefit of using the web console over the Motech Admin module
is that it gives access to bundles that are not MOTECH modules - for example third party libraries such as Spring.
More information on the Web Console can be found here: http://felix.apache.org/documentation/subprojects/apache-felix-web-console.html

Installation
============

The Web Console can be installed by simply downloading it from the `Felix Downloades Page<http://felix.apache.org/downloads.cgi>`_ into the
*~/.motech/bundles* directory belonging to the user running MOTECH. The console should become active after starting MOTECH.

Accessing the Console
=====================

The console should be available after appending *module/system/console* to your MOTECH server url. The default login
is admin/admin. Refer to the `Web Console Documentation<http://felix.apache.org/documentation/subprojects/apache-felix-web-console.html>`_ for ways to change it.
