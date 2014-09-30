=======
Roadmap
=======

This page describes the high-level roadmap for the next few MOTECH Platform releases. The specific features that comprise the releases listed below may be rescheduled as additional information comes to light. For more granular and up-to-date information about release plans, click on the issue tracker links for each of the releases below.

Version 0.25 - Fall 2014
========================

`Issue Tracker <https://applab.atlassian.net/browse/MOTECH/fixforversion/16840/?selectedTab=com.atlassian.jira.jira-projects-plugin:version-issues-panel>`_

MOTECH Data Services Performance
--------------------------------

The goal of this effort will be to ensure that MDS performance is as good as (or better than) MOTECH running on CouchDB. We will begin by conducting performance testing and benchmarking of MDS against CouchDB to find performance bottlenecks. These will be prioritized, and the most important ones will be fixed for this release (others may be postponed to future releases).

MOTECH Scale Testing
--------------------

A test environment will be created to simulate MOTECH running at scale. Once this environtment is created, we will test performance of MOTECH under various configurations including clustering. Through this process, we expect to identify, document, and/or fix specific issues discovered, including making clustered mode operational and performant, as well as providing configuration recommendations for ActiveMQ at scale.

IVR Support
-----------

The legacy MOTECH IVR modules were deprecated in the 0.24 release. Starting with version 0.25, there will be two IVR modules - one for VXML/CCXML, and one for Verboice.

MDS REST API Generation
-----------------------

MOTECH will automatically generate REST APIs for CRUD operations on entities defined in MOTECH Data Services.

Version 0.26 - Late 2014
========================

`Issue Tracker <https://applab.atlassian.net/browse/MOTECH/fixforversion/17440/?selectedTab=com.atlassian.jira.jira-projects-plugin:version-issues-panel>`_

DHIS2 Module
------------

A new module will be created to support pushing individual level anonymous data (DHIS2 Event Capture) to DHIS2. DHIS2 data push will be exposed as a new Action through MOTECH Tasks. Support for additional DHIS2 use cases will likely come in future releases.

ETL
---

We will provide a demo and documentation illustrating how to export MOTECH data using one or more popular ETL tools to a star schema for reporting using Pentaho, Jasper or other BI toolkit.

Version 1.0 - 2015
==================

`Issue Tracker <https://applab.atlassian.net/browse/MOTECH/fixforversion/15741/?selectedTab=com.atlassian.jira.jira-projects-plugin:version-issues-panel>`_

Stable Semantic Versioning
--------------------------

We will apply a semantic version scheme to MOTECH Platform and all modules thus making it easier to determine backward compatibility. We will stabilize our core system and API providing implementers with a level of confidence that their system will be compatible with future releases of MOTECH.

End User Install
----------------

End users should be able to install MOTECH without compiling. Install should be scriptable and unattended. We will provide an example install script that operations engineers may reuse or modify as desired for their purposes.

Platform API Documentation
--------------------------

Every public API will be documented with standard javadoc that is published with each release.

API Sanitization
----------------

The process of cleaning up MOTECH's public API - which was started in v0.24 - will be completed, resulting in a stable public API for MOTECH.
