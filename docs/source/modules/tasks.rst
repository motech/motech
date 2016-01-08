.. _tasks-module:

============
Tasks Module
============

.. contents:: Table of Contents
   :depth: 3

############
Introduction
############

This topic aims to explain the architecture and internal mechanisms of the Tasks module.
If you are looking for the Tasks module usage instructions, please see
the :doc:`Using the Tasks module </get_started/using_tasks/using_tasks>` documentation.


###########
Terminology
###########

Task
####

Defines actions that must be executed when a certain event happens in MOTECH. Each task has got exactly
one :std:ref:`trigger <trigger>`, which determines when the task executes. A task defines one or more :std:ref:`actions <action>`,
which essentially define what happens, when the task is triggered. Moreover, it is possible (but not required) to define one or
more :std:ref:`filters <filters>`, that can limit the task execution by placing some constraints on the triggers.
A task may optionally define one or more :std:ref:`data sources <data_source>`, that allow to fetch some additional
data during task execution and that can be used in task actions or filters. The triggers and actions can be exposed by
any MOTECH module, in the form of a :std:ref:`channel <channel>`.

.. _channel:

Channel
#######

The task channel can be seen as a metadata of a module, that provides information about the task triggers and
task actions that this module exposes. The next chapters define how the Tasks module discovers and registers task channels.

.. _trigger:

Trigger
#######

A trigger holds information about :doc:`MOTECH events </architecture/event_scheduler_architecture>` fired by the module. It contains the event subject and may contain
event parameters, that map directly to the keys, provided in the event payload. The registered triggers can
be selected during task creation. A trigger is an essential part of the task, as it defines when the task executes.

.. _action:

Action
######

An action holds an information about the code that will be executed in response to a trigger. Actions can be specified
either as a method, exposed in the OSGi service or an event, that the module is capable of handling. When the task
executes, the Tasks module will either invoke the specified method or generate and send the necessary event. Actions
are specified in the task channels and can be used during task creation, once discovered by the Tasks module. Task actions
specify parameters, that map directly to the method parameters or keys in the event parameter map, depending on the chosen
way of action handling. Actions are essential part of the task, as they define what should happen when the task is triggered.


################
Context tracking
################

The Tasks module allows other modules to register task channels. To do so, the module must expose
the task channel definition in its resource directory. Task channels can also be marked, using task annotations.
It is the Tasks module job to keep track of modules registration and deregistration and invoke necessary actions.

The module uses OSGi Service Tracker to listen to Spring context registrations, created by the Gemini blueprint extender.
Once a new context becomes available, the Tasks module looks for a file named **task-channel.json** in the resource directory
of the module to which the context belongs to. If such file exists, it gets processed by the ChannelService
and the channel gets registered in the Tasks module. All channel definitions are kept in MDS.
The module is also scanned for the presence of the tasks-related annotations. Annotation processing is performed with
Spring BeanPostProcessor. This effectively means that the Tasks module will only be able to locate the annotations,
if they have been placed in the classes that are Spring beans.


######################
Channel deregistration
######################

Before execution of a task, a validation will be performed to determine whether the bundle exposing the
action is available. In case at least one of the actions cannot be executed due to the lack of the necessary
module, the task will not be executed at all and the flag indicating lack of registered channel will be set.
This is also reflected on the UI, by making background of the tasks that lack required modules transparent
and grayed out.


##############
Event handling
##############

The Tasks module must react to certain events fired by other modules. Subjects of these events are provided during
trigger registration. To achieve this, the Tasks module subscribes itself to listen to the events, using **EventListenerRegistryService**,
provided by the Events module. All of the event listeners are registered to invoke the **handle** method in the
**TaskTriggerHandler** class. Once the handle method gets invoked, it determines the received event subject, looks for
active tasks triggered by the received event and finally attempts to execute all of them.

.. note::

   The Tasks module will not register a listener for trigger subjects until there's at least one task, which
   uses that trigger.


###################
Value manipulations
###################

The Tasks module provides an ability to manipulate values retrieved from the trigger or data provider. There
are several predefined manipulations that are handled, for example substring or capitalize. The manipulations are
performed before the task executes, which means that the action event handler or the OSGi service method or a filter
receives values that have already been modified. The **KeyEvaluator** class is responsible for both
determining the actual value of the provided keys, in the context of currently executed task, as well as
applying user manipulations to these values. Manipulations can be used in filters and task actions. They
are always only valid for a single field they appear in. This means that if uppercase manipulation is applied to the
certain trigger field in a filter, this manipulation will not be taken into account, when using the same trigger field
in the task action. There is no limit on the amount of manipulations that can be used - they will be applied in the
specified order.

While with some browsers it is possible to set manipulations using the Tasks UI, using manipulations programmatically
and with browsers that do not support manipulations UI, requires the user to know their internal text representation.
The values coming from the triggers or data providers are wrapped with double curly braces. The values coming from
the trigger are prefixed with "trigger.", while values from the data source are prefixed with "ad.". To draw an example,
a representation of a trigger field of name "name", would look like: **{{trigger.name}}**.

The manipulations are added either directly via Tasks UI, or by providing their text definition, right after the value.
For example, to capitalize the name field from the trigger: **{{trigger.name?capitalize}}**. As mentioned before, you can
use various manipulations for a single value: **{{trigger.name?capitalize?substring(0,3)}}**, which means that the value
will first get capitalized and then a substring operation will be performed (the order of the manipulations is kept).


.. _data_source:

###########
Data source
###########

Data sources allow to fetch some additional data for the executed task, using predefined lookups, provided by the data providers.
Any module can register a data provider, by implementing the **org.motechproject.commons.api.DataProvider** interface and exposing
it as OSGi service. Such implementations are discovered by the Tasks module and are included in the list of available data
providers, during task creation. The data providers define lookups, that they are capable of handling. These lookups return
instances, based on some criteria, like id or language and name. Users can make use of the objects retrieved with data sources
in the task actions.

The data providers are supposed to provide an implementation of the **toJSON()** method. If the structure of the data
provider does not change during runtime, it is common to define that structure in a json file and simply load that file
in the aforementioned method. The **org.motechproject.commons.api.AbstractDataProvider** abstract class contains some helper
code for resource loading and can be used as an alternative to implementing the DataProvider interface. In case the data
provider structure changes at runtime, the current state of the provider (also in JSON format) must be generated
somewhere in the code, for instance, using a *Velocity template*.


.. _filters:

#######
Filters
#######

Filters allow users to limit the execution of tasks, based on the values present in the trigger or data
source. A single filter can be seen as a single conditional statement. These statements can be grouped into filter sets.
Within single filter set, users can configure whether all or any of the provided conditions must be met. The task
will only be executed if all provided filter sets have their conditions fulfilled. The processing of the filters takes
place in the **TaskFilterExecutor** class. Its **checkFilters** method simply iterates over all specified filter sets and
based on them gives an answer whether the task should be executed or no.


###############
Tasks execution
###############

Tasks execution is performed by the **TaskActionExecutor** class. A single action can be defined in two ways. The first
option is to provide an interface name of a service that is exposed as OSGi service and a method name to call. Another
option is providing an event subject that the module is capable of handling and a list of parameters that can be
included in the event payload.

It is possible to provide configuration for both OSGi service call and sending an event for a single action, but only
one of them will be executed. Calls to OSGi service have priority over events. In case the specified service is unavailable,
an attempt to send an event will be made. If events have not been configured, the **TaskHandlerException** will be raised.
The same exception will be raised if the specified method does not exist in the OSGi service or if provided
list of arguments does not match the method signature.

If the execution of a task fails for any reason, a number of operations will be performed. First of all, the information
about failure will be logged to the MDS, using the **TaskActivityService**, together with the exception that was
thrown during task execution. The Tasks module will then check if the failure count for the task has not reached the
number of allowed errors. If this is the case, the task will be automatically disabled. Finally, an event about task
failure, containing the task name, stacktrace, failure time and a few other potentially interesting information is raised.
The error handling is performed in the **TaskTriggerHandler** class.


#######################
Tasks activity tracking
#######################

The **TaskActivityService** does not only keep track of the task execution failures. In fact, it keeps record of all
task executions and logs successful executions, failed executions and warnings. Thanks to this, the Tasks module is
capable of displaying basic stats for the tasks, like the number of times they have been executed, the time when
they were last triggered or the number of times they have failed, together with the description of the failure. Moreover,
for each successful or failed execution, the Tasks module fires an event. Please see the
:std:ref:`Emitted events <emitted_events>` section, for the respective event subjects.


###################
Tasks import/export
###################

The Tasks module allows the export and import of tasks. The tasks are serialized to and from the JSON representation, by the
Jackson JSON Processor. The JSON representation contains all the required information, to reproduce the state of the task,
including selected trigger, actions, filters and data sources. The task execution history is not included in the JSON representation,
therefore after importing the task on another machine, the activity log will not be available for previous fires. The
code responsible for tasks import and export resides in the **TaskService**.


.. _emitted_events:

##############
Emitted events
##############

+------------------------------------------------------+----------------------------------------------------------------+
|Subject                                               |Additional notes                                                |
+======================================================+================================================================+
|org.motechproject.tasks.*taskName*.success            |Fired when task has been successfully executed.                 |
|                                                      |*taskName* stands for task name, where blank spaces are         |
|                                                      |replaced with hyphen "-"                                        |
+------------------------------------------------------+----------------------------------------------------------------+
|org.motechproject.tasks.*taskName*.failed.*cause*     |Fired when task execution failed.                               |
|                                                      |*taskName* stands for task name, where blank spaces are         |
|                                                      |replaced with hyphen "-"                                        |
|                                                      |*cause* represents cuase of failure and is one of the following:|
|                                                      |trigger, filter, datasource, action                             |
+------------------------------------------------------+----------------------------------------------------------------+
|org.motechproject.message                             |Sends notification to the admin module about disabled task. The |
|                                                      |task name is provided in the event parameters.                  |
+------------------------------------------------------+----------------------------------------------------------------+
|org.motechproject.tasks.channel.update                |Raised when channel gets successfully updated. The name of the  |
|                                                      |bundle, for which the channel got updated is included in        |
|                                                      |the event parameters.                                           |
+------------------------------------------------------+----------------------------------------------------------------+
|org.motechproject.tasks.dataProvider.update           |Raised when data provider gets successfully updated. The name of|
|                                                      |the bundle, for which the channel got updated is included in    |
|                                                      |the event parameters.                                           |
+------------------------------------------------------+----------------------------------------------------------------+

Due to the specific role of the Tasks module, it is also capable of firing any other events, depending on what gets
configured as the task action.


###############
Consumed events
###############

+------------------------------------------------------+----------------------------------------------------------------+
|Subject                                               |Additional notes                                                |
+======================================================+================================================================+
|org.motechproject.tasks.channel.update                |Triggers re-validation of task triggers and actions to ensure   |
|                                                      |that they are still useable after the update.                   |
+------------------------------------------------------+----------------------------------------------------------------+
|org.motechproject.tasks.dataProvider.update           |Triggers re-validation of task data providers to ensure         |
|                                                      |that they are still useable after the update.                   |
+------------------------------------------------------+----------------------------------------------------------------+

Due to the specific role of the Tasks module, it is also capable of handling any other events, depending on what gets
configured in the task trigger.
