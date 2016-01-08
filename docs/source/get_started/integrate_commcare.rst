================================
Integrating MOTECH with CommCare
================================

.. contents:: Table of Contents
   :depth: 3

############
Introduction
############
The Commcare module provides an ability to integrate MOTECH with CommCareHQ. At the moment, the Commcare module
allows the following:

- View forms and cases, using MOTECH UI
- Receive notifications when a new form or case is received or when the application schema changes
- Use the exposed OSGi services, to query CommCareHQ for forms, cases, users and fixtures and upload certain data to
  CommCareHQ, like cases and data forwarding rules
- Import forms, each import form will result in firing an event
- Querying the CommCareHQ stock ledger
- Use the data from CommCareHQ to model more advanced logic, using the Tasks module

Please note, that throughout this document, two similar expressions will be used:

- **Commcare** - refers to the MOTECH module, that allows the integration with CommCareHQ
- **CommCareHQ** - refers to an external service, located under www.commcarehq.org

More information about Commcare module you will find in the :std:ref:`Commcare module topic <commcare-module>`.

#########################
Configure Commcare module
#########################

Account settings
################
To allow MOTECH to connect to the CommCareHQ, you should first add a new configuration and provide correct credentials
to the CommCareHQ account:

- CommCare Base URL (a link to the CommCareHQ instance; by default www.commcarehq.org)
- CommCare Domain (project name on CommCareHQ)
- Username
- Password

To add configuration you must use **Add configuration** button . After filling in the data, press the **Save** button.

        .. image:: img/commcare_acc_settings.png
                :scale: 100 %
                :alt: Commcare account settings
                :align: center

To verify the provided data, click the **Verify** button. The Commcare module will send a test request to the CommCareHQ,
attempting to authenticate with the credentials you have provided. If everything works OK, you will be notified about
successful connection. If there were any problems connecting to the CommCareHQ, an error will be displayed and you will
not be able to work with the Commcare module, until valid credentials are provided.

You can provide more than one account configuration. Only one of the supplied configuration can be the default. The
default configuration will be selected whenever you do not specify a particular configuration. To mark the configuration
as default you must save the configuration(if it is new) and use **Make default** button.

Event forwarding
################
This section allows you to configure the events, fired by the Commcare module. Currently, you can pick the
**Event Strategy** for the forwarding of case events. There are three options available:

- minimal (the event will contain only the case ID)
- partial (the event will contain case ID, as well as other, not-case specific parameters (case metadata)
- full (the event will contain case ID, case metadata and all field values of a case)

Once you switch the option, the events fired by the Commcare module will contain only the fields you have chosen to forward.

.. note::

    When you switch to a more strict strategy (forwarding less details), make sure that no listeners rely on the
    content of these events (eg. Task, triggered by "Received case")

Connect CommCareHQ
##################
To let CommCareHQ know, where the data should be forwarded, you also need to set up data forwarding URLs. This can be
achieved in two ways. The first way is to do it via the checkboxes. If you want to set up CommCareHQ to
forward the data to the Commcare module, select the checkbox and the Commcare module will automatically
set up an URL on your CommCareHQ account.

        .. image:: img/commcare_connect_commcarehq.png
                :scale: 100 %
                :alt: Commcare connect CommCareHQ
                :align: center

.. note::

    To use those checkboxes you must set the server URL in the Settings tab in the Admin module.

If for any reason, the first way doesn't work or sets up invalid URL, the data forwarding rules can also be set in the
project settings on your CommCareHQ account. If you have enabled the rules via the slider buttons, you can also verify
that the correct rules (with proper URL to your server) have been set up on the CommCareHQ.

        .. image:: img/commcarehq_project_settings.png
                :scale: 100 %
                :alt: CommCareHQ project settings
                :align: center

To disable the data forwarding rules, you have to open the project settings on your CommCareHQ account and disable them
from there. The Commcare module can only set up the rules, but cannot disable them.

        .. image:: img/commcarehq_stop_forwarding.png
                :scale: 100 %
                :alt: CommCareHQ data forwarding
                :align: center

As you can see the provided URL :code:`http://demo.motechproject.org/module/commcare/forms/` doesn't have specified configuration.
So if the Commcare module receives data, the default configuration will be used. To work with more than one configuration
you will have to use for example such URL :code:`http://demo.motecproject.org/module/commcare/forms/myProjectConf`. The URL
with the configuration name should be automatically added by the Commcare module.

############
Fired events
############

+--------------------------------------------------------+--------------------------------------------------------------------------+
|Subject                                                 |Info                                                                      |
+========================================================+==========================================================================+
|org.motechproject.commcare.api.schemachange.            |Fired, when the project schema gets changed on the CommCareHQ             |
|                                                        |(module added, form edited, etc.).                                        |
+--------------------------------------------------------+--------------------------------------------------------------------------+
|org.motechproject.commcare.api.forms                    |Fired, when a new form has been received on CommCareHQ or when form has   |
|                                                        |been imported. One event per received/imported form.                      |
+--------------------------------------------------------+--------------------------------------------------------------------------+
|org.motechproject.commcare.api.case                     |Fired, when a new form has been received on CommCareHQ.                   |
|                                                        |One event will be fired per affected case.                                |
+--------------------------------------------------------+--------------------------------------------------------------------------+
|org.motechproject.commcare.api.formstub                 |Fired, when a new form has been received on CommCareHQ.                   |
|                                                        |Contains only IDs of affected form and cases.                             |
+--------------------------------------------------------+--------------------------------------------------------------------------+
|org.motechproject.commcare.api.receivedStockTransaction |Fired, when a stock transaction object was fetched by querying the        |
|                                                        |CommCareHQ stock ledger.                                                  |
+--------------------------------------------------------+--------------------------------------------------------------------------+

There are three more events, that are fired, when an internal exception occurs while parsing XML file,
received from CommCareHQ. They are:

- **org.motechproject.commcare.api.forms.failed** (when parsing of a form fails)
- **org.motechproject.commcare.api.formstub.failed** (when parsing of a form stub fails)
- **org.motechproject.commcare.api.exception**  (when parsing of a case fails)


#################################
Integration with the Tasks module
#################################
The Commcare module will automatically update the Tasks triggers and data sources, each time a schema change event
is received. For each form and for each case type, a separate trigger and data source object will be created. This means
that you can trigger tasks, when a certain form or case is received and use its fields in an action you select. The fields
of forms and cases are based on the schema received from CommCareHQ. The Commcare module also provides an action for
querying the CommCareHQ stock ledger for the stock transactions and a trigger for retrieving the stock transactions.
