==========================
Version 0.29 Release Notes
==========================

**Release Date:** August 25, 2016

Release Summary
===============

**The 0.29 release improves the stability of the 0.28 release, adds much deeper integration with OpenMRS and a new integration with the RapidPro system.

Where to Get it
===============

**Source Code:** `Platform <https://github.com/motech/motech/tree/motech-0.29>`_ | `Modules <https://github.com/motech/modules/tree/modules-0.29>`_

**Binary Distribution**

`Platform WAR <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/motech-platform-server/0.29/motech-platform-server-0.29.war>`_

Modules:

* `Alerts <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/alerts/0.29/alerts-0.29.jar>`_
* `Appointments <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/appointments/0.29/appointments-0.29.jar>`_
* `Atom Client <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/atom-client/0.29/atom-client-0.29.jar>`_
* `Batch <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/batch/0.29/batch-0.29.jar>`_
* `CMS Lite <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/cms-lite/0.29/cms-lite-0.29.jar>`_
* `CommCare <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/commcare/0.29/commcare-0.29.jar>`_
* `CSD <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/csd/0.29/csd-0.29.jar>`_
* `DHIS2 <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/dhis2/0.29/dhis2-0.29.jar>`_
* `Event Logging <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/event-logging/0.29/event-logging-0.29.jar>`_
* `Hindi Transliteration <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/hindi-transliteration/0.29/hindi-transliteration-0.29.jar>`_
* `HTTP Agent <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/http-agent/0.29/http-agent-0.29.jar>`_
* `Hub <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/hub/0.29/hub-0.29.jar>`_
* `IHE Interop <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/ihe-interop/0.29/ihe-interop-0.29.jar>`_
* `IVR <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/ivr/0.29/ivr-0.29.jar>`_
* `Message Campaign <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/message-campaign/0.29/message-campaign-0.29.jar>`_
* `Metrics <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/metrics/0.29/metrics-0.29.jar>`_
* `mTraining <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/mtraining/0.29/mtraining-0.29.jar>`_
* `ODK <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/odk/0.29/odk-0.29.jar>`_
* `OpenMRS <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/openmrs/0.29/openmrs-0.29.jar>`_
* `Pill Reminder <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/pill-reminder/0.29/pill-reminder-0.29.jar>`_
* `Rapid Pro <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/rapidpro/0.29/rapidpro-0.29.jar>`_
* `Schedule Tracking <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/schedule-tracking/0.29/schedule-tracking-0.29.jar>`_
* `SMS <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/sms/0.29/sms-0.29.jar>`_

Major Changes
=============

The following major changes were made in this release:

* Atom Client - Added an atom client module to be able to read atom feeds and fire tasks when there are changes in the feed.
* CommCare - Fixed a number of bugs with the CommCare integration that were caused by the upgrade to Java 1.8, added the the ability to refresh a CommCare application's metadata at the push of a button and added the abilility to access form XML attributes in the tasks module.
* DHIS2 - Updated connectivity to the latest version of the DHIS2 web API.
* IHE-Interop - Added basic authentication to the module, allowing to post to authenticated endpoints.
* OpenMRS - Changed the name from OpenMRS1.9 to OpenMRS because we now support the most recent API changes made in versions of OpenMRS Platform 1.x and 2.0. We added the ability to pull cohort queries from the OpenMRS reporting module, triggering a task for each member returned from the query. We added the capability to connect to multiple OpenMRS servers. We added the ability to query patient relationships, create/update program enrollments and get valid identifiers from the IDGen web services module.
* RapidPro - We added basic connectivity to RapidPro, allowing us to create, update and delete contacts. Deeper integration will be available in the 0.30 release.
* Tasks - Added the ability to define a retry schedule for each task through the UI as well as the ability to push a button to retry a specific failed task. This improves our ability to connect to systems with intermittent internet connectivity.

Tickets
=======

You can browse the list of tickets resolved for this release on our `issue tracker <https://applab.atlassian.net/browse/MOTECH-2864?jql=project%20%3D%20MOTECH%20AND%20fixVersion%20in%20%280.29.1%2C%200.29%29>`_.