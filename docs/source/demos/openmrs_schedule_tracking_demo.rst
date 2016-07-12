Demo: OpenMRS Schedule Tracking
===============================

.. contents:: Table of Contents
   :depth: 2

Getting started
###############

The source code for the demo is available on our `GitHub repository <https://github.com/motech/motech-campaign-demo/tree/v1.0>`_ (branch v1.0).

You will need to set up the following external systems for the demo implementation:
 - CommCareHQ
 - OpenMRS 1.9 (with Rest Web Services module, version 2.4)

You may check our documentation, for complete guide on :doc:`Connecting MOTECH to OpenMRS </get_started/connect_openmrs>`.

Before starting to work with the demo implementation, you must prepare your CommCareHQ and OpenMRS instance. First, access
the CommCareHQ and upload the forms schema, present in the commcare.schema folder. Then access MOTECH Commcare module
and connect it with your CommCareHQ instance, by providing necessary data. Finally, enable data forwarding for forms,
either via MOTECH Commcare module or via CommCareHQ instance. For OpenMRS, you are supposed to prepare some sample data.
Access your OpenMRS instance and under Administration tab, click "View Concept Dictionary" and add four new concepts named:
Demo Concept Question #1, Demo Concept Question #2, Demo Concept Question #3, Demo Concept Question #4. Make sure to set the
datatype in all of them to "Date". Get back to Administration and click on locations. Add at least one location of any name.
Get back to Administration and click "Manage providers". Click "Add Provider" and add provider for person linked with the
initial admin user (by default the person name is "Super User"). Get back to Administration and click on "Manage Encounter
Types". Click "Add Encounter Type" and add type of name "ADULTRETURN", with any description.

You can build the demo project invoking the following command from the demo project directory:

    .. code-block:: bash

        $ mvn clean install

The demo module exposes its options via a very simple UI, available under:
<motech-platform-url>/scheduletracking-demo/enroll/scheduleTracking


Demo specification
##################

In this demo, a milestone corresponds to an observation of a particular concept having been made on or after the initial
date of enrollment into the Demo Schedule. The four milestones are the Demo concepts, defined in the previous step.

Milestones have "windows" which represent a period of time. For example, today through five days from now represents a
five day window. In the demo, each milestone has the same window periods. 0-1 minutes is the "early" window and no
messages or alerts are raised.

At 1 minute in, a due message is raised and a phone call as well as text message are placed to the patient indicating
that they are due for that particular Concept Question.

At 3 minutes, they enter the late window, and a late message is dispatched in the same manner. They receive a second
late message one minute later at the 4 minute mark. The late window lasts until the 6th minute.

After this, no more late messages are sent and they enter the "max" window. They may still complete the milestone
during this period. If they go beyond 15 minutes from the start of a milestone without fulfilling it, they are defaulted
and their enrollment in the schedule is no longer active.

If a patient fulfills a milestone before they have defaulted, they will move on to the next milestone (unless there
are no more, then the enrollment is completed) with new messages scheduled following the same format outlined above.

A patient may only have one active enrollment in the schedule at any given time, but may unenroll and start a new
enrollment, or enroll again after their enrollment is completed or defaulted.

The three Commcare forms, that you have uploaded in the previous step are:
 - **Patient Registration** - This form will create an OpenMRS patient, create a MOTECH patient, and optionally enroll the
   patient into the Demo Schedule.
 - **Patient Enrollment** - This form will enroll the MOTECH patient into the Demo Schedule (A corresponding OpenMRS
   patient with the same MOTECH Id must exist to use this form.)
 - **Patient Encounter** - This form will create an encounter for an OpenMRS patient. You must provide the
   MOTECH Id of the patient in OpenMRS, an observation date, and an observed concept.


Demo workflow
#############

In order to use the demo, you must register a patient into MOTECH with a phone number. The same patient ID must also be
registered into OpenMRS. This can be achieved by registering the patient directly in OpenMRS or by sending a
Commcare "Registration form".

You may then enroll that patient in the schedule. You may view the definition of a schedule in the simple-schedule.json
file, located in the resources directory of the scheduletracking demo module. This schedule will be automatically created
during demo startup. A patient can be enrolled to the schedule from the scheduleTracking page or by sending the Commcare
Enrollment form. If an enrolled patient is not found in both the demo MOTECH phone number database and in OpenMRS,
they will not be enrolled in the schedule. Once a patient is enrolled, SMS messages and phone calls will be placed,
indicating that the patient is due for a particular concept.

To complete a concept, a Patient Encounter form should be submitted via CommCareHQ. You must provide the MOTECH Id of the
patient in OpenMRS, an observation date, and an observed concept id. After you have completed all 4 concepts, you will be
removed from the Demo Schedule. You are also free to complete concepts (e.g. Demo Concept Question #1), before you enroll.
In this case, when you do enroll, you will be enrolled at the next required concept milestone. For example, if you
complete Demo Concept Question #1 and #2, then enroll in the Demo Schedule, you will be scheduled for the third milestone
(Demo Concept Question #3).

Possible failures
-----------------
Each form received by the scheduletracking demo is validated before processing. A few of the reasons a form may fail include:
 - Bad phone number format (must be in form XXX-XXX-XXXX)
 - Out of sequence Concept, e.g. you can't complete "Demo Concept Question #3" before completing "Demo Concept Question #2"

If a validation of a form fails, an information will be printed in the logs and no further action (eg. enrollment, encounter)
will be executed.
