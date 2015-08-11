# Overview #
Release 0.20 will be the second release since we switched to a bi-monthly release schedule.

# Carry-over from release 0.18 #
| **Feature** | **Description** | **Key Customer(s)** | **Spec** |
|:------------|:----------------|:--------------------|:---------|
| Multi-tenant infrastructure: Scheduler | MOTECH should be able to leverage a single Quartz instance as the scheduler for multiple MOTECH tenants. | World Vision-Sierra Leone<br>MOTECH Cloud customers <table><thead><th> Link TBD </th></thead><tbody></tbody></table>

<h1>New cards for 0.19</h1>
<table><thead><th> <b>Feature</b> </th><th> <b>Description</b> </th><th> <b>Key Customer(s)</b> </th><th> <b>Spec</b> </th></thead><tbody>
<tr><td> CommCare form push support </td><td> USM has a module that allows declarative mapping of patients, observations and encounters.  Needs to be cleaned up and added as a module. </td><td> Ethiopia <br>CARE (India) </td><td> <a href='https://trello.com/c/UVkqZoGI'>Link</a> </td></tr>
<tr><td> Admin Panel UI: MRS Part 2 </td><td> UI support for CRUD operations on entities in the MOTECH MRS module (Encounters, Observations, Providers, Facilities) </td><td>                        </td><td> <a href=''>Link</a> </td></tr>
<tr><td> Enhanced CommCare Module & Integration with Tasks </td><td> UI-based forms schema import & integration with tasks </td><td>                        </td><td> <a href='https://docs.google.com/a/grameenfoundation.org/document/d/1di0DaNBaJZo0EkN2jWMoFpcNJGUY7iTG6YLfQ1fjT1E/edit?usp=sharing'>Link</a> </td></tr>
<tr><td> Queue Statistics Viewer </td><td> ActiveMQ comes with a web console that allows users to view and interact with the message queue.  Within the MOTECH Admin UI it would be nice if a subset of that web console was available.  At a minimum read only views over the queues, counts of enqueue, dequeue and current messages and maybe the ability to view items in each queue. This view should only show the queue for the current tenant. </td><td>                        </td><td> <a href='https://trello.com/c/WdZoEz2d'>Link</a> </td></tr>
<tr><td> SPIKE: Get MOTECH running on Heroku </td><td> As a hosting alternative for our customers, we'd like to support MOTECH running on IaaS systems like Heroku. This spike will help us determine feasibility and document the process. </td><td>                        </td><td> <a href='https://trello.com/c/omjYWpZH'>Link</a> </td></tr>
<tr><td> Tasks v2       </td><td> The Tasks module will be updated to support multiple actions per task, multiple filter and data loaders in a user defined order, and the ability to filter based on fields loaded via a data loader. </td><td>                        </td><td> <a href='https://docs.google.com/a/grameenfoundation.org/document/d/1dj1HU9BOIa61OvAXeU_W1cr2PMRPX6XAem-EZQQ7ckc/edit'>Link</a> </td></tr>