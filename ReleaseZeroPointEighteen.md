# New Features #
  * REST API for Message Campaign
  * Tasks Module iteration #6 - string manipulation functions
  * Tasks Module iteration #7 - additional data sources and data binding
  * IVR call logs and UI
  * Support CommCare fixture API
  * Integration tests for one-step install
  * REST API for send SMS
  * Multi-tenant infrastructure: CouchDB


# Integration Notes #
The following changes may be of interest to implementers because they entail API or functionality changes that may require modifications to existing implementation code upon upgrade to MOTECH 0.18.
  * [REST API for send SMS](https://trello.com/c/EUssztHl) - some changes to the SmsService API, including refactoring SendSms parameters into an SmsRequest object
  * [Deprecated methods in decision tree](https://trello.com/c/Ehd5AS1H) - removes deprecated methods getRootNode and setRootNode
  * [Cron jobs and !DayOfWeek jobs should support ignorePastFiresAtStart flag](https://trello.com/c/xlJATuBo) - adds support for new flag, includes signature change for public constructors


For the complete list of cards that were completed for this release (including many small enhancements and bug fixes not listed above), see the [Releases](https://trello.com/board/releases/5087292416df848e4c001c88) board.