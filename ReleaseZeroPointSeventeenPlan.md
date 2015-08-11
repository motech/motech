# Overview #
For the January release, we will continue our focus on moving towards MOTECH Cloud, as we prepare for our first Cloud customer (World Vision-Sierra Leone) in February. Most of the features that we are prioritizing for this release correspond to specific requirements for the Sierra Leone implementation. Note that we don't expect to have a robust multi-tenant MOTECH system in place for this customer - we will continue to put multi-tenant infrastructure in place over the course of Q1 2013, with some carefully managed downtime for live customers if required.

# Carry-over from release 0.16 #
| **Feature** | **Description** | **Key Customer(s)** | **Spec** |
|:------------|:----------------|:--------------------|:---------|
| IVR enhancements | Enhancements to the IVR API, events, interface and reports within MOTECH. | MOTECH Cloud customers | <a href='https://docs.google.com/a/grameenfoundation.org/document/d/1fW2rcDGsrTstvVF16o8W_vmvZJ_PEILqimhiUMSbMfo/edit'>Link</a> |
| SPIKE: REST frameworks | Evaluate REST frameworks and decide which is appropriate for MOTECH. | All                 | <a href='https://docs.google.com/a/grameenfoundation.org/document/d/10fNflt8Ouhj7aTT5V077Kz6T88gfnVURhgaJnV-9ZeQ/edit'>Link</a> |
| Publish one-step install packages | Add a step to the release that publishes the one-step install packages to a server. |                     | <a href='https://trello.com/c/SgGDCl7w'>Link</a> |

# New cards for 0.17 #
| **Feature** | **Description** | **Key Customer(s)** | **Spec** |
|:------------|:----------------|:--------------------|:---------|
| Tasks – Iteration 3 | Next rev of the “ifttt”-style workflow engine. This release will add the task dashboard UI. | Ethiopia <br>LAC (Colombia) <br>MOTECH Cloud customers <table><thead><th> <a href='https://docs.google.com/a/grameenfoundation.org/file/d/0B6__Hl_QLjI4aXotc0dBRDJXbU0/edit'>Link</a> </th></thead><tbody>
<tr><td> Couch MRS API Iteration 2 </td><td> Add support for Patient and Provider entities to the Couch MRS repository </td><td> World Vision-Sierra Leone </td><td> <a href='https://docs.google.com/a/grameenfoundation.org/document/d/1TwobiExwnFTsfTetvufuDBkLVCIaEYUhI4YZXGd9D8Y/edit#'>Link</a> </td></tr>
<tr><td> Multi-tenant MOTECH Spike </td><td> Explore what it would take to have a single (or clustered) MOTECH handle multiple tenants. </td><td> MOTECH cloud customers </td><td> <a href='https://trello.com/c/3YoZ853f'>Link</a> </td></tr>
<tr><td> Multi-tenant infrastructure: CouchDB </td><td> MOTECH should be able to leverage a single CouchDB instance as the database for multiple MOTECH tenants. </td><td> World Vision-Sierra Leone<br>MOTECH Cloud customers </td><td> <a href='https://trello.com/c/h9TdzopO'>Link</a> </td></tr>
<tr><td> (STRETCH GOAL) Multi-tenant infrastructure: ActiveMQ </td><td> MOTECH should be able to leverage a single ActiveMQ instance as the broker for multiple MOTECH tenants. </td><td> World Vision-Sierra Leone<br>MOTECH Cloud customers </td><td> <a href='https://trello.com/c/QamFId4M'>Link</a> </td></tr>
<tr><td> (STRETCH GOAL) Multi-tenant infrastructure: Scheduler </td><td> MOTECH should be able to leverage a single Quartz instance as the scheduler for multiple MOTECH tenants. </td><td> World Vision-Sierra Leone<br>MOTECH Cloud customers </td><td> Link TBD </td></tr>
<tr><td> (STRETCH GOAL) MOTECH Cloud Monitoring Iteration 0 </td><td> First rev of monitoring system for MOTECH Cloud - in spec phase </td><td> World Vision-Sierra Leone<br>MOTECH Cloud customers </td><td> <a href='https://docs.google.com/a/grameenfoundation.org/document/d/18nq4NE2mrs594HiQsF-2rFQ0kFXswa8QPjLwwF3bO6w/edit#'>Link</a> </td></tr></tbody></table>

<h1>Bugs</h1>
Any pressing bugs for this release?