==========================
Version 0.28 Release Notes
==========================

**Release Date:** May 4, 2016

Release Summary
===============

**The 0.28 release represents a breaking change from 0.27.** We upgraded to Java 1.8, separated the database and released a number of new modules.

Where to Get it
===============

.. note::
    The links below point to the 0.28.1 release, which is the current dot release from the 0.28.X code branch.

**Source Code:** `Platform <https://github.com/motech/motech/tree/motech-0.28.1>`_ | `Modules <https://github.com/motech/modules/tree/modules-0.28.1>`_

**Binary Distribution**

`Platform WAR <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/motech-platform-server/0.28.1/motech-platform-server-0.28.1.war>`_

Modules:

* `Alerts <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/alerts/0.28.1/alerts-0.28.1.jar>`_
* `Appointments <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/appointments/0.28.1/appointments-0.28.1.jar>`_
* `Atom Client <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/atom-client/0.28.1/atom-client-0.28.1.jar>`_
* `Batch <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/batch/0.28.1/batch-0.28.1.jar>`_
* `CMS Lite <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/cms-lite/0.28.1/cms-lite-0.28.1.jar>`_
* `CommCare <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/commcare/0.28.1/commcare-0.28.1.jar>`_
* `CSD <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/csd/0.28.1/csd-0.28.1.jar>`_
* `DHIS2 <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/dhis2/0.28.1/dhis2-0.28.1.jar>`_
* `Event Logging <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/event-logging/0.28.1/event-logging-0.28.1.jar>`_
* `Hindi Transliteration <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/hindi-transliteration/0.28.1/hindi-transliteration-0.28.1.jar>`_
* `HTTP Agent <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/http-agent/0.28.1/http-agent-0.28.1.jar>`_
* `Hub <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/hub/0.28.1/hub-0.28.1.jar>`_
* `IHE Interop <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/ihe-interop/0.28.1/ihe-interop-0.28.1.jar>`_
* `IVR <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/ivr/0.28.1/ivr-0.28.1.jar>`_
* `Message Campaign <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/message-campaign/0.28.1/message-campaign-0.28.1.jar>`_
* `Metrics <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/metrics/0.28.1/metrics-0.28.1.jar>`_
* `mTraining <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/mtraining/0.28.1/mtraining-0.28.1.jar>`_
* `ODK <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/odk/0.28.1/odk-0.28.1.jar>`_
* `OpenMRS 1.9 <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/openmrs-19/0.28.1/openmrs-19-0.28.1.jar>`_
* `Pill Reminder <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/pill-reminder/0.28.1/pill-reminder-0.28.1.jar>`_
* `Schedule Tracking <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/schedule-tracking/0.28.1/schedule-tracking-0.28.1.jar>`_
* `SMS <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/sms/0.28.1/sms-0.28.1.jar>`_

Major Changes
=============

The following major changes were made in this release:

* Upgraded from Java 1.7 to Java 1.8
* Upgraded Spring from v3.1 to v3.2
* Atom Client - Added an atom client module to be able to read atom feeds and fire tasks when there are changes in the feed.
* CommCare - Added the ability to pull CommCare Supply transactions into MOTECH and push them to DHIS2, Create/Update cases and submit forms to CommCare with task actions
* DHIS2 - Updated connectivity to the latest version of the DHIS2 web API.
* HTTP Client - Added tasks integration to the HTTP client module, allowing to generically post to any java REST endpoint through the tasks module.
* IHE-Interop - Added the ability to create standardized XML templates that integrate with the tasks module, allowing you to send standardized forms based on popular IHE profiles. The core workflow focuses on the capability to send a HL7 CCD to Mirth Connect on an incoming CommCare form.
* Metrics - Added the ability to capture program metrics in a graphite.js server, allowing for dashboarding of information. This module also includes tasks integration that supports the ability to add or subtract counts in graphite.
* MOTECH Data Services - Split the database into motechschema and motechdata, allowing implementers to separate user data from MOTECH infrastructure.
* ODK - Added the ability to receive forwarded forms from ODK Aggregate, KoBo Toolbox and Ona.io based on the xForm standard. This also opens up the ability to collect data on Android handsets with ODK Collect and in web browsers with Enketo as well as scanning paper forms with ODK Scan.
* OpenMRS-19 - Added tasks integration to OpenMRS and improved the ability to create/update patients, providers and submit encounters from MOTECH.
* Scheduler - Added the ability to create end user defined schedules through the interface and integrated with the tasks module so you can trigger tasks on a schedule.

Tickets
=======

You can browse the list of tickets resolved for this release on our `issue tracker <https://applab.atlassian.net/browse/MOTECH-2628?jql=project%20%3D%20MOTECH%20AND%20fixVersion%20in%20%280.28.2%2C%200.28%2C%200.28.1%29>`_.
