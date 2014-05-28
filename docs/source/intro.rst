======================
Introduction to MOTECH
======================

Mobile Technology for Community Health (MOTECH) is a modular, extensible open source software project originally designed for mobile health (mHealth), which can also be used outside of the health domain. It allows organizations to use mobile technology to communicate information to patients, collect data about patients, alert caregivers to patients’ status, and schedule caregivers’ work. The modular system allows organizations to choose among multiple mHealth technologies and to enable data sharing for users of the system.

MOTECH Architecture Overview
============================

MOTECH consists of the core platform and several modules, each providing use of a technology such as SMS or email, or access to an external system such as CommCare. Organizations can choose to install one or more modules, and developers can extend MOTECH by writing new modules. MOTECH is written in Java. It depends on open source systems including Apache Tomcat, Apache ActiveMQ, and Quartz. For more information about MOTECH architecture, see :doc:`architecture/core_architecture` and :doc:`architecture/modules_architecture`.

What can the MOTECH Platform do?
================================

The MOTECH Platform can be used for setting appointments, tracking any scheduled activity, and managing workers deployed in the field. Its initial implementations have been for mHealth projects that improve health by sending messages to patients and caregivers based on an evaluation of the recommended schedule of care compared to the patient’s health-related actions. Some features of typical MOTECH-based applications are:

Communicate information to patients via voice or SMS according to a schedule of care defined for the patient’s condition, e.g.:

* Reminders for ANC appointments, lab visits, etc.
* Reminders to take medication on schedule, e.g., DOTS, ART, etc.
* Reminder notices to take children for scheduled immunization services

Collect data from patients or caregivers, e.g.:

* Patients report their symptoms prior to treatment or during treatment (adverse events)
* Patients give feedback on service delivery
* Caregivers report what service was delivered to a patient and on what date

Alert caregivers of the status of their patients, e.g.:

* Notify Community Health Worker when patient has not taken ART, DOTs or other drugs
* Notify nurse when patient has not kept a scheduled appointment (e.g., ANC visit)

Facilitate communication between patients, caregivers, and/or health administrators, e.g.:

* Establish secure peer networks for patients who share similar health concerns
* Initiate conversations between patients and caregivers in a way that allows the caregiver to manage the workload effectively
