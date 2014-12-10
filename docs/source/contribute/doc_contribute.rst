=============
Documentation
=============

We could really use some help telling our story, and we'd love your help.

First, a bit about how our docs are stored and managed. Each MOTECH code repository contains a *docs* directory populated with reStructured Text (reST) files. These files are built by Sphinx and hosted at http://readthedocs.org. :doc:`This page <../development/documentation>` contains more information about reStructuredText and how to build the docs on your local machine. Once you've written and built your docs locally, you can just check them in and they'll automatically appear on our docs site after the next doc build.

The instructions below will let you know how to get started with adding/editing MOTECH documentation.

.. note::

    If you are a writer (not a software developer!) and you find that committing documentation to git is a bit daunting, `drop us a line <mailto:motech-dev@googlegroups.com>`_. We can provide extra support through the process (or even check in your docs for you).

Mailing Lists and Accounts
==========================

#. Sign up for the MOTECH Developer `mailing list <https://groups.google.com/forum/?fromgroups#!forum/motech-dev>`_ - this is the best place to get help with any questions about MOTECH documentation or code.
#. Create an account on `Gerrit <http://review.motechproject.org>`_, our code review system.
#. Get a Jira account if you'll be working on documentation tickets - there is no self-serve way to request a Jira account yet, so please just email the MOTECH Developer list and we'll get you set up.

Doc Environment & Tools
=======================

#. Install Sphinx and Javasphinx, and test out building the docs locally. Full instructions :doc:`here <../development/documentation>`.
#. Configure your Git client to :doc:`submit changes via Gerrit <../development/patch>`.
#. Install an editor for reStructuredText. Any editor will work, but we find that `Sublime <http://www.sublimetext.com/>`_ works pretty well.

Finding Something to Work On
============================

#. All planned documentation topics for MOTECH are tracked in Jira -- you are welcome to pick any unassigned ticket from `this query <https://applab.atlassian.net/issues/?filter=19964>`_. Note that many of the tickets have sub-tickets as well, so you can drill down to find additional unassigned topics.
#. If the topic you want to write about doesn't appear to be tracked in Jira, `email the list <mailto:motech-dev@googlegroups.com>`_ and let us know. We'll help you determine where the topic fits in our ToC and create a Jira ticket for it.
#. Assign the Jira ticket to yourself and click "Start Progress" when you're ready to start writing.

Writing and Submitting Your Doc
===============================

#. As you are writing your doc, we recommend building periodically to ensure that the doc looks the way you expect. It can take some trial and error to get the hang of reStructuredText markup.
#. When you're ready to push your changes, please squash your commits to keep the change history concise, and write a commit message that conforms to our :doc:`guidelines <../development/commit_message>`.
#. Submit your doc using **git push origin** - if you configured your environment correctly, this sends your changes to Gerrit, our code review system.
#. Please incorporate review feedback and update your patch as needed - once your change has passed code review, one of the project maintainers will merge your change to the repo.
#. Resolve the relevant issue(s) in Jira.
