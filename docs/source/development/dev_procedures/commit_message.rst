=====================
Commit Message Format
=====================

To ensure that our commit messages are both concise and informative, all MOTECH committers are asked to follow the git commit message format outlined below. For reference, Linus Torvalds provides a description of a good commit message `here <https://gist.github.com/matthewhudson/1475276>`_.

Format
======
#. First line is the Jira issue ID + subject (max 50 chars)
#. Leave a blank line (if not, the entire message is interpreted as subject)
#. Summary (ensure it is wrapped to a reasonable number of characters, e.g. 72, because git log does not handle wrapping). 

The description of the change should be both brief and informative. The Linux kernel documentation has this to say about good commit summaries:

    ...the "summary" must be no more than 70-75 characters, and it must describe both what the patch changes, as well as why the patch might be necessary. It is challenging to be both succinct and descriptive, but that is what a well-written summary should do.

Author/Committer
================
There is no need to include the names of the developers who developed the change in the subject line. If there is more than one person working on a change, the committer is asked to include the --author parameter in his/her git commit to specify the second person's name.

Example
=======
Here is an example of a MOTECH commit message:

    MOTECH-678 Moves campaign modules to github

    Disables pillreminder, message-campaign and scheduletracking modules
    from main pom.xml as they've moved to a new repo on github -
    https://github.com/motech/platform-campaigns

    Change-Id: I5964249887160c868fa9598c413aebb93a49fa32