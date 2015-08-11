# New Features #

  * OpenMRS/MOTECH integration - ATOM feed module
  * Call logs for IVR decision tree
  * Unique ID for every MOTECH event
  * Spring Security Couch
  * OpenMRS-api config panel
  * Support CommCare's new form stub feed

# Bug Fixes #

  * No DTMF Input scenario not handled in Kookoo module
  * Remove prefix from folders which is the same as folder name inside which they are located
  * getCampaignTimings in message campaign service should not return timings with campaign is inactive / stopped.
  * SmsHttpService PostMethod to use PostMethod.FORM\_URL\_ENCODED\_CONTENT\_TYPE  as the Content-Type
  * Should be able to nest play tag into gather tag.
  * Set various checkstyle violations' severity to error
  * java.lang.IllegalArgumentException thrown when accessing admin UI, caused by config files from bundles which were removed
  * Fix admin UI layout issues
  * UI improvements for motech-demo bundle

# Development Operations Improvements #
  * Improvements to release process
  * CI server enhancements

For the complete list of cards that were completed for this release, see the [Releases](https://trello.com/board/releases/5087292416df848e4c001c88) board.