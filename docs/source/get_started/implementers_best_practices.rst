============================
Implementers' best practices
============================

.. contents:: Table of Contents
   :depth: 2

The goal of this document
=========================

The goal of this document is to provide a set of tips, instructions and good practices for implementations built on
top of the MOTECH platform or using MOTECH in the backend. The content of this document has been built based on
our experiences with the implementations and the MOTECH platform in general.


Hardware recommendations
========================

The hardware requirements will vary, depending on the amount and type of requests that your instance will serve at the same time
and the amount of data, that you plan to store. This includes the installed bundles, log files and the database. We
recommend that the instance running the MOTECH server has got at least 4GB of RAM. Lower amounts may considerably
slow down the processing.

Of course the best way to find out the optimal configuration is to perform some stress testing. There are a lot of tools
that can simulate the traffic to the server. Some examples are `Apache JMeter <http://jmeter.apache.org/>`_ or
`The Grinder <http://grinder.sourceforge.net/>`_. Running such stress tools on servers with various configurations will help to
determine at which point adding more resources stops adding up to the amount of processed requests.


Topology recommendations
========================

MOTECH can run in a clustered environment. This means that you can run several MOTECH instances in order to distribute
the workload across multiple processing units. Similarly, you can set up multiple ActiveMQ instances that handle MOTECH
events. In case one of the ActiveMQ instances goes down for any reason, the whole system remains functional, thanks to the failover
mechanism. In order to make use of failover logic, a failover URI should be specified. You can read more about `failover mechanism
in ActiveMQ documentation <http://activemq.apache.org/failover-transport-reference.html>`_.

In a topology with several MOTECH instances, you can choose whether to have the event handled by one instance
only or, what is a rare case, by all instances. Using the first option (posting to a queue), the ActiveMQ event queue serves
as a load balancer between multiple MOTECH instances. The second option (posting to a topic) is usually only used to notify the
instances about some changes that must be immediately reflected on all of the instances. Finally, it is also possible to have clustered databases.
There are two approaches when it comes to clustering databases. In the master-master model, all of the databases can update
the data and the changes get replicated across all of the instances. In the master-slave model, only one of the databases
(considered master) handles data updates and pushes the updated data to the slave servers. Of course using clustered
environment is not a requirement. You can use single MOTECH, ActiveMQ and database instance, depending on the expected
traffic.


Releases
========

Major MOTECH releases usually do not come periodically. The release date is dictated by the amount of tickets assigned
to next versions and the time it takes to resolve them. Each new major release is announced on our `mailing list <https://groups.google.com/forum/?fromgroups#!forum/motech-dev>`_.
We strongly recommend using stable MOTECH versions.

In case the MOTECH version your implementation uses contains some bug, that seriously impacts your implementation,
feel free to contact us. We will see if we can deliver the bug fix in form of a point release.


Building modules
================

Building implementations is often connected with the need to create additional modules, that will work with MOTECH.
To ease and speed up the process of building a new module, we have prepared a set of maven archetypes, that help
to start developing the module. In simple words, archetypes are "Hello World" MOTECH modules, containing all the
basic configuration and files. You can pick a minimal archetype, that will contain only files and code required to have
a module, or add more features, like module UI and controllers, communication with the database and module configuration
and settings. If you plan to develop modules for your implementation, we highly recommend using our archetypes. You
can :doc:`read more about archetypes in our documentation </development/archetype>`.


Security
========

MOTECH implements several mechanisms to provide security on the application level. The access to the application
or modules requires a login and necessary permissions. The security configuration allows to set up certain elements
of the MOTECH security, like the limit of invalid logins, session timeouts or minimum password lengths. We recommend
taking a close look at the documentation of the MOTECH :doc:`Security Model </architecture/security_model>`.

Despite the application level solutions, there are still several security aspects that must be kept in mind while
implementing web applications. The HTTP protocol does not provide any encryption of the data, therefore no guarantee
can be given that the communication over HTTP is secure. To enhance the security of the communication between the
server and the client browser, a secure protocol, like HTTPS, should be used. The data kept in MOTECH database
is not encrypted as well. Establishing a secure connection with the database lies with the implementation as well.

.. warning::

    Some of MOTECH modules allow integration with other systems. That usually requires providing account data for the external
    system. That data is kept in its plain form in the database or the property file!

One more thing to consider is the security of the property files. The bootstrap configuration is kept in the
.motech/config directory in the user's home directory and contains all the credentials required to access the database.
Securing the access to that file rests with the implementation. We recommend setting strict access rights on the file
and ensuring it cannot be accessed by unauthorized people.


Logs
====

While having logging enabled is a good thing, due to the ability to audit actions and possible server failures, it
must be taken into consideration, that logs can use a considerable amount of disk space. MOTECH allows to control
the logging levels via a property file (log4j.properties) and its Admin UI. Available levels, in the decreasing order of
importance, are: FATAL, ERROR, WARN, INFO, DEBUG and TRACE. It is also possible to set up logging ALL or disable logging
at all, by setting OFF. Setting up logging to a certain level will log all the statements of that level, plus all statements
with the higher level. It means that setting up the logging for WARN level, will log all WARN, ERROR and FATAL statements.

The loggers always have names. In MOTECH, the name of a logger corresponds to the class name it logs from, however for the logs coming
from the external libraries, the convention for logger names can differ. If you want to set up logging for external libraries,
please check their website or documentation for logger names. There's one logger that has got a special function and its name is **root**.
If you have not explicitly provided different level for a logger, then this level is used. Please be cautious when changing
the root logger level, as the amount of loggers in MOTECH and its libraries is huge. The default level is ERROR. We do
no recommend going lower than WARN for the root logger. If you need more detailed logs, set up lower log levels for
concrete loggers.

When developing your own modules to use with MOTECH, we recommend that you remember to set up proper logging in the code.
This helps to debug issues and find problems faster. Try to use proper logger levels when logging information.


Data backup
===========

MOTECH does not provide any tools or support for data backup out of the box. Taking care of performing regular data backups
lies with the implementations. Of course most of the data is kept in the database. Please remember, that MOTECH uses
two databases. One of them is managed by the Motech Data Services module (the main persistence layer in MOTECH). Another
database belongs to the Quartz scheduler and contains scheduler schema and scheduled jobs data. The most popular way
to backup data are SQL dumps, that are complete snapshots of the current state of the database. Such snapshots can
be used to restore database from scratch at any time. The creation of such backups can be easily automatized, by having
a script that runs periodically and creates necessary snapshots.

Besides databases, MOTECH can store some data in property files and read configurations from environment variables.
The bootstrap configuration, containing database and ActiveMQ access data resides in .motech/config or is read from
environment variables. MOTECH settings reside in .motech/config directory or database, depending on the chosen configuration source.
Moreover, if the configuration mode has been set as FILE, the .motech/config directory will also contain property files of all
modules, that store some settings. Depending on your implementation, you might want to backup this directory as well.


Get in touch
============

In case of any questions or problems during the implementation, feel free to contact us via `mailing list <https://groups.google.com/forum/?fromgroups#!forum/motech-dev>`_.
We will be happy to hear about your implementation and will try to resolve your doubts.