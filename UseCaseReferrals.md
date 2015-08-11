# Overview #

MOTECH Suite includes the capability of tracking the initiation and completion of patient referrals as well as messaging the parties involved in the referral (recipient, initiating caregiver, and referred caregiver) to increase successful completion of the referral.

# Use Cases #

Without MOTECH, referral processes typically follow a workflow similar to this:
![http://www.motechsuite.org/assets/wiki/referral-flow.png](http://www.motechsuite.org/assets/wiki/referral-flow.png)

In a MOTECH application, there are five potential non-exclusive referral use cases which we’ll discuss in the following sections:
  1. The referring provider records the encounter in the EMR using MOTECH Suite
  1. The referring provider is reminded of the referral so that she can follow up with the patient
  1. The patient is reminded of the referral to encourage her to follow through with the referral
  1. The referred provider is notified when a referral is directed to her
  1. The referred provider records the encounter in the EMR using MOTECH Suite

![http://www.motechsuite.org/assets/wiki/referral-flow2.png](http://www.motechsuite.org/assets/wiki/referral-flow2.png)

## 1.	The referring provider records the encounter in the EMR using MOTECH Suite ##
**Goal:** The goal of this use case is to improve the accuracy of entered data and enable better reporting. This improves the data collected in the step labeled #1 in the diagram above.

**Example:** A CHW to use a CommCare handset to record that a referral was made.

**Pre-requisites / Dependencies:** CommCare handset for each referring provider
## 2.	The referring provider is reminded of the referral so that she can follow up with the patient ##
**Goal:** The goals of this use case are to increase the likelihood that the patient will follow through with the referral and help the CHW manage her task list. This improves the efficiency of the step labeled #2 in the diagram above.

**Example:** Three days after a CHW refers a patient to a regional clinic, the CHW receives a message (via the task list on her CommCare handset, as a text, and/or as an IVR message) reminding her to follow-up with the patient.

**Pre-requisites / Dependencies:** Referral Use Case #1 (above) must also be implemented.

## 3.	The patient is reminded of the referral to encourage her to follow through with the referral ##
**Goal:** The goal of this use case is to increase the likelihood that the patient will follow through with the referral. This improves the effectiveness of the step labeled #3 in the diagram above.

**Example:** Once the CHW refers the patient to the regional clinic, the patient begins receiving text or IVR messages reminding her to follow through on the referral. These reminders can be on virtually any schedule. Reminders can stop after a certain time period, based on a response from the patient (e.g., press 2 if you have been to the clinic for the referral), or manually when the CHW revisits the patient and confirms the referral was followed.

**Pre-requisites / Dependencies:** Referral Use Case #1 (above) must also be implemented. Patient phone number must be collected as part of the patient record.

## 4.	The referred provider is notified when a referral is directed to her ##
**Goal:** The goal of this use case is to increase the likelihood that the referred provider will be able to see the referred patient.

**Example:** Once the CHW refers the patient to the regional clinic, the referred provider is receives a text or IVR message notifying that the patient was referred to that clinic.

**Pre-requisites / Dependencies:** Referral Use Case #1 (above) must also be implemented.

## 5.	The referred provider records the encounter in the EMR using MOTECH Suite ##
**Goal:** The goal of this use case is to more effectively close the referral loop

**Example:** When the provider at the referred clinic sees a referred patient, she can record that the referral was followed via a CommCare handset or web-based application.

**Pre-requisites / Dependencies:** Referral Use Case #1 (above) must also be implemented. Providers at referred clinics must have CommCare handsets or PC-based internet connectivity. A system for uniquely identifying all patients must be implemented.