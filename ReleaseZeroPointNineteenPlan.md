# Overview #
For the April release, we will continue our focus on moving towards MOTECH Cloud, as we support our first MOTECH Cloud customers - World Vision and LAC (Grameen Foundation agriculture project). The Tasks module continues to be a centerpiece of our cloud offering, as we attempt to make MOTECH easy to configure for non-developers. Similarly, we continue to focus on developing UI panels for high-priority MOTECH modules in order to make configuration easy.

We have decided to hold off on making MOTECH truly multi-tenant for now we will host several sandboxed MOTECHs on each cloud server until we have a robust multi-tenant architecture in place later in 2013. We have a few cards earmarked for this release which will allow us to install and operate several instances of MOTECH on a server concurrently. We will migrate existing customers to the fully multi-tenant system with some managed downtime later in the year if required.

# Carry-over from release 0.18 #
| **Feature** | **Description** | **Key Customer(s)** | **Spec** |
|:------------|:----------------|:--------------------|:---------|
| Multi-tenant infrastructure: ActiveMQ | MOTECH should be able to leverage a single ActiveMQ instance as the broker for multiple MOTECH tenants. | World Vision-Sierra Leone<br>MOTECH Cloud customers <table><thead><th> <a href='https://trello.com/c/QamFId4M'>Link</a> </th></thead><tbody>
<tr><td> Multi-tenant infrastructure: Scheduler </td><td> MOTECH should be able to leverage a single Quartz instance as the scheduler for multiple MOTECH tenants. </td><td> World Vision-Sierra Leone<br>MOTECH Cloud customers </td><td> Link TBD </td></tr>
<tr><td> Admin UI Panel: Message Campaign Part 1 </td><td> Support enrollment in message campaigns via UI </td><td> LAC (Colombia)      </td><td> <a href='https://docs.google.com/a/grameenfoundation.org/file/d/0Bx0Y9F3GRf6pSk1YYVNUaW5XTzA/edit'>Link</a> </td></tr></tbody></table>

<h1>New cards for 0.19</h1>
<table><thead><th> <b>Feature</b> </th><th> <b>Description</b> </th><th> <b>Key Customer(s)</b> </th><th> <b>Spec</b> </th></thead><tbody>
<tr><td> Tasks – calling APIs </td><td> Enhance the Tasks module to allow task actions to call APIs </td><td> Ethiopia <br>LAC (Colombia) <br>MOTECH Cloud customers </td><td> <a href='https://docs.google.com/a/grameenfoundation.org/file/d/0B6__Hl_QLjI4aXotc0dBRDJXbU0/edit'>Link</a> </td></tr>
<tr><td> Admin Panel UI: MRS </td><td> UI support for CRUD operations on entities in the MOTECH MRS module </td><td>                        </td><td> <a href=''>Link</a> </td></tr>
<tr><td> Admin Panel UI: Message Campaign Part 2 </td><td> Support authoring of message campaigns via UI </td><td> LAC (Colombia) <br>MOTECH Cloud customers </td><td>             </td></tr>
<tr><td> Admin Panel UI: SMS Enhancements </td><td> SMS log search and filtering UI, similar to the IVR call log feature </td><td>                        </td><td> <a href=''>Link</a> </td></tr>
<tr><td> Admin Panel UI: IVR Call Logging Part 2 </td><td> Completion of the call log filtering UI </td><td>                        </td><td> <a href=''>Link</a> </td></tr>
<tr><td> Install enhancement: allow installing multiple MOTECHs on one server </td><td> Allow for multiple MOTECH instances to be installed on the same server, as a stopgap until we have a fully multi-tenant system </td><td> MOTECH Cloud operations (all cloud customers) </td><td> Spec TBD    </td></tr>
<tr><td> Install and configure monitoring system for Cloud </td><td> Choose and install an NMS to monitor health of services for MOTECH Cloud </td><td>                        </td><td> <a href=''>Link</a> </td></tr></tbody></table>


<h1>Bugs</h1>
TBD