=================
Core Architecture
=================

Architecture Overview
=====================

MOTECH can logically be broken into the core platform and modules. The core platform wraps several well-known open source systems, and augments and exposes their features to the other components. The main functions of the core are to wrap ActiveMQ (which provides the message queue and the message topic) and present an internal pub/sub like event interface to the module and implementation layers. The core also provides a module loading environment (OSGi), an interface to the Scheduler, and access to the database.

		.. image:: core_arch.png
		   :scale: 100 %
		   :alt: MOTECH Architecture Diagram
		   :align: center

Modules within MOTECH are self-contained bits of functionality that are loaded into the server via the OSGi host. Typically a module provides one type of functionality, such as SMS or access to an external health system. For more information, see :doc:`modules_architecture`. For a list of current and planned modules, see :doc:`../modules/index`.

MOTECH is designed to be horizontally scalable with multiple MOTECHs all acting as workers connecting to the same message queue and topic.

Design Philosophy
=================

Stateless
---------

A core design principle of the MOTECH platform is that the server should be stateless across requests to allow for horizontal scalability. It is expected that code running within the MOTECH server should perform a single action per request and then return. The module should never persist any state in memory or local disk and expect that state to be available to later requests.

Events
------

To aid in the development of stateless services, the MOTECH engine provides a pub/sub like event system. (The event system follows the publish-subscribe pattern but does not implement the standard Java pub/sub protocol.) It helps to decouple emitters of events from the modules that wish to consume them. Any module can emit an event by calling the EventRelay and passing it a MotechEvent and a subject. To register for an event, a module just needs to annotate a method with the list of event subjects of interest.

.. code-block:: java

    @MotechListener(subjects={EventKeys.SAMPLE_EVENT_SUBJECT})
    public void handle(MotechEvent event) {
        logger.info("Received sample event");
    }

For more information, see :doc:`event_scheduler_architecture`.

Scheduled Events & Timers
-------------------------

To assist in the development of a stateless event-based server, the MOTECH platform provides access to a flexible scheduling system. Using the open source Quartz engine, MOTECH can easily schedule events for future consumption. For more information, see :doc:`event_scheduler_architecture`.

Subsystems
==========

Tasks System
------------

The Tasks system allows you to connect modules without code by using tasks. Each task consists of three parts:

#. Trigger: an event raised by Module A (or the Scheduler)
#. Filter: a conditional statement specifying whether the task should run
#. Action: an action executed by Module B in response

In between the trigger and the action, tasks may use data loaders to look up data from other modules that are registered as data sources.

Data Services
-------------

MOTECH Data Services is a flexible data modeling system that allows users to define and share custom schemas without code, and provides auditing and revision tracking. It is a JDBC-based user configurable database abstraction layer on top of a standard SQL database. It provides generated POJOs and OSGi service interfaces for the data objects, generated CRUD events, and generated user interface for data browsing and editing. In a future release it will also support auto-generation of REST APIs.

Dependencies on Third-Party Systems
===================================

Quartz Scheduler
----------------

Quartz is an open source job scheduling engine that enables MOTECH modules to schedule events for future consumption.

Tomcat
------

Apache Tomcat provides the application container for MOTECH.

ActiveMQ
--------

Apache ActiveMQ is an open source message broker that provides the message queue and the message topic.

OSGi
----

Each MOTECH module is an OSGi bundle. Using OSGi allows the platform to manage the bundle lifecycle (adding, removing, starting, and stopping modules), and allows modules to expose service interfaces. For more information, see :doc:`modules_architecture`.

