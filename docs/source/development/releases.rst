===============
Release Process
===============

MOTECH releases are created roughly quarterly, with some bigger releases taking longer. See the :doc:`Roadmap <../roadmap>` for guidance on when to expect the next release.

This page provides step-by-step instructions for creating an official release. Many of these steps require elevated permissions in Gerrit and Jenkins, and are intended to be performed by a member of the release management team.

Create the Release Branch and Associated CI jobs
================================================

There are two ways to create a release branch and the associated release jobs in our CI:

1. Using our handy release script that automates most of the steps
2. Manually

Option 1 - Release Script
-------------------------

We have a Python script at https://github.com/motech/release that automates all of the steps for branching. It expects the following paramaters:

Required
^^^^^^^^
--jenkinsUsername      A user in jenkins who has permission to create new jobs
--jenkinsPassword      The password for the jenkins user
--gerritUsername       A username in gerrit who is in the Bypass Review group.
--version              The version of the release i.e. 0.22
--developmentVersion   The next development version on the branch i.e. 0.22.1-SNAPSHOT
--nextMasterVersion    The next working version on master i.e. 0.23-SNAPSHOT

Optional
^^^^^^^^
--buildDirectory       The base location to check out all source to. Will delete if it exists. Defaults to ./builds
--verbose              Be a little chatty on stdout

Option 2 - Manual Process
-------------------------

1. Cut and paste the following commands to set your environment variables to the appropriate values:

    VERSION={version}

    USERNAME={username}

2. Create branches for the release (one for each repo):

    .. code-block:: bash

        mvn release:branch -!DbranchName=0.$VERSION.X -Dscm.connection=scm:git:ssh://$USERNAME@review.motechproject.org:29418/motech -Dscm.developerConnection=scm:git:ssh://$USERNAME@review.motechproject.org:29418/motech
        mvn release:branch -!DbranchName=0.$VERSION.X -Dscm.connection=scm:git:ssh://$USERNAME@review.motechproject.org:29418/modules -Dscm.developerConnection=scm:git:ssh://$USERNAME@review.motechproject.org:29418/modules

3. In the Jenkins UI, create CI jobs for the new branches. List of jobs:

    Platform-{VERSION}.X

    Modules-{VERSION}.X

The most straightforward way to create the jobs is to copy the config from a previous release. Any other method will be error-prone, as there are many fields to configure, several of which are hidden under "Advanced" sections.

Test the Release Candidate
==========================

After the release branch has been created, user acceptance testing (UAT) begins. The release management team will make an initial pass over the tickets that were resolved for the release, and close those that are low-priority for UAT. All others should be tested and closed before building the official release. If testing reveals that an issue requires more work, the ticket should be reopened with fixVersion=Inbox so that it can be triaged at the next Inbox Review Meeting. A decision will be made as to whether the issue should be fixed on the release branch or moved to the next release.

Additional release criteria or functional areas for UAT may be added for specific releases, depending on the needs of partners or the desire to stress major new functionality. In future, release criteria will also include API freeze tools passing (API freeze tools aren't in place yet), documentation requirements, and a code coverage minimum.

Build the Release
=================

Once the build has been tested and you are ready to create the official release, you can do so from the Jenkins UI.

1. Trigger the platform release build from the platform-{VERSION}.X CI job first.
2. Wait and ensure that the platform build completes successfully.
3. Trigger the modules release build from the modules-{VERSION}.X CI job.

If Something Goes Wrong...
--------------------------

If the release build fails for any reason, it will be necessary to do some cleanup before attempting a retry.

1. Depending on how far along the build process got before failing, some artifacts may have been uploaded to Nexus. You will need to expand the directories for each bundle and delete these artifacts manually if they exist. Subsequent release attempts will fail if these artifacts aren't removed.

2. Jenkins makes two checkins when preparing the release and preparing for the next development iteration. These will need to be reverted. The commit message subjects will be similar to the following:
    [maven-release-plugin] prepare for next development iteration

    [maven-release-plugin] prepare release motech-{VERSION}

Push changes to the remote branch via Gerrit using git push origin HEAD:refs/for/{VERSION}.X

3. If the tag for the new release was created, it will need to be deleted.
    git push --delete origin motech-{VERSION}

After these issues are addressed (and the root cause of the release failure is investigated/fixed), it should be safe to retry the release build.

Release Notes
=============

Release notes should be published under the :doc:`Release Notes<../releases/index>` section on our documentation site. They should contain pointers to the binaries and source code, a summary of major changes delivered in the new release, and a list of known issues (with workarounds when applicable).
