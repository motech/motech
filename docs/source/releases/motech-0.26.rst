==========================
Version 0.26 Release Notes
==========================

**Release Date:** September 3, 2015

Release Summary
===============

The 0.26 release introduces two new modules: the CSD module, which implements the Care Services Discovery API; and the DHIS2 module, which may be used to forward data to DHIS2. It also features enhancements to MOTECH Data Services, including UI support for entity relationships and import/export support for schemas.

This release also adds a significant amount of documentation aimed at MOTECH developers and implementers, including documentation for most existing modules.

Where to Get it
===============

.. note::
    The links below point to the 0.26.6 release, which is the current dot release from the 0.26.X code branch.

**Source Code:** `Platform <https://github.com/motech/motech/tree/motech-0.26.6>`_ | `Modules <https://github.com/motech/modules/tree/modules-0.26.6>`_

**Binary Distribution**

`Platform WAR <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/motech-platform-server/0.26.6/motech-platform-server-0.26.6.war>`_

Modules:

* `Alerts <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/alerts/0.26.6/alerts-0.26.6.jar>`_
* `Appointments <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/appointments/0.26.6/appointments-0.26.6.jar>`_
* `Batch <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/batch/0.26.6/batch-0.26.6.jar>`_
* `CMS Lite <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/cms-lite/0.26.6/cms-lite-0.26.6.jar>`_
* `CommCare <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/commcare/0.26.6/commcare-0.26.6.jar>`_
* `CSD <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/csd/0.26.6/csd-0.26.6.jar>`_
* `DHIS2 <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/dhis2/0.26.6/dhis2-0.26.6.jar>`_
* `Event Logging <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/event-logging/0.26.6/event-logging-0.26.6.jar>`_
* `Hindi Transliteration <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/hindi-transliteration/0.26.6/hindi-transliteration-0.26.6.jar>`_
* `HTTP Agent <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/http-agent/0.26.6/http-agent-0.26.6.jar>`_
* `Hub <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/hub/0.26.6/hub-0.26.6.jar>`_
* `IVR <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/ivr/0.26.6/ivr-0.26.6.jar>`_
* `Message Campaign <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/message-campaign/0.26.6/message-campaign-0.26.6.jar>`_
* `mTraining <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/mtraining/0.26.6/mtraining-0.26.6.jar>`_
* `OpenMRS 1.9 <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/openmrs-19/0.26.6/openmrs-19-0.26.6.jar>`_
* `Pill Reminder <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/pill-reminder/0.26.6/pill-reminder-0.26.6.jar>`_
* `Schedule Tracking <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/schedule-tracking/0.26.6/schedule-tracking-0.26.6.jar>`_
* `SMS <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/sms/0.26.6/sms-0.26.6.jar>`_




Major Changes
=============

DHIS2 Module
------------

The newly added :std:ref:`DHIS2 module <dhis2-module>` enables MOTECH to forward data to a DHIS2 server. Individual-level (DHIS "tracker") data may be forwarded in addition to aggregate-level data. One interesting use case that this module enables is forwarding data collected with handsets (e.g. CommCare) to DHIS2 by way of MOTECH. This use case may be configured entirely using UI utilizing the Tasks module to define the mapping between systems.


CSD Module
----------

The newly added CSD module supports connecting MOTECH to a health worker/facility registry that supports the `Care Services Discovery <http://wiki.ihe.net/index.php?title=Care_Services_Discovery>`_ standard.

UI for MDS Object Relationships
-------------------------------

This version also adds support for creating relationships among Data Services entities using the user interface. Relationships may now be defined either via code annotations or via UI using the schema editor. Several types of relationships are supported: one-to-one, one-to-many, many-to-many, and master-detail. You can read more about entity relationships :std:ref:`here <dde-relationships>`.

MDS Schema Import/Export
------------------------

MOTECH adds the ability to import/export MDS entities in version 0.26. This feature allows an implementer to export his/her end-user-defined entities (EUDEs) to a file so they may be imported into another MOTECH server. You can read more about this feature :std:ref:`here <schema_import_export>`.

REST API Documentation
----------------------

MOTECH added the ability to auto-generate :std:ref:`REST APIs for Data Services entities <rest-api>` in version 0.25. Now documentation for these APIs is also auto-generated. To learn more about this, see the :doc:`Automatic REST API documentation UI in MOTECH <../get_started/rest_documentation>` documentation. [#f1]_

Tickets
=======

You can browse the list of tickets resolved for this release on our `issue tracker <https://applab.atlassian.net/issues/?jql=project%20%3D%20MOTECH%20AND%20fixVersion%20in%20(0.26%2C%200.26.1%2C%200.26.2%2C%200.26.3%2C%200.26.4%2C%200.26.5%2C%200.26.6)>`_.

.. rubric:: Footnotes

.. [#f1] And yes, we did just invite you to view documentation describing the auto-generated documentation that describes the auto-generated APIs for manipulating database entities that are auto-generated based on code annotations. Simple, right?
