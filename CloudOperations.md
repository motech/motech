This space will be used to document steps for the following operational requirements:

  * Provisioning a server - can be done manually today, chef script would be ideal
  * Installing MOTECH instance (for now we do this using the .deb packages, which won’t work for multiple instances - we need a card to modify the packages to put all files in a single directory, and set a tenant ID)
  * Monitoring (is mysql up? is couch up? is ActiveMQ up?)
  * Backup/restore (Rackspace does file system backup - will work for Couch; write a cron job to do hourly backup of scheduler DB; we won’t back up the queue)
  * Upgrade (upgraded from 0.17 to 0.18 by building a new version of the .deb, running it, copying jars over)