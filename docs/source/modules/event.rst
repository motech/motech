.. _event-module:

============
Event Module
============

.. contents::
    :depth: 3

Description
===========

The Event module allows to create a listener, that contains an action, triggered by firing an event. The module is using
:code:`MotechEvent` to provide event with specific data that can be sent by any MOTECH module (using :code:`EventRelay`) when an event is fired.
The module is using ActiveMQ for delivery of the events. It is possible to register your own callback
function using :code:`EventCallbackService`.

MotechEvent
===========

The Event module uses :code:`MotechEvent` to store information about fired event. Instance of this class can be sent by
any module to a specific handler to fire an event. Every module can also be a subscriber of an event and receive events to handle.

Main fields of :code:`MotechEvent`:

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
========

There are 2 ways to create an event listener:

#. Use an :code:`@MotechListener` annotation to a handler function.

    In :doc:`Core Architecture </architecture/core_architecture>` you can find an example usage of this annotation.

#. Use an OSGi :code:`EventListenerRegistry` service exposed by the event module.

Register custom callback
========================

ActiveMQ is sending events with its own retry. The default ActiveMQ retries will take place when the handler method fails (an exception is thrown).
The default retries can be disabled by custom event callback service (flag returned by the failureCallback method). To register
event callbacks you should implement an :code:`EventCallbackService` interface and expose it as OSGi service. Then this service
name must be set in the callback field of the :code:`MotechEvent`.

Example of a custom callback
----------------------------

First you should implement an :code:`EventCallbackService`:

    .. code-block:: java

        @Service('serviceName')
        public class ServiceName implements EventCallbackService {
            public static final String CALLBACK_NAME = "CallbackName";

            @Override
            public boolean failureCallback(MotechEvent event, Throwable throwable) {
                //Actions for failure
            }

            @Override
            public void successCallback(MotechEvent event) {
                //Actions for success
            }

            @Override
            public String getName() {
                return CALLBACK_NAME;
            }
        }

Then expose it as the OSGi service:

    .. code-block:: xml

        <osgi:service id="serviceId" auto-export="interfaces" ref="serviceName"
                  interface="org.motechproject.event.listener.EventCallbackService"/>

After that you can use a callback service in :code:`MotechEvent` by using e.g. such construction:

    .. code-block:: java

        eventRelay.sendEventMessage(new MotechEvent(eventSubject, parameters, ServiceName.CALLBACK_NAME, metadata));
