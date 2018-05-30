==========================
Version 1.0 Release Notes
==========================

**Release Date:** April 26, 2017

Release Summary
===============

**The 1.0 release improves integration with CommCare and OpenMRS. That release also fixed a few bugs.

Where to Get it
===============

**Source Code:** `Platform <https://github.com/motech/motech/tree/motech-1.0>`_ | `Modules <https://github.com/motech/modules/tree/modules-1.0>`_

**Binary Distribution**

`Platform WAR <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/motech-platform-server/1.0/motech-platform-server-1.0.war>`_

Modules:

* `Alerts <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/alerts/1.0/alerts-1.0.jar>`_
* `Appointments <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/appointments/1.0/appointments-1.0.jar>`_
* `Atom Client <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/atom-client/1.0/atom-client-1.0.jar>`_
* `Batch <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/batch/1.0/batch-1.0.jar>`_
* `CMS Lite <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/cms-lite/1.0/cms-lite-1.0.jar>`_
* `CommCare <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/commcare/1.0/commcare-1.0.jar>`_
* `CSD <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/csd/1.0/csd-1.0.jar>`_
* `DHIS2 <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/dhis2/1.0/dhis2-1.0.jar>`_
* `Event Logging <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/event-logging/1.0/event-logging-1.0.jar>`_
* `Hindi Transliteration <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/hindi-transliteration/1.0/hindi-transliteration-1.0.jar>`_
* `HTTP Agent <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/http-agent/1.0/http-agent-1.0.jar>`_
* `Hub <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/hub/1.0/hub-1.0.jar>`_
* `IHE Interop <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/ihe-interop/1.0/ihe-interop-1.0.jar>`_
* `IVR <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/ivr/1.0/ivr-1.0.jar>`_
* `Message Campaign <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/message-campaign/1.0/message-campaign-1.0.jar>`_
* `Metrics <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/metrics/1.0/metrics-1.0.jar>`_
* `mTraining <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/mtraining/1.0/mtraining-1.0.jar>`_
* `ODK <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/odk/1.0/odk-1.0.jar>`_
* `OpenMRS <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/openmrs/1.0/openmrs-1.0.jar>`_
* `Pill Reminder <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/pill-reminder/1.0/pill-reminder-1.0.jar>`_
* `Rapid Pro <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/rapidpro/1.0/rapidpro-1.0.jar>`_
* `Schedule Tracking <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/schedule-tracking/1.0/schedule-tracking-1.0.jar>`_
* `SMS <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/sms/1.0/sms-1.0.jar>`_

Major Changes
=============

The following major changes were made in this release:

* Converted CI to Travis
* Atom Client - Extended reading atom feeds with a pagination.
* CommCare - Interacted with CommCare's UCR API. Added ability to import forms and cases by form/case id.
* OpenMRS - Added the ability to use atom feeds without atom client module. We added the ability to query Bahmni Program Enrollment, query observation by patient UUID and Concept UUID and optional Observation Value, by encounter UUID and Concept UUID and optional Observation Value. Supported the Orders and order types.

Tickets
=======

You can browse the list of tickets resolved for this release on our `issue tracker <https://applab.atlassian.net/browse/MOTECH-3179?jql=project%20%3D%20MOTECH%20AND%20fixVersion%20%3D%200.30>`_.