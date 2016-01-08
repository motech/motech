====================
Modules Architecture
====================

Modules within MOTECH are self-contained bundles of functionality that are loaded into the server via the OSGi host. Modules interact with the core platform through its APIs and with other modules, either through their APIs or by consuming their events. Modules can expose service interfaces of their own as well as emit their own events. Modules may also register servlet controllers, which allow them to respond to HTTP requests.

Through MOTECH Data Services, a modules may expose entities from its data model. This allows a module to provide a data editor, REST APIs, record-level security, and field-level auditing. Via the Tasks system, modules can expose triggers, data and actions to be orchestrated by other modules. 

See :doc:`../modules/index` for the list of current and planned modules.

Reasons to create a module include developing application-specific UI, business logic, and data models. Another reason is to develop generic reusable functionality to share with the MOTECH community.

The MOTECH war already contains all platform modules required for operation and two additional modules: Admin Module and :doc:`/modules/scheduler`.
The Admin Module allows installing additional modules at runtime, either by uploading a module jar or by selecting a module to be downloaded from our repository.
Except from war modules, all MOTECH modules live in ``~/.motech/bundles``. Module jars placed in that directory will be loaded by MOTECH at startup, if a module placed there has
the same `Bundle-Version <http://wiki.osgi.org/wiki/Bundle-Version>`_ and `Bundle-SymbolicName <http://wiki.osgi.org/wiki/Bundle-SymbolicName>`_ as a module from the war,
that module will override the platform one.