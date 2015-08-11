# New Features #
  * CouchMRS: implement Observations and Encounters
  * Tasks: enable module methods as Task actions
  * SMS logging UI
  * Tasks: add ability to register channel actions programmatically
  * Tasks: allow users to specify the order in which operations are executed
  * MRS UI panel
  * Add new events to various modules: MRS, Outbox, Message Campaign
  * CMS Lite UI panel
  * Message Campaign UI panel (authoring)
  * Tasks iteration #8: error handling
  * Multi-Tenant Infrastructure: ActiveMQ (prefix queue name)
  * Enable installation of multiple MOTECHs on a single machine
  * Register CMS Lite as Tasks data provider

# Integration Notes #
The following changes may be of interest to implementers because they entail API or functionality changes that may require modifications to existing implementation code upon upgrade to MOTECH 0.19.
  * The CommCare module has been updated to integrate with CommCare API version 0.3 (previously 0.1)

For the complete list of cards that were completed for this release (including many small enhancements and bug fixes not listed above), see the [Releases](https://trello.com/board/releases/5087292416df848e4c001c88) board.