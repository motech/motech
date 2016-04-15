=======
Roadmap
=======
This page describes the high-level roadmap for the MOTECH system as we move toward the release of version 1.0. For more granular and up-to-date information about release plans, click on the issue tracker links for each of the releases below.

Version 0.28 - Release date: April 2016
=====================================

`Issue Tracker <https://applab.atlassian.net/browse/MOTECH/fixforversion/20840/?selectedTab=com.atlassian.jira.jira-projects-plugin:version-issues-panel>`_

Version 0.28 will represent a breaking change between 0.27. We plan to upgrade a number of core components that will require changes in deployment.

Key Technical Changes
---------------------
The following is a list of key technical changes planned to be implemented in version 0.28:
    - Upgrade to Java 8
    - Upgrade Data Nucleus to version 4.0.1
    - Improve UI build process with Node, Bower, Gulp and SCSS
    - Split database from motech_data_services to motech_data and motech_schema


Module Enhancements
-------------------
    - New IHE Interop Module supports the creation of HL7 CDA documents in MOTECH
    - Improved OpenMRS module with tasks integration
    - New Atom-Client module allows MOTECH to query atom feeds and act on changes in the feed. It will primarily be used in OpenMRS integration to detect when a new patient, encounter or provider has been created. However, this module can work with any atom feed.

Version 0.29 - Release date: June 2016
=====================================

`Issue Tracker <https://applab.atlassian.net/browse/MOTECH/fixforversion/21340/?selectedTab=com.atlassian.jira.jira-projects-plugin:version-issues-panel>`_

Version 0.29 focuses on enhancing OpenMRS workflows for an implementation in Nepal and Mozambique as well as adding new interoperability with iHRIS and OpenLMIS.

Key Technical Changes
---------------------
The following is a list of key technical changes planned to be implemented in version 0.28:
    - Upgrade Spring from version 3.1 to 3.2

Module Enhancements
-------------------
    - OpenMRS module
        - Supporting multiple OpenMRS systems connecting to a single MOTECH server
        - Ensuring MOTECH can communicate with OpenMRS REST Web Services API versions 1.9 through 1.12.
    - New Metrics module that integrates MOTECH with a Graphite Server allowing for server monitoring and the development of Key Performance Indicators.
    - New iHRIS integration that allows MOTECH to send data to iHRIS.
    - New OpenLMIS module provides basic interaction with OpenLMIS.

Version 1.0 - Release date: Q3 2016
===================================

`Issue Tracker <https://applab.atlassian.net/browse/MOTECH/fixforversion/15741/?selectedTab=com.atlassian.jira.jira-projects-plugin:version-issues-panel>`_

Stable Semantic Versioning
--------------------------
We will apply the `open semantic version scheme <http://semver.org/>`_ to the MOTECH Platform and all modules thus making it easier to determine backward compatibility. We will stabilize our core system and API providing implementers with a level of confidence that their system will be compatible with future releases of MOTECH. (`MOTECH-585 <https://applab.atlassian.net/browse/MOTECH-585>`_)

Platform and Module API Standardization and Documentation
---------------------------------------------------------
Every public API will be standardized and documented with standard javadoc that is published with each release. (`MOTECH-1466 <https://applab.atlassian.net/browse/MOTECH-1466>`_)

Consistent User Interface Elements
------------------------------------
Developers across the globe have contributed UI code to MOTECH. We will make UI elements consistent across the platform to ease implementation and prepare for UI improvements after the MOTECH 1.0 release. (`MOTECH-2008 <https://applab.atlassian.net/browse/MOTECH-2008>`_)

Standardized Interoperability
-----------------------------
We are working to expand the IHE interop module to better support standardized interoperability workflows. This includes integrating MOTECH with the `Open eHealth Integration platform <http://80.86.91.51/display/ipf2/Home>`_ and will ultimately open up new standardized workflows that support the majority of IHE profiles and, therefore, OpenHIE workflows.(`MOTECH-1242 <https://applab.atlassian.net/browse/MOTECH-1242>`_)

Post 1.0 - Release date: TBD
============================
We are currently focusing on our MOTECH 1.0 release and will reevaluate the post 1.0 issues at a later time. Below are our issue trackers for Post 1.0

`MOTECH Post 1.0 <https://applab.atlassian.net/projects/MOTECH/versions/16242/?selectedTab=com.atlassian.jira.jira-projects-plugin:version-issues-panel#release-report-tab-body>`_

`Backlog <https://applab.atlassian.net/projects/MOTECH/versions/15740>`_