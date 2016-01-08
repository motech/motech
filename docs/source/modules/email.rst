.. _email-module:

Email Module
============

.. contents::
    :depth: 2

Description
-----------

The Email module allows possibility to send e-mails. Messages are sent via :code:`org.springframework.mail.javamail.JavaMailSender`.
There are three ways to send email:

- Using OSGI Service
- Sending via GUI
- By publishing an event(the event is exposed as an action in tasks)

Configuration
-------------

+-----------------------------+----------------------------+--------------------------------------------------------------------+----------------+
|Property                     |Display name                |Description                                                         |Value           |
+=============================+============================+====================================================================+================+
|mail.host                    |Host                        |SMTP server URL(for example :code:`smtp.domain.com`)                |valid url       |
+-----------------------------+----------------------------+--------------------------------------------------------------------+----------------+
|mail.port                    |Port                        |SMTP server Port                                                    |port value      |
+-----------------------------+----------------------------+--------------------------------------------------------------------+----------------+
|mail.log.address             |Log User Email Address      |If true then logs will be saved with the address of the recipient   |true, false     |
|                             |                            |and the sender                                                      |                |
+-----------------------------+----------------------------+--------------------------------------------------------------------+----------------+
|mail.log.subject             |Log Message Subjects        |If true then logs will be saved with the message subject            |true, false     |
+-----------------------------+----------------------------+--------------------------------------------------------------------+----------------+
|mail.log.body                |Log Message Body            |If true then logs will be saved with the message body               |true, false     |
+-----------------------------+----------------------------+--------------------------------------------------------------------+----------------+
|mail.log.purgeenable         |Purge any record older than |If true then mails logs older than value specified by the           |true, false     |
|                             |                            |:code:`mail.log.purgetime` and :code:`mail.log.purgetimemultiplier` |                |
|                             |                            |params, will be removed. This option works only when the Scheduler  |                |
|                             |                            |module is installed and running                                     |                |
+-----------------------------+----------------------------+--------------------------------------------------------------------+----------------+
|mail.log.purgetime           |\-                          |Number of time units used by :code:`mail.log.purgeenable` param     |purgetime value |
+-----------------------------+----------------------------+--------------------------------------------------------------------+----------------+
|mail.log.purgetimemultiplier |\-                          |Unit of time used by :code:`mail.log.purgeenable` param             |Hours, Days,    |
|                             |                            |                                                                    |Weeks, Months,  |
|                             |                            |                                                                    |Years           |
+-----------------------------+----------------------------+--------------------------------------------------------------------+----------------+

Default Email module configuration:

.. code-block:: json

    mail.host=localhost
    mail.port=8099
    mail.log.address=true
    mail.log.subject=true
    mail.log.body=true
    mail.log.purgeenable=false
    mail.log.purgetime=0
    mail.log.purgetimemultiplier=days

.. attention::

    Using the module requires a configured and working mail server, for example :code:`Postfix`.


OSGI Service API
----------------

The Email module exposes two OSGi services. First service provides possibility to send mail using the following API:

.. code-block:: java

    public interface EmailSenderService {

        /**
         * Attempts to send the supplied email message. Adds an {@link org.motechproject.email.domain.EmailRecord}
         * entry to the log with the details of the activity.
         *
         * @param message  the message to send
         */
        void send(Mail message);
    }

Email message is represented by an object of :code:`org.motechproject.email.contract.Mail` class which contains following fields:

- :code:`String fromAddress` - sender address.
- :code:`String toAddress` - recipient address.
- :code:`String subject` - mail subject.
- :code:`String message` - messages which is treated as html.

Second service provides API for browsing logged email activity and deleting unwanted logs.

.. code-block:: java

    public interface EmailAuditService {

        // finds an email in the log by ID
        EmailRecord findById(long id);

        // finds all emails available in the log
        List<EmailRecord> findAllEmailRecords();

        // finds emails matching the specified search criteria
        List<EmailRecord> findEmailRecords(EmailRecordSearchCriteria criteria);

        // returns the count of emails matching the specified search criteria
        long countEmailRecords(EmailRecordSearchCriteria criteria);

        // deletes the specified email from the log
        void delete(EmailRecord emailRecord);
    }


Email module UI
---------------

Email module user interface delivers three tabs:

- Send Email - gives you the ability to send e-mail messages.
- Email Logs - allows you to view and filter email logs, you can also export them to a `csv` file.
- Settings - allows you to change the current configuration of the module.

    .. image:: img/send_email.png
        :scale: 100 %
        :alt: Send email screen
        :align: center

User roles
-----------

Access to the module via GUI is granted to users with one of the following roles:

- Email Junior Admin - user can send mails, change module settings and view basic email logs(user can only see message status and delivery time).
- Email Admin - user can send mails, change module settings and view detailed email logs.

Consumed Event
---------------

To send e-mail from other module there is no need to have dependency on the Email module, because it handle events(:code:`org.motechproject.event.MotechEvent`).
Messages are created on the basis of handles events. Valid event must have subject :code:`SendEMail` (constant :code:`SEND_EMAIL_SUBJECT`) and following parameters(The constants
are in class :code:`org.motechproject.email.constants.SendEmailConstants`):

+------------+--------------------+--------------------+---------------+
|Parameter   |Description         |Value               |Constant names |
+============+====================+====================+===============+
|fromAddress |Sender address      |valid email address |FROM_ADDRESS   |
+------------+--------------------+--------------------+---------------+
|toAddress   |Recipient address   |valid email address |TO_ADDRESS     |
+------------+--------------------+--------------------+---------------+
|subject     |Mail subject        |any                 |SUBJECT        |
+------------+--------------------+--------------------+---------------+
|message     |Messages            |any                 |MESSAGE        |
+------------+--------------------+--------------------+---------------+

The Email module registers an event handler to allow other modules to request sending emails. This event is exposed to the Task module as a task action,
so you can send mails using the Task module.