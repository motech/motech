.. _event-module:

Event Module
============

.. contents::
    :depth: 2

Description
-----------

The Event module allows to create listener and assign action to event. The module is using :code:`MotechEvent` to provide
event with specific data that will be sent by Motech Scheduler when a scheduled event is fired.
The module is using ActiveMQ to support the event execution and callback. It is possible to register your own callback
function using :code:`EventCallbackService`.

MotechEvent
-----------

The Event module use :code:`MotechEvent` to storage information about fired event. Instance of this class is sending by Motech Scheduler
module to a specific handler (see :code:`TaskTriggerHandler` in Tasks module). Main fields of :code:`MotechEvent`:

+-------------------+----------------------------------------------------------------+--------------------+
|Field              |Description                                                     |Type                |
+===================+================================================================+====================+
|subject            |Identifies event. The listeners can subscribe event using it.   |String              |
+-------------------+----------------------------------------------------------------+--------------------+
|messageDestination |Destination of an event. Contains the id of the Motech listener |String              |
|                   |that this event is meant for.                                   |                    |
+-------------------+----------------------------------------------------------------+--------------------+
|callbackName       |Name of service, which is containing callback functions.        |String              |
+-------------------+----------------------------------------------------------------+--------------------+
|parameters         |Parameters of event. Contains data used in event execution.     |Map<String, Object> |
+-------------------+----------------------------------------------------------------+--------------------+
|metadata           |Stores any additional data, that is not meant to be the event's |Map<String, Object> |
|                   |payload.                                                        |                    |
+-------------------+----------------------------------------------------------------+--------------------+

This class is immutable.

Listener
--------

To create an event listener you should add an :code:`@MotechListener` annotation to handler function. In
:doc:`Core Architecture </architecture/core_architecture>` you can find an example of use of this annotation.

Register custom callback
------------------------

ActiveMQ is sending events with its own retry. To register event callbacks you should implement an :code:`EventCallbackService`
interface and expose it as OSGi service. Then this service name must be set in the callback field of the :code`MotechEvent`.