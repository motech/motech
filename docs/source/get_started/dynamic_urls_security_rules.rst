.. _security_rules:

=============================
Security Rules - Dynamic URLs
=============================

.. contents:: Table of Contents
    :depth: 2

Security Rules
==============

Security rules are used to build the Spring SecurityFilterChain which is used to filter incoming requests. By default, MOTECH blocks access to any resources
if you are not logged in, therefore, accessing any URL will redirect to the login page. If you need an endpoint using a different configuration, you must add a
new rule or edit an existing one.

Each rule contains the following parameters:

+---------------------+-----------------+--------------------------------------------------------------------------------+----------------------+
|Display name         |Parameter name   |Description                                                                     |Values                |
+=====================+=================+================================================================================+======================+
|Active               |active           |You can enable the rule using this parameter                                    |true, false           |
+---------------------+-----------------+--------------------------------------------------------------------------------+----------------------+
|URL Pattern          |pattern          |URL pattern the security rule applies to                                        |all                   |
|                     |                 |(? matches one character, * matches zero or more characters,                    |                      |
|                     |                 |** matches zero or more 'directories' in a path)                                |                      |
+---------------------+-----------------+--------------------------------------------------------------------------------+----------------------+
|Protocol             |protocol         |Protocol which will be used for communication                                   |HTTP or HTTPS         |
+---------------------+-----------------+--------------------------------------------------------------------------------+----------------------+
|HTTP Method          |methodsRequired  |HTTP methods that have access to the endpoint                                   |ANY, GET, POST, HEAD, |
|                     |                 |                                                                                |OPTIONS, PUT, DELETE, |
|                     |                 |                                                                                |TRACE                 |
+---------------------+-----------------+--------------------------------------------------------------------------------+----------------------+
|Rest                 |rest             |Whether the endpoint is meant for a form login process or as an REST endpoint   |true, false           |
|                     |                 |that does not create a session for the client                                   |                      |
+---------------------+-----------------+--------------------------------------------------------------------------------+----------------------+
|Priority             |priority         |Rule which has a higher priority will be checked first                          |priority value        |
+---------------------+-----------------+--------------------------------------------------------------------------------+----------------------+
|Supported Schema     |supportedSchemes |Specify which authentication is required                                        |NO_SECURITY or        |
|                     |                 |                                                                                |USERNAME_PASSWORD,    |
|                     |                 |                                                                                |BASIC, OPEN_ID        |
+---------------------+-----------------+--------------------------------------------------------------------------------+----------------------+
|User access          |userAccess       |Specify which users has access                                                  |list of users names   |
+---------------------+-----------------+--------------------------------------------------------------------------------+----------------------+
|Permission Access    |permissionAccess |Requires user has at least one of the listed permissions to access the URL      |list of permissions   |
|                     |                 |                                                                                |names                 |
+---------------------+-----------------+--------------------------------------------------------------------------------+----------------------+
|(Not present in GUI) |origin           |The module or user the rule originated from. Rules with SYSTEM_PLATFORM origin  |all                   |
|                     |                 |will be cleared at server start, so that they are always reloaded by the server |                      |
+---------------------+-----------------+--------------------------------------------------------------------------------+----------------------+
|(Not present in GUI) |version          |The version of the module or platform in which the rule was created             |all                   |
+---------------------+-----------------+--------------------------------------------------------------------------------+----------------------+

.. attention::

    After creating a new dynamic URL rule, it is currently necessary to restart the server for the rule to take effect. This will be addressed by `MOTECH-1486 <https://applab.atlassian.net/browse/MOTECH-1486>`_.

Priority
========

You can specify the order of processing using the priority parameter. Rules with greater priority will be checked first. In case of conflicting rules, the ones with higher priority
will block the ones with lower priority. In this case it is worth considering to use more accurate URL patterns. It is very helpful for a hierarchy model of urls.

User access and permission access
=================================

When you are using permission access with user access in one rule you must know that these options operate separately. For example you gave User access to :code:`sampleUser`
and Permission access to :code:`viewSecurity` permission. Access to the endpoint will be granted to :code:`sampleUser` and each other user with :code:`viewSecurity` permission.

Supported Schema, Rest and @PreAuthorize
========================================

If resources are protected using @PreAuthorize annotation you must remember that :code:`NO_SECURITY` schema will not work because access to these resources will be granted only to users
with respective roles. If other schemas are used, the user will still have to have the appropriate roles. The value of the rest option is important, you must know that if it's true
then only :code:`NO_SECURITY` and :code:`BASIC` schemas will be supported.

Configuration via GUI
=====================

.. attention::

    Before saving configuration remember to check the correctness of the settings, because you can lock yourself access to change them or you could provide access to the whole system. If you have lost access to the system, read the :std:ref:`information on regaining access, due to incorrect security rules configuration <regaining-access-label>`.

If you want edit those settings via GUI, your user account must have viewSecurity and updateSecurity permissions. To open the configuration you want to select
'Manage dynamic URLs' option under Security tab in the Admin panel. You should see a list of all security rules. When you start editing or adding a new security rule form will
expand and you will see options that were described earlier. To activate current configuration you must save changes.

    .. image:: img/security_rule.png
        :scale: 100 %
        :alt: Configuration screen for security rules
        :align: center


Configuration via files
=======================

You can add rules to your module using configuration files. To do this you must create a file named :code:`securityRules.json` and place it in the resources
directory and then build the module. Security rule configuration files are discovered automatically by MOTECH when the module starts.

Sample file:

.. code-block:: json

    [
        {
            "active": true,
            "pattern": "/**/myModuleApi/someResources/**",
            "supportedSchemes": [
                "NO_SECURITY"
            ],
            "protocol": "HTTP",
            "priority": 2,
            "rest": true,
            "origin": "SYSTEM_MODULE_MY_MODULE",
            "version": "0.25",
            "methodsRequired": [
                "GET",
                "POST"
            ]
        },
        {
            "active": true
            "pattern": "/**/myModuleApi/otherResources/**",
            "supportedSchemes": [
                "BASIC"
            ],
            "protocol": "HTTP",
            "userAccess": [
                "userName"
            ],
            "priority": 3,
            "rest": true,
            "origin": "SYSTEM_MODULE_MY_MODULE",
            "version": "0.25",
            "methodsRequired": [
                "ANY"
           ],
       }
    ]


.. _regaining-access-label:

Regaining access
================

To regain access to MOTECH, restart it. When server starts, default platform rules are always reloaded so it may help you regain access. If that doesn't work you should try drop database
table holding security rules or delete only rules that block access.
