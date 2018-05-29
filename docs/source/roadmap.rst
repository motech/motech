=======
Roadmap
=======
This page describes the high-level roadmap for the MOTECH system as we move toward the release of version 1.0. For more granular and up-to-date information about release plans, click on the issue tracker links for each of the releases below.

Version 0.30 - Release date: November 2016
=====================================

`Issue Tracker <https://applab.atlassian.net/browse/MOTECH-2913?jql=project%20%3D%20MOTECH%20AND%20fixVersion%20%3D%200.30>`_

Version 0.30 focuses on improving the CommCare module, enhancing OpenMRS integration and improving tasks based workflows.

Module Enhancements
-------------------
    - CommCare module
        - Integrate with the new UCR API, allowing users to query custom reports and act on each row that's returned
    - OpenMRS module
        - Query Bahmni specific endpoints in the tasks module
        - Add the ability to create complex visits, encounters and observations in OpenMRS from a MOTECH task. The creation of simple encounters with observations is currently supported.

Version 1.0 - Release date: TBD
===================================

`Issue Tracker <https://applab.atlassian.net/browse/MOTECH-2902?jql=project%20%3D%20MOTECH%20AND%20fixVersion%20%3D%20%22MOTECH%201.0%22>`_

Stable Semantic Versioning
--------------------------
We will apply the `open semantic version scheme <http://semver.org/>`_ to the MOTECH Platform and all modules thus making it easier to determine backward compatibility. We will stabilize our core system and API providing implementers with a level of confidence that their system will be compatible with future releases of MOTECH. (`MOTECH-585 <https://applab.atlassian.net/browse/MOTECH-585>`_)

Platform and Module API Standardization and Documentation
---------------------------------------------------------
Every public API will be standardized and documented with standard javadoc that is published with each release. (`MOTECH-1466 <https://applab.atlassian.net/browse/MOTECH-1466>`_)

Consistent User Interface Elements
----------------------------------
Developers across the globe have contributed UI code to MOTECH. We will make UI elements consistent across the platform to ease implementation and prepare for UI improvements after the MOTECH 1.0 release. (`MOTECH-2008 <https://applab.atlassian.net/browse/MOTECH-2008>`_)

Beyond 1.0 - Release date: TBD
============================
We are currently focusing on our current release and will reevaluate the post 1.0 issues at a later time. Below are our issue trackers for all issues beyond version 1.0

`MOTECH Post 1.0 <https://applab.atlassian.net/browse/MOTECH-2854?jql=project%20%3D%20MOTECH%20AND%20fixVersion%20in%20%281.1%2C%20Backlog%2C%20%22MOTECH%20Post-1.0%22%29>`