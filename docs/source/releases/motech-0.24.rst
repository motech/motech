==========================
Version 0.24 Release Notes
==========================

**Release Date:** September 3, 2014

Release Summary
===============

The 0.24 release is primarily dedicated to removing MOTECH's dependency on CouchDB, as well as enhancing MOTECH Data Services (MDS) to make it usable as the data layer for most MOTECH applications. All modules have now been migrated to MDS, with a few exceptions noted below. One notable enhancement to MDS in this release is the support for relationships between entities (1:1, 1:many, master-detail), in order to enable a number of the module migrations.

This release also features some consolidation of our code repositories (details below) and deprecation of a few modules. The Platform modules were also refactored to reduce their public surface area. Three new modules have been developed: mTraining, Batch, and Hub.

Where to Get it
===============

**Source Code:** `Platform <https://code.google.com/p/motech/source/list?name=motech-0.24>`_ | `Modules <https://github.com/motech/modules/tree/modules-0.24>`_

**Binary Distribution**

`Platform WAR <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/motech-platform-server/0.24/motech-platform-server-0.24.war>`_

Modules:

* `Alerts <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/alerts/0.24/alerts-0.24.jar>`_
* `Appointments <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/appointments/0.24/appointments-0.24.jar>`_
* `Batch <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/batch/0.24/batch-0.24.jar>`_
* `CMS Lite <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/cms-lite/0.24/cms-lite-0.24.jar>`_
* `CommCare <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/commcare/0.24/commcare-0.24.jar>`_
* `Event Logging <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/event-logging/0.24/event-logging-0.24.jar>`_
* `Hindi Transliteration <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/hindi-transliteration/0.24/hindi-transliteration-0.24.jar>`_
* `HTTP Agent <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/http-agent/0.24/http-agent-0.24.jar>`_
* `Hub <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/hub/0.24/hub-0.24.jar>`_
* `Message Campaign <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/message-campaign/0.24/message-campaign-0.24.jar>`_
* `mTraining <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/mtraining/0.24/mtraining-0.24.jar>`_
* `OpenMRS 1.9 <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/openmrs-19/0.24/openmrs-19-0.24.jar>`_
* `Pill Reminder <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/pill-reminder/0.24/pill-reminder-0.24.jar>`_
* `Schedule Tracking <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/schedule-tracking/0.24/schedule-tracking-0.24.jar>`_
* `SMS <http://nexus.motechproject.org/content/repositories/releases/org/motechproject/sms/0.24/sms-0.24.jar>`_

Major Changes
=============

Modules Migrated from CouchDB to MDS
------------------------------------

All MOTECH modules (with the exception of the IVR modules mentioned later) are now using MOTECH Data Services for data storage and retrieval. Any modules that are used in MOTECH implementations should likewise be migrated from CouchDB to MDS. The best reference for using MDS at this time is the source code for the existing modules; soon we will provide developer-focused :doc:`documentation for MDS <../../get_started/model_data>`.

New MDS Features
----------------

A number of new features were added to MDS in order to support migration of the existing modules. These features include:

* Support for relationships among MDS entities. This includes 1:1, 1:many, and master-detail relationships (bi-directional and many:many relationships are expected in a future release)
* UI support for browsing and restoring items from Trash
* UI support for Filters
* Support for additional types: Locale, Date, File, Map
* Various UI changes to improve the schema editor and data browser

Repository Consolidation
------------------------

In order to simplify development, some of the MOTECH source code repositories have been merged. Rather than maintaining four separate repositories for MOTECH source code (motech, platform-campaigns, platform-communcations, and platform-medical-records), there are now two repositories: motech and modules. Please see the :doc:`MOTECH Code Repositories <../../development/repositories>` topic for more information.

Modules Moved to motech-contrib
-------------------------------

As part of the repository cleanup effort, a few modules were moved out to the motech-contrib repository. The specific modules that were chosen are those that have very few (or in some cases no) consumers. These modules will no longer be maintained by Grameen Foundation, but MOTECH implementers are welcome to continue using them and either fork the code or submit pull requests to the repo as needed. The list of migrated modules is as follows:

* Event Aggregation
* Mobile Forms
* OpenMRS OMOD
* Outbox

New Modules
-----------

Several new modules were developed as part of this release:

* **mTraining** - The mTraining module provides data containers and APIs for defining training courses and tracking user enrollment and progress. A typical use case might be an IVR-based training course for a health worker.
* **Batch** - The Batch module is an implementation of Spring batch (version:3.0.0.M3). It essentially deals with scheduling triggering of jobs.
* **Hub** - The Hub module is the implementation of Google's `PubSubHubbub Hub 0.4 Specifications <https://pubsubhubbub.googlecode.com/git/pubsubhubbub-core-0.4.html>`_. It exposes an API so other modules can act as publisher and make contents available to it for distribution whenever there is an update in a topic.

Legacy IVR Modules Deprecated - Replacements Coming
---------------------------------------------------

The following modules have been deprecated:

 * Call Flow
 * Decision Tree
 * IVR (including API, Asterisk, Kookoo, Verboice, and Voxeo)

These modules are still present in the source code repository, but they were not built as part of the release. If you need to use one or more of these modules, you can build each module that you require by executing **mvn install** from the module's base directory. Note that these modules depend on CouchDB.

These legacy modules will be replaced in 0.25 by new generic modules for handling VXML and CCXML. There will also be a re-worked version of the Verboice module in a coming release that removes the dependencies on call-flow and decision-tree.

Known Issues
============

* `MOTECH-818 <https://applab.atlassian.net/browse/MOTECH-818>`_ - Able to remove a field from a lookup even if the lookup is being used in a Task

    **Summary:** User should not be able to remove a field from an MDS Lookup if the Lookup is used as a data source in a Task. Currently this is not prevented.

    **Workaround:** When modifying a Lookup, the user will need to verify manually that it is not being used as a data source in a Task (this can be checked via the Tasks UI).

* `MOTECH-1084 <https://applab.atlassian.net/browse/MOTECH-1084>`_ - MDS ComboBox UI Bugs

    **Summary:** There are some problems with combobox fields when we add two or more of them to an MDS entity:

    1. After opening instance view in Data Browser we can see error message "This field is required" under dropboxes of all comboboxes (except for the first one) even though they aren't.
    2. After clicking "Add option" button and filling text field with any value on more than one combobox, when we click "Save" for any of them, save buttons for all others will just gray out and turn off. They'll become active again when we enter anything in text fields in any combobox.

    **Workaround:**

    1. A number of workarounds may exist depending on the nature of your application. For example, one could create a dummy default option for the ComboBox with a name like "Empty" or "No Value" when defining the ComboBox field.
    2. Enter anything in the text fields of any combobox, and the "Save" buttons will become active again.

* `MOTECH-1125 <https://applab.atlassian.net/browse/MOTECH-1125>`_ - Getters starting with "is" are not recognized by the MDS annotation processor

    **Summary:** The MDS annotation processor should recognize boolean getters, starting with "is", eg. for field "completed", the getter method "isCompleted()" should be recognized. Currently, it seems to only recognize getters starting with "get".

    **Workaround:** This issue may be temporarily avoided by prefixing getters on MDS entity classes with "get".

* `MOTECH-1147 <https://applab.atlassian.net/browse/MOTECH-1147>`_ - Default value for Date type fields doesn't work

    **Summary:** Create an entity and add a Date type field. Set a default value to any date and save changes. Notice that the default value field is clear and when you add an instance of that entity, there's no default value inserted.

    **Workaround:** When using an MDS Entity with a field of type Date, the values for all Date fields will need to be set explicitly.

* `MOTECH-1153 <https://applab.atlassian.net/browse/MOTECH-1153>`_ - Creating an MDS entity with an enum field fails if the enum has many values

    **Summary:** Attempting to create an entity that has an enum field (allow user supplied option disabled), that has got many values causes failures. This does not happen when user supplied option is enabled (as it creates a list, instead of enum).

    **Workaround:** Some possible workarounds for this issue (depending on the nature of the application) include:
    - Enabling user supplied values on enum fields, if appropriate for the application
    - If possible, splitting the enum into multiple enums

* `MOTECH-1156 <https://applab.atlassian.net/browse/MOTECH-1156>`_ - Error when adding MDS Entity with space in name

    **Summary:** If a user adds an Entity with spaces in the name then there is an error. After that it is impossible to add other Entities. Entity name should be validated against spaces in the name or they should be deleted.

    **Workaround:** Avoid spaces in Entity names.

Tickets
=======

You can browse the list of tickets resolved for this release on our `issue tracker <https://applab.atlassian.net/issues/?jql=fixVersion%20%3D%200.24%20AND%20project%20%3D%20MOTECH>`_.
