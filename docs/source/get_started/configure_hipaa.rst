Configuring Your MOTECH App to be HIPAA Compliant
=================================================

.. contents:: Table of Contents
    :depth: 2

What is HIPAA?
==============

From http://www.hhs.gov/ocr/privacy/hipaa/understanding/srsummary.html:

The Health Insurance Portability and Accountability Act of 1996 (HIPAA) required the Secretary of the U.S.
Department of Health and Human Services (HHS) to develop regulations protecting the privacy and security of certain
health information.1 To fulfill this requirement, HHS published what are commonly known as the HIPAA Privacy Rule and
the HIPAA Security Rule. The Privacy Rule, or Standards for Privacy of Individually Identifiable Health Information,
establishes national standards for the protection of certain health information. The Security Standards for the
Protection of Electronic Protected Health Information (the Security Rule) establish a national set of security standards
for protecting certain health information that is held or transferred in electronic form. The Security Rule operationalizes
the protections contained in the Privacy Rule by addressing the technical and non-technical safeguards that organizations
called “covered entities” must put in place to secure individuals’ “electronic protected health information” (e-PHI).
Within HHS, the Office for Civil Rights (OCR) has responsibility for enforcing the Privacy and Security Rules with
voluntary compliance activities and civil money penalties.

In short, HIPAA is a standard for protecting sensitive patient data (PHI).


Scope of this document
======================

Compliance with HIPAA requires implementing physical and technical safeguards, as well as technical and administrative
policies when it comes to dealing with sensitive patient. It is impossible for us to help you achieve full compliance,
without knowing the details of your organization or the data you are storing. Ultimately it falls on your and/or your hosts
shoulders to guarantee full HIPAA compliance.

This document focuses on the MOTECH part of the patient - what can be configured and what should be configured in Motech
in order to be HIPAA compliant. Take note that this is only a part of the full picture and it is up to you to make sure
you are fully compliant. More information on HIPAA and being compliant can be found on the website of the
U.S. Department of Health & Human services: http://www.hhs.gov/ocr/privacy/hipaa/understanding/srsummary.html

Technical safeguards
====================

* Access control - only authorized persons should have access to e-PHI. MOTECH implements a user based access system based
 on Spring Security. This provided by default and nothing has to be changed in order for it to function. You can create multiple
 user accounts and associate them with different access roles. What you have to do however, is to make sure that all the
 users accessing the system have their own unique accounts with correct access permissions - remember that only authorized persons
 can have access to e-PHI.

 Since MOTECH also provides a configurable security rule system, you must make sure that no parts of the system allowing
 any kind of access to ePHI can be accessed without authentication and authorization.

 If you are implementing your own modules, you must make sure to secure all HTTP endpoints that require specific permissions
 using Spring Security measures, preferably annotations but other ways of doing it are also acceptable. If you add a new endpoint
 without adding any security safeguards, then by default it still we restricted to only authenticated users (all authenticated users,
 which very often is not what you should do).

* Audit - access and other activity in information systems that contain or use e-PHI must be recorded and examined. This means
  you should track all changes to e-PHI. If you are using MOTECH Data Services for persistence, then you are given a history
  recording feature for you data out of box. It is not enabled for new entities by default, so you will have to turn on it
  either through UI or the @Entity annotation. The history feature will track all changes to an entity and record them in
  a separate table of the table. This history can be viewed through the MDS UI.

  MDS also stores the last modification date and the modification author in the entity table itself - this is always
  turned on and gives you a view on who made changes to the given object.

  The last login date for each user is recorded and stored in the database.

  Moreover MOTECH implements a comprehensive logging system that allows you to track the functioning of the system.
  The suggest logging level for operations is INFO, but you can tweak the logging levels of different components as you
  see fit, even during runtime.