.. _configuration-system:

====================
Configuration System
====================

This document describes the MOTECH configuration system.

Step 1: Specify Config Locations
================================

Default Behavior
----------------

By default, all configuration files are loaded from one of the following locations::

....${user.home}/.motech/config/ 

For example, if Motech runs under the user motech in Linux, configuration files will be searched in **“/home/motech/.motech/config”** folder.

Or::

..../etc/motech/

If the configuration files are not found in the above mentioned location, files will be searched in **“/etc/motech”** folder.

Overriding Default Behavior
---------------------------

If you want to override the location where configuration files are loaded from, you have to add config-locations.properties to **${user.home}/.motech** or to **${user.home}/** directory. Otherwise the property file in classpath will be picked up. Default config-locations.properties file::

....config.location= ${sys:user.home}/.motech/config/,/etc/motech/.

where **“${sys:user.home}”** is used to specify the home directory.

Note that, multiple locations can be specified for config.location property, and motech-settings.properties (which contains platform core config) config file will be searched starting from the first location and then falling back to next specified location, if it is not found. The directory in which it is found, is considered as the current config location and all other config files will be looked only in that particular location. For e.g., in the above sample config, if you have *motech-settings.properties* in **/etc/motech/**, then all config files will be searched in **/etc/motech/** location only. You cannot have files in different locations.

Step 2: Bootstrap Configuration
===============================

There are certain properties which are essential for the system to start up. These properties can be defined either in bootstrap.properties file or by environment variables. The properties are:

* db.url – The database connection url, e.g.: localhost:5984
* db.username – The user who has access to the database.
* db.password – Password to connect to the database.
* tenant.id – Optional. Default value is default.
* config.source – Optional. The source from which MOTECH core configuration and all module configurations should be read. Valid config values can be either one of FILE or UI. Default value is UI. 

During startup, the system will look for these configurations in the following locations, falling back in order (that is when the properties are found in any of the following locations, search look up will stop):

1. Config Directory Environment Variable: If MOTECH_CONFIG_DIR environment variable is defined, then the system will load properties from ${MOTECH_CONFIG_DIR}/bootstrap.properties.

2. Config Environment Variables: Config is loaded from one or more environment variables:
    * MOTECH_DB_URL – specifies value for db.url
    * MOTECH_DB_USERNAME - specifies value for db.username
    * MOTECH_DB_PASSWORD – specifies value for db.password
    * MOTECH_TENANT_ID – specifies value for tenant.id
    * MOTECH_CONFIG_SOURCE – specifies value for config.source

3. Location from Property file: bootstrap.properties file is loaded from any one of the locations specified in config-locations.properties file described above.

We are working on a feature in which, if bootstrap.properties is not found in any of the above mentioned locations, a UI will be presented to user after startup, prompting for bootstrap properties.

Step 3: MOTECH Core Config
==========================

There are some system configurations and activemq configurations which are needed to get MOTECH up and running.

* System configurations:
    - system.language - Can take en(English), pl(Polski), es(Spanish), fr(French), it(Italian), sw(Swahili) as values(although only English and Polski are implemented as of now). Optional, default value is en.
    - statusmsg.timeout - Represents the expiration time(in seconds) of messages and notifications in admin UI. Optional, default value is 60.
    - login.mode - Can be repository or openId (case insensitive).
    - provider.name - OpenId? provider name, mandatory in case login mode is openId.
    - provider.url - OpenId? provider url, mandatory in case login mode is openId.

* Security configurations(For more details you should read the :std:ref:`security configuration section <security-configuration>`):
    - security.required.email - Indicates whether you must provide an email address when creating the user.
    - security.failure.login.limit - The permissible number of incorrect login attempts, default value is 0. After this limit is reached the user is blocked. After a successful login counter is reset. If the value is 0 then blocking is inactive.
    - security.session.timeout - The session timeout in seconds, default 30 minutes. After this time session will be closed.
    - security.password.minlength - The minimum length of the password, default 0. if the value is 0 then length checking is disabled.
    - security.password.validator - The password validator, it specify password rules e.g. 1 number, 1 special character. Can take none, lower_upper(at least 1 uppercase and lowercase), lower_upper_digit(at least 1 uppercase, lowercase and digit), lower_upper_digit_special(at least 1 uppercase, lowercase, digit and special character) as values, default none validator is used.

* Activemq configurations:
    - jms.queue.for.events - Queue name to hold motech event messages. Optional, default value is QueueForEvents.
    - jms.topic.for.events - Topic name to hold motech event messages. Optional, default value is TopicForEvents.
    - jms.broker.url - JMS broker URL. Can take failover URLs also. Sample values: tcp://localhost:61616, failover:(tcp://192.168.32.1:61616,tcp://192.168.32.2:61616)?randomize=false
    - jms.maximumRedeliveries - Maximum number of redeliveries in case of any exceptions and a message consumption fails. Optional, default value is 0.
    - jms.redeliveryDelayInMillis - Delay(in seconds) between successive re-deliveries of messages. If delay=d and first exception was raised at time=t, then successive redelivery times are calculated using exponential backoff . i.e. t+d, t+(d*2), t+(d*4), t+(d*8), t+(d*16) and so on, till maximum redelivery count is reached. Optional, default value is 2000.
    - jms.concurrentConsumers - Optional, default value is 1.
    - jms.maxConcurrentConsumers - Optional, default value is 10.
    - jms.session.cache.size - Optional, default value is 10.
    - jms.cache.producers - Optional, default value is false.

Case 1: When ConfigSource is FILE
---------------------------------

Define motech-settings.properties file in any one of the locations defined in config-locations.properties with the above mentioned properties.

Case 2: When ConfigSource is UI
-------------------------------

After server startup, if core settings are not configured already, you will be presented with a startup page which asks for System Language, Queue URL for events, Login Mode and user setup based on login mode. Other activemq settings can be changed in Settings tab after logging in.

Step 4: Module Configurations
=============================

Case 1: When ConfigSource is FILE
---------------------------------

Module specific property files can be added to::

....<config-location-dir>/<module-symbolic-name>/ directory

and any JSON templates/configurations to::

....<config-location-dir>/<module-symbolic-name>/raw/ directory.

A typical example of a motech’s module symbolic name::

....<module-name>

prefixed with “org.motechproject.motech-”.

All these files are monitored for changes. So, any change to these config files at runtime would be detected and saved in DB. Restart the module if required using Manage Modules tab in UI. We are enhancing the config monitor to raise an event in case of config change. This event can be listened by interested modules and take appropriate actions.

Case 2: When ConfigSource is UI
-------------------------------

After server startup, you can find each module having settings UI associated with it in the Manage Modules tab, where you can edit the properties for the module. Also, restart the module if required. We are enhancing the config monitor to raise an event in case of config change.
