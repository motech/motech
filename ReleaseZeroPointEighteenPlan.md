# Overview #
For the February release, we will continue our focus on moving towards MOTECH Cloud, as we prepare for our first Cloud customer (World Vision-Sierra Leone) in February. Some of the features that we are prioritizing for this release also correspond to specific requirements for the Colombia agriculture cloud implementation. Note that we don't expect to have a robust multi-tenant MOTECH system in place for our first customer - we will continue to put multi-tenant infrastructure in place over the course of Q1 2013, with some carefully managed downtime for live customers if required.

# Carry-over from release 0.17 #
| **Feature** | **Description** | **Key Customer(s)** | **Spec** |
|:------------|:----------------|:--------------------|:---------|
| IVR enhancements part 2 | Enhancements to the IVR API, events, interface and reports within MOTECH. (First version of call log search with all filtering will be ready for 0.17. Features such as showing events and auto completion of phone numbers etc. will be completed for 0.18.) | MOTECH Cloud customers | <a href='https://docs.google.com/a/grameenfoundation.org/document/d/1fW2rcDGsrTstvVF16o8W_vmvZJ_PEILqimhiUMSbMfo/edit'>Link</a> |

# New cards for 0.18 #
| **Feature** | **Description** | **Key Customer(s)** | **Spec** |
|:------------|:----------------|:--------------------|:---------|
| Tasks – Iteration 6 & 7 | Next rev of the “ifttt”-style workflow engine. This release will add data binding and string manipulation. | Ethiopia <br>LAC (Colombia) <br>MOTECH Cloud customers <table><thead><th> <a href='https://docs.google.com/a/grameenfoundation.org/file/d/0B6__Hl_QLjI4aXotc0dBRDJXbU0/edit'>Link</a> </th></thead><tbody>
<tr><td> Multi-tenant infrastructure: CouchDB </td><td> MOTECH should be able to leverage a single CouchDB instance as the database for multiple MOTECH tenants. </td><td> World Vision-Sierra Leone<br>MOTECH Cloud customers </td><td> <a href='https://trello.com/c/h9TdzopO'>Link</a> </td></tr>
<tr><td> Multi-tenant infrastructure: ActiveMQ </td><td> MOTECH should be able to leverage a single ActiveMQ instance as the broker for multiple MOTECH tenants. </td><td> World Vision-Sierra Leone<br>MOTECH Cloud customers </td><td> <a href='https://trello.com/c/QamFId4M'>Link</a> </td></tr>
<tr><td> Multi-tenant infrastructure: Scheduler </td><td> MOTECH should be able to leverage a single Quartz instance as the scheduler for multiple MOTECH tenants. </td><td> World Vision-Sierra Leone<br>MOTECH Cloud customers </td><td> Link TBD </td></tr>
<tr><td> REST API for Message Campaign </td><td> REST API for Message Campaign enrollment / unenrollment </td><td> LAC (Colombia)      </td><td> Link TBD </td></tr>
<tr><td> REST API for SMS </td><td> REST API for SMS send </td><td> LAC (Colombia)      </td><td> Link TBD </td></tr></tbody></table>


<h1>Bugs</h1>
<ul><li>Event aggregation module not behaving according to spec<br>
</li><li>Update bundle archetype so released version depends on latest released version of platform (and Add step to release wiki about updating archetype version)<br>
</li><li>Admin panel left hand nav doesn't auto refresh<br>
</li><li>Admin UI should report an error when an uploaded module has an error