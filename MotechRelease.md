I've created a python script that automates all the steps for branching.  I haven't done the release:perform portion yet.  I'm also not doing the demo repository.

https://github.com/motech/release

**Todo** Need to add a step for updating version numbers in documentation.  Currently the commands for using the archetype contain the version number.  There are probably others.
  1. summary Release instructions - for now, these include minimal QA/build verification, but over time these will be updated with additional validation steps.

# Building a Release #
  1. Check whether the Platform and Modules jobs are green on our [CI server](http://ci.motechproject.org)
  1. To cut and paste the commands below set the following environment variables to the appropriate values:
    * VERSION={version}
    * USERNAME={username}
  1. Create branches for the release (one for each repo):
    * MOTECH
```
mvn release:branch -!DbranchName=0.$VERSION.X -Dscm.connection=scm:git:ssh://$USERNAME@review.motechproject.org:29418/motech -Dscm.developerConnection=scm:git:ssh://$USERNAME@review.motechproject.org:29418/motech
```
    * Modules
```
mvn release:branch -!DbranchName=0.$VERSION.X -Dscm.connection=scm:git:ssh://$USERNAME@review.motechproject.org:29418/modules -Dscm.developerConnection=scm:git:ssh://$USERNAME@review.motechproject.org:29418/modules
```
  1. Create CI jobs for new branches. Please note that the core platform needs to be built first. List of jobs:
    * MOTECH-Platform-{version},
    * Modules-{version}
  1. Configure the CI jobs
> > The only difference between the job configurations should be their scm parameters, as all of the jobs do releases for different repositories.
> > For gerrit repositories, the only changing part is the project name in the gerrit connection url. Platform-demo uses a different url, as its only hosted on github.
> > Verify that the correct urls are in fact specified in the following places:
    * Source Code Management - the url used to checkout the repository
    * Advanced options of the release step - scm.connection and scm.developerConnection
    * Generate javadoc step(Invoke Ant) - scm.connection and scm.developerConnection in the properties
> > The correct scm urls for their respective repositories are:
      * scm:git:ssh://$USERNAME@review.motechproject.org:29418/motech
      * scm:git:ssh://$USERNAME@review.motechproject.org:29418/modules
    * Set releaseVersion = {version} (e.g. 0.13)
    * Set developmentVersion = {version}.1-SNAPSHOT (e.g. 0.13.1-SNAPSHOT)
    * Set scm.tag = release-{version} (e.g. release-0.13)
  1. Manually verify that the Maven archetype is working on the release branch for the Platform repo. Build MOTECH and install the server .war on a test server. [Build the archetype](GettingStartedImplementer.md) and install it on the test server. Validate that the archetype presents a simple UI. Soon we will enhance the archetype with additional functionality, so there will be more to verify in this step.
  1. Trigger release build for motech from the Jenkins UI
  1. For modules, you will need to edit the main pom and change motech.version from the SNAPSHOT to the newly released platform version
  1. Trigger release build for each module repository

# If Something Goes Wrong... #
If the release build fails for any reason, it will be necessary to do some cleanup before attempting a retry.

  1. Depending on how far along the build process got before failing, some artifacts may have been uploaded to Nexus. You will need to expand the directories for each bundle and delete these artifacts manually if they exist.
  1. Jenkins makes two checkins when preparing the release and preparing for the next development iteration. These will need to be reverted. The commit message subjects will be similar to the following:
    * [maven-release-plugin] prepare for next development iteration
    * [maven-release-plugin] prepare release motech-0.21
    * Push changes to the remote branch via Gerrit using git push origin HEAD:refs/for/BRANCH\_NAME
  1. If the tag for the new release was created, it will need to be deleted.
    * git push --delete origin motech-0.VERSION

After these issues are addressed (and the root cause of the release failure is investigated/fixed), it should be safe to retry the release build.