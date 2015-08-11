# Carry-over from release 0.15 #
| **Feature** | **Description** | **Key Customer(s)** | **Spec** |
|:------------|:----------------|:--------------------|:---------|
| Tasks – Iteration 0 (code complete, needs review and merge) |  Basic plumbing for "ifttt" style workflow engine. No UI yet, but user can author tasks in the persistence format and they will execute. | Ethiopia <br>MOTECH Cloud customers <table><thead><th> <a href='https://docs.google.com/a/grameenfoundation.org/file/d/0B6__Hl_QLjI4aXotc0dBRDJXbU0/edit'>Link</a> </th></thead><tbody>
<tr><td> MOTECH Admin Auth (code complete, needs review and merge) </td><td>  How do we make the experience of logging in to the different components of MOTECH Suite painless? This also includes auth and permissions within MOTECH itself. </td><td> All                 </td><td> <a href='https://docs.google.com/document/d/1c8zWn2DE83WKCYuA9bfyHeoP-IrULg4nUpp5wldHuHo/edit'>Link</a> </td></tr>
<tr><td> Event Aggregation Module (in progress, won’t be finished by 11/20) </td><td>  New module that aggregates a stream of incoming messages until a specified time. Once this time is reached, the aggregated messages are combined into one or many messages and dispatched. </td><td>                     </td><td> <a href='https://docs.google.com/a/grameenfoundation.org/document/d/1p7qn9vomM-FAOTLuZsx4T7b5KxD6N4MIZkfOwmMeL1o/edit'>Link</a> </td></tr></tbody></table>

<h1>New cards for 0.16</h1>
<table><thead><th> <b>Feature</b> </th><th> <b>Description</b> </th><th> <b>Key Customer(s)</b> </th><th> <b>Spec</b> </th></thead><tbody>
<tr><td> Tasks – Iteration 1 </td><td> Next rev of the “ifttt”-style workflow engine. This release will add a functional (if not polished) UI for authoring. </td><td> Ethiopia <br>MOTECH Cloud customers </td><td> <a href='https://docs.google.com/a/grameenfoundation.org/file/d/0B6__Hl_QLjI4aXotc0dBRDJXbU0/edit'>Link</a> </td></tr>
<tr><td> IVR enhancements </td><td> Enhancements to the IVR API, events, interface and reports within MOTECH. </td><td> MOTECH Cloud customers </td><td> <a href='https://docs.google.com/a/grameenfoundation.org/document/d/1fW2rcDGsrTstvVF16o8W_vmvZJ_PEILqimhiUMSbMfo/edit'>Link</a> </td></tr>
<tr><td> SPIKE: REST frameworks </td><td> Evaluate REST frameworks and decide which is appropriate for MOTECH. </td><td> All                    </td><td> <a href='https://docs.google.com/a/grameenfoundation.org/document/d/10fNflt8Ouhj7aTT5V077Kz6T88gfnVURhgaJnV-9ZeQ/edit'>Link</a> </td></tr>
<tr><td> "CouchMRS" Person Repository </td><td> Concrete implementation of medical records system API backed by Couch. For the first iteration, implement the MRSPerson object only. </td><td> MOTECH demos<br>MOTECH Cloud customers</td><td> <a href='https://docs.google.com/a/grameenfoundation.org/document/d/1TwobiExwnFTsfTetvufuDBkLVCIaEYUhI4YZXGd9D8Y/edit'>Link</a> </td></tr>
<tr><td> Publish one-step install packages </td><td> Add a step to the release that publishes the one-step install packages to a server. </td><td>                        </td><td> <a href='https://trello.com/c/SgGDCl7w'>Link</a> </td></tr></tbody></table>

<h1>Bugs</h1>
<ul><li>Very slow MOTECH startup<br>
</li><li>Harcoded smslib queue directory<br>
</li><li>Openmrs-api-bundle modules restart does not work<br>
</li><li>org.apache.commons.io osgi dependency out of date