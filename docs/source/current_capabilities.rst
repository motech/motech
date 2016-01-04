=============================
MOTECH's Current Capabilities
=============================
The latest version of MOTECH is 0.27.x. We recognize that open source projects rapidly develop with many contributors adding a piece at a time. This document focuses on the capabilities of MOTECH right now and will remain updated regardless of the current release. We're keeping a list of future features on `our roadmap <roadmap.html>`_ with a focus on MOTECH 1.0.

Below is a list of features currently available in MOTECH:

- Commcare two-way integration
    - Configure multiple connections to Commcare projects
    - Receive and act on forwarded forms and cases from CommCare
    - Import historical forms
    - Connect to Commcare's data, fixture and location APIs to supplement tasks
- DHIS2 one-way push integration
    - Configure a single connection to a DHIS2 server
    - Create tracked entity instances
    - Enroll tracked entities in DHIS2 programs
    - Push program stage events
    - Send aggregate data values and value sets
- SMS Integration
    - Send and receive SMS from MOTECH through integration with an SMS aggregator with predefined configurations for popular systems like Clickatell, Plivo, Twilio and Voto
- IVR Integration
    - Act on inbound and initiate outgoing calls from MOTECH by integrating with a mobile network operator
- Perform Message Campaigns
    - MOTECH is able to perform SMS, voice and email message campaigns directly to communities and front line workers
- Pill Reminder System
    - Enroll users in a custom pill regimen and send per-user reminders for their specific medication
- Consume Facility, Provider and Organization information based on the IHE Care Services Discovery profile
    - Connect to a CSD provider such as OpenInfoMan via REST or SOAP and store the data in the MOTECH Data Services system.
- Wire up events in MOTECH through the UI with the tasks module
    - The tasks module is the primary method to act on events in MOTECH. Each interaction raises an event in MOTECH which can trigger a task and perform a corresponding action.
- Role based access controls, audit logs, password requirements and many other security and auditing capabilities

Tab-by-tab features
-------------------

Admin Tab
^^^^^^^^^
- Managing Modules (Manage Modules)
    Admins can install, remove, start, restart and configure modules through the user interface.
- Automated Email and SMS Notification (Messages)
    Admins can setup MOTECH so system administrators receive emails or SMS when errors occur in MOTECH.
- View and download the server log (Server Log)
    Admins are able to view the tomcat server log within the UI as well as manage log options to log more endpoints.

Security Tab
^^^^^^^^^^^^
- Role Based Access Controls
    Users are able to manage role based access control from the user interface.

Modules Tab
^^^^^^^^^^^
The modules tab is the location where users interact with specific modules. These modules are loaded under the admin tab > manage modules. The following modules are present in the core platform.

Data Services
"""""""""""""
- User Defined Entities (Schema Editor > + New Entity)
    Users are able to create entities in the data base through the user interface with automatic Tasks Integration. This is commonly used to store elements from incoming sources, compile them and push them to other sources.
- User Defined Lookups (Schema Editor > Advanced > Indexes & Lookups)
    Users are able to create lookups on any entity in MOTECH that return one or many results.
- User Defined REST endpoints (Schema Editor > Advanced > REST API)
    Users are able to define REST endpoints through the user interface.
- Entity Auditing and Access Control (Schema Editor > Advanced > Auditing & revision Tracking)
    Users are able to turn on auditing and access control for any entity in data services.

Email
"""""
- Configure an email server
- Send emails through the UI
- View email logs

Scheduler
"""""""""
- View scheduler jobs

Tasks
"""""
- Create and manage tasks
- View task activity and troubleshoot failed tasks

REST API Tab
^^^^^^^^^^^^
- View exposed REST API documentation created in the Data Services entities (Utilizes Swagger.io to automatically generate this documentation.)
