# New Features #
  * MOTECH Admin Authentication
  * Tasks Module: Iteration #0 and #1
  * Event Aggregation Module
  * Enhance schedule tracking service interface
  * MOTECH Custom Module Bootstrap
  * Create a repository for MRS Person Entity in CouchDB
  * OSGi integration tests for core modules, Tasks module
  * Send test SMS and IVR call from Config Panels
  * Enhance MRS encounter service

# Bug Fixes #

  * Various Checkstyle and PMD fixes
  * CouchMRS bundle needs to export service through OSGi
  * Event aggregation published events are not properly serialized
  * Admin security enabled when web-security bundles starts
  * Clean up bundle names
  * Error message when trying to create new user through 'Manage Users' UI
  * Fix footer position
  * OsgiListener class(for restarting osgi bundle) from non-osgi env used in org.motechproject.commcare.web.SettingsController with static reference
  * ActiveMQ bundle missing import
  * Fix deployment-test
  * openmrs-api-bundle module restart does not work
  * Exception when starting atomfeed module
  * Hardcoded smslib queue directory
  * java.io.NotSerializableException: org.motechproject.security.service.MotechUserProfile

For the complete list of cards that were completed for this release, see the [Releases](https://trello.com/board/releases/5087292416df848e4c001c88) board.