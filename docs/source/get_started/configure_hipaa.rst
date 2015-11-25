=================================================
Configuring Your MOTECH App to be HIPAA Compliant
=================================================

.. contents:: Table of Contents
    :depth: 4

What is HIPAA?
==============

From http://www.hhs.gov/ocr/privacy/hipaa/understanding/srsummary.html:

The Health Insurance Portability and Accountability Act of 1996 (HIPAA) required the Secretary of the U.S.
Department of Health and Human Services (HHS) to develop regulations protecting the privacy and security of certain
health information. To fulfill this requirement, HHS published what are commonly known as the HIPAA Privacy Rule and
the HIPAA Security Rule. The Privacy Rule, or Standards for Privacy of Individually Identifiable Health Information,
establishes national standards for the protection of certain health information. The Security Standards for the
Protection of Electronic Protected Health Information (the Security Rule) establish a national set of security standards
for protecting certain health information that is held or transferred in electronic form. The Security Rule operationalizes
the protections contained in the Privacy Rule by addressing the technical and non-technical safeguards that organizations
called “covered entities” must put in place to secure individuals’ “electronic protected health information” (e-PHI).
Within HHS, the Office for Civil Rights (OCR) has responsibility for enforcing the Privacy and Security Rules with
voluntary compliance activities and civil money penalties.

A unofficial version that presents all regulatory standards in one document (as of March 2013) is available on the website of the
U.S. Department of Health & Human services:

http://www.hhs.gov/ocr/privacy/hipaa/administrative/combined/hipaa-simplification-201303.pdf

Scope of this document
======================

Compliance with HIPAA requires implementing physical and technical safeguards, as well as technical and administrative
policies when it comes to dealing with sensitive patient data. It is impossible for us to help you achieve full compliance,
without knowing the details of your organization or the data you are storing. Ultimately it falls on your and/or your hosts
shoulders to guarantee full HIPAA compliance.

This document focuses on the MOTECH part of the patient - what can be configured and what should be configured in MOTECH
in order to be HIPAA compliant. Take note that this is only a part of the full picture and it is up to you to make sure
you are fully compliant. More information on HIPAA and being compliant can be found on the website of the
U.S. Department of Health & Human Services: http://www.hhs.gov/ocr/privacy/hipaa/understanding/srsummary.html

The Security Rule
=================

The HIPAA Security Rule establishes national standards to protect individuals electronic personal health information
that is created, received, used, or maintained by a covered entity. The Security Rule requires appropriate
administrative, physical and technical safeguards to ensure the confidentiality, integrity, and security of
electronic protected health information.

This section of the document will focus on the technical safeguards that should be configured in MOTECH in order to
achieve HIPAA compliance.

####################
Technical safeguards
####################

Technical safeguards required for HIPAA are divided into standards. Every entity covered by HIPAA must comply with every
standard. However, the HIPAA Security Rule categorizes certain implementation specifications as "addressable", while others
are "required". Covered entities are required to comply with every Security Rule "Standard." However, the Security Rule
categorizes certain implementation specifications within those standards as "addressable," while others are "required."
The "required" implementation specifications must be implemented. The "addressable" designation does not mean that an
implementation specification is optional. However, it permits covered entities to determine whether the addressable
implementation specification is reasonable and appropriate for that covered entity. If it is not, the Security Rule
allows the covered entity to adopt an alternative measure that achieves the purpose of the standard, if the alternative
measure is reasonable and appropriate.

Following are the HIPAA standards that must be adhered to in order to be HIPAA compliant.

########################
Standard: Access control
########################

To be in accordance with standard, only authorized persons or software programs should have access to e-PHI. MOTECH includes
a web-security module, which covers this standard. Following are the implementation specifications for this standard:

Unique user identification (Required)
#####################################

User identity must be tracked by assigning them unique names or numbers.

MOTECH implements a user based access system based on Spring Security. All users have unique user names.
This is provided by default and nothing has to be changed in order for it to function. You can create multiple
user accounts and associate them with different access roles. What you have to do however, is to make sure that all the
users accessing the system have their own unique accounts with correct access permissions - remember that only authorized persons
can have access to e-PHI.

Since MOTECH also provides a configurable security rule system, you must make sure that no parts of the system allowing
any kind of access to ePHI can be accessed without authentication and authorization.

If you are implementing your own modules, you must make sure to secure all HTTP endpoints that require specific permissions
using Spring Security measures, preferably annotations but other ways of doing it are also acceptable. If you add a new endpoint
without adding any security safeguards, then by default it still will be restricted to only authenticated users (all authenticated users,
which very often is not what you should do).

Refer to the :doc:`Security Model </architecture/security_model>` documentation for more information.

You should also make sure to secure your MDS entities using appropriate annotations so that only authorized users can
access them through the MDS Data Browser. Refer to the :std:ref:`Security section of the MDS documentation <security>`
for information on securing your entities.

Emergency access procedure (Required)
#####################################

Establish (and implement as needed) procedures for obtaining necessary electronic
protected health information during an emergency.

This is something you should define on your based on you infrastructure and/or hosting solution. All data stored by MOTECH
goes to the database, so if you are using MDS for storing you e-PHI, you should develop a plan for emergency access to
the database.

Automatic logoff (Addressable)
##############################

Implement electronic procedures that terminate an electronic session after a predetermined time of inactivity.

MOTECH has a setting that allows controlling this. By default the session timeout after inactivity is set to 30 minutes,
but can configured (or turned off if set to 0) in the MOTECH settings. Set the timeout to an appropriate value for your
situation.

Refer to the :doc:`Security Model </architecture/security_model>` documentation for more information on this setting.

Encryption and decryption (Addressable)
#######################################

Implement a mechanism to encrypt and decrypt electronic protected health information.

MOTECH does not provide any out of the box solution for this. It is up to you to either configure encryption and you have
a few options for achieving this. The first one is to do this manually in your code - you would have to manually encrypt
and decrypt all sensitive data going in and out of the database.

You can also use encryption on the database level, by either using database features or by placing the database data
directory on an encrypted disk. Refer to this documentation for `PostgreSQL <http://www.postgresql.org/docs/9.3/static/encryption-options.html>`_.
For MySQL you can use full disk encryption or third party products, more information can be found in this
`Article <http://www.porticor.com/2012/05/mysql-cloud-encryption/>`_.

########################
Standard: Audit controls
########################

Implement hardware, software, and/or procedural mechanisms that record and examine activity in information systems that
contain or use electronic protected health information.

If you are using MOTECH Data Services for persistence, then you are given a history recording feature for your data out of box.
It is not enabled for new entities by default, so you will have to turn it on, either through UI or the @Entity annotation.
The history feature will track all changes to an entity and record them in a separate table in the database. This history
can be viewed through the MDS UI.

MDS also stores the last modification date and the modification author in the entity table itself - this is always
turned on and gives you a view on who made changes to the given object.

The last login date for each user is recorded and stored in the database.

Moreover MOTECH implements a comprehensive logging system based on SLF4J that allows you to track the functioning of
the system. The suggest logging level for operations is INFO, but you can tweak the logging levels of different components
as you see fit, even during runtime.

A status message API for posting status messages that get persisted in the database is also exposed by the admin module
and can be leveraged.

When you develop your own application, you should make sure you have enough logging statements to trace activity in the
application.

###################
Standard: Integrity
###################

Implement policies and procedures to protect electronic protected health information from improper alteration or
destruction.

Mechanism to authenticate electronic protected health information (Addressable)
###############################################################################

Implement electronic mechanisms to corroborate that electronic protected health information has not been altered or
destroyed in an unauthorized manner.

This ultimately falls on your shoulders, since you know your data. Take note that
MDS allows easy usage of javax.validations - you can set them up across your entities to validate the data being persisted
automatically. If a validation fails, the object will not be persisted and you will be notified about that in the logs.
If you need more advanced validation measures, then it is up to you to implement them.

#########################################
Standard: Person or entity authentication
#########################################

Implement procedures to verify that a person or entity seeking access to electronic protected health information is
the one claimed.

This is covered by MOTECH user based access system. Users identify with a password and are given roles composed of permissions.
These permissions can be used to restrict to which parts of the system he has access to.

Refer to the :doc:`Security Model </architecture/security_model>` documentation for more information.

###############################
Standard: Transmission security
###############################

Implement technical security measures to guard against unauthorized access to electronic protected health information
that is being transmitted over an electronic communications network.

Integrity controls (Addressable)
################################

Implement security measures to ensure that electronically transmitted electronic protected health information
is not improperly modified without detection until disposed of.

This means using encryption for all e-PHI data being sent over the wire. This includes, but is not limited to HTTP
communication, database communication (if it's over a network), emails and so forth. Since MOTECH runs on Tomcat,
it can be configured to use communication level cryptographic protocols such as TLS or SSL. This is described in the
`Apache Tomcat SSL/TLS Configuration HOW-TO <https://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html>`_.

MOTECH can be also configured to connect to the database using SSL.

This is important to note when communicating with outside services such as the IVR provider, SMS provider, the SMTP server
for sending emails, outside systems such as OpenMRS or Commcare and so on. When using these services that are not the same
host as the MOTECH server, make sure that use `HTTPS` instead of plain `HTTP` or in other cases, such as the email server,
make sure that proper cryptographic configuration is being used - this is not configured out of the box.

Encryption (Addressable)
########################

Implement a mechanism to encrypt electronic protected health information whenever deemed appropriate.

As mentioned, MOTECH does not provide out of the box encryption utils. You can implement your own encryption methods using
`JAVA cryptography <https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html>`_ or any other tool.

The Privacy Rule
================

The HIPAA Privacy Rule establishes national standards to protect individuals’ medical records and other personal
health information and applies to health plans, health care clearinghouses, and those health care providers that conduct
certain health care transactions electronically.  The Rule requires appropriate safeguards to protect the privacy of
personal health information, and sets limits and conditions on the uses and disclosures that may be made of such
information without patient authorization. The Rule also gives patients rights over their health information,
including rights to examine and obtain a copy of their health records, and to request corrections.

It is up to you as an implementer and an entity covered by HIPAA to adhere to the privacy rule. You have to take care
to always send the required minimum of data at all times. Moreover the rule covers rules around disclosing PHI, sharing it
with your business partners and so on.

You can find more information looking at the `Guidance on Significant Aspects of the Privacy Rule <http://www.hhs.gov/ocr/privacy/hipaa/understanding/coveredentities/privacyguidance.html>`_
and the `Guidance in de-identification of PHI in accordance with HIPAA: <http://www.hhs.gov/ocr/privacy/hipaa/understanding/coveredentities/De-identification/guidance.html>`_.
