=============================
MOTECH's Current Capabilities
=============================
The latest version of MOTECH is 0.27.x. We recognize that open source projects rapidly develop with many contributors adding a piece at a time. This document focuses on the capabilities of MOTECH right now and will remain updated regardless of the current release. We're keeping a list of future features on `our roadmap <roadmap.html>`_ with a focus on MOTECH 1.0.

Below is a list of features currently available in MOTECH:

- Administration
    - Manage modules through the user interface. MOTECH is a modular system that allows you to add specific features and connections through modules. Administrators are able to install, configure and manage modules through the user interface.
    - Role based access controls, audit logs, password requirements and many other security and auditing capabilities
    - Create user defined entities (MySQL database tables, lookups and relationships) through the user interface
- Tasks
    - Wire up events in MOTECH through the UI with the tasks module
    - The tasks module is the primary method to act on events in MOTECH. Each interaction raises an event in MOTECH which can trigger a task and perform a corresponding action.
- Interfaces
    - Web Browser
        - End users can directly interact with the MOTECH interface through a web browser
    - Commcare two-way integration
        - Configure multiple connections to Commcare projects
        - Receive and act on forwarded forms and cases from CommCare
        - Import historical forms
        - Connect to Commcare's data, fixture and location APIs to supplement tasks
    - SMS Integration
        - Send and receive SMS from MOTECH through integration with an SMS aggregater with predefined configurations for popular systems including:
            - `Clickatell <https://www.clickatell.com/>`_
            - `KooKoo <http://kookoo.ozonetel.com/>`_
            - `Nuntium <http://instedd.org/technologies/nuntium/>`_
            - `Plivo <https://www.plivo.com/>`_
            - `Rancard <http://www.rancard.com/>`_
            - `Twilio <https://www.twilio.com/>`_
            - `Voxeo <https://voxeo.com/>`_
            - `Voto <https://www.votomobile.org/>`_
        - Create a JSON SMS template to easily connect to a specific Mobile Network Operator
    - IVR Integration
        - Initiate outgoing calls from MOTECH by integrating with a mobile network operator
- Modules
    - Perform Message Campaigns
        - MOTECH is able to perform SMS, voice and email message campaigns directly to communities and front line workers
    - Pill Reminder System
        - Enroll users in a custom pill regimen and send per-user reminders for their specific medication
    - Consume Facility, Provider and Organization information based on the IHE Care Services Discovery profile
        - Connect to a CSD provider such as OpenInfoMan via REST or SOAP and store the data in the MOTECH Data Services system.
- Reporting
    - DHIS2 one-way push integration
        - Configure a single connection to a DHIS2 server
        - Create tracked entity instances
        - Enroll tracked entities in DHIS2 programs
        - Push program stage events
        - Send aggregate data values and value sets
    - ETL Connections to Pentaho and Jasper Reports
        - MOTECH can connect to popular third party reporting systems through an Extraction, Transformation and Loading (ETL) system such as `Pentaho's Data Integration - Kettle <http://community.pentaho.com/projects/data-integration/>`_ and `Jaspersoft-ETL <http://community.jaspersoft.com/project/jaspersoft-etl>`_