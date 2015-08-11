# Overview #

MOTECH Suite includes the capability to send alerts to actors in the health care system. These alerts can be presented via the CommCare handset, IVR, SMS, email and other modes of delivery.


# Use Case Matrix #
Many different permutations of Alerts functionality are supported, so it’s useful to think about the characteristics alerts can have. These include:
## Health System Actors ##
Alerts can be sent to any of the actors in the healthcare system provided there is a known way to reach them (phone number, CommCare handset, email address, etc.).
Simultaneous alerts about the same event can go to multiple actors in the system. For instance, an overdue care alert can be sent to the patient and caregiver.
## Mode of Transmission ##
Alerts can be presented via IVR, SMS, email as well as through the CommCare handset UI.
## Triggers ##
Alerts can be triggered by different things:
### • Fixed calendar date ###
Example: an alert that goes out each year on January 1.
### • Schedule relative to a user-specific date ###
Example 1: Alert for ANC Visit 2 occurs 20 weeks after patient’s LMP.
### • Changes in the healthcare record ###
Example: Alert is sent to referred clinic when CHW records that a patient was referred to that clinic.
### • Evaluation of complicated criteria ###
Example: Alert sent to CHW if she has not seen half her monthly patient caseload by the 15th of each month.
## Alert Response ##
Some alerts are just outbound. Others have outbound and inbound components. Outbound alerts are those that are displayed or played to the recipient, but alerts can also collect information from the person who receives them. For instance, an IVR alert reminding a patient to go to an appointment could ask the patient to press 1 if she was not feeling well enough to go. In this case, the alert could escalate the problem (via another alert) to the CHW.
# MOTECH Suite Modules / Systems #
Depending on the type of Alerts implemented, the Alerts functionality depends on the MST Tasks Module and Schedule Tracking Module, as well as the modules for the relevant transmission mode (e.g., IVR module for voice messages).