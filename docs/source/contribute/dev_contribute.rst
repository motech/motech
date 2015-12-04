===========
Development
===========
Want to help us develop MOTECH? The step-by-step guide below will help you get started. Please let us know if you see something is missing or incorrect in these instructions. Our mailing list is populated with helpful people who will help you get going - and will get this documentation up to date with any issues you encounter.

Here is how to get started:

Mailing Lists and Accounts
==========================
#. Sign up for the MOTECH Developer `mailing list <https://groups.google.com/forum/?fromgroups#!forum/motech-dev>`_ - this is the best place to get help with any questions about MOTECH development.
#. Create an account on `GitHub <https://github.com>`_, our code repository and review system.
#. Get a Jira account if you'll be working on documentation tickets - there is no self-serve way to request a Jira account yet, so please just email the `MOTECH Developer list <mailto:motech-dev@googlegroups.com>`_ and we'll get you set up.

Dev Environment & Tools
=======================
#. Configure your :doc:`dev machine <../development/dev_setup/dev_install>`.
#. `Fork one of our GitHub repositories <https://github.com/motech/>`_.
#. Clone your repository locally and enter the motech directory.
#. Familiarize yourself with our `CI <http://ci.motechproject.org/>`_.

Finding Something to Work On
============================
#. We recommend that you find a `community issue <https://applab.atlassian.net/issues/?jql=labels%20%3D%20community>`_ from our issue tracker These are bugs and user stories that we think are good introductory items for new MOTECH community members.
#. If you're already building your own system on top of MOTECH and you'd like us to incorporate your changes for an issue you've found and fixed, please first check whether the issue already exists in our issue tracker. If you are not sure, please email the mailing list and we'll help you determine whether the issue is already known. Please track your work with a new issue so that we can evaluate it for inclusion in the platform.

Developing and Submitting Your Code
===================================
#. If your fix will be nontrivial, please leverage the mailing list for feedback on your design before you get too far - we are a friendly bunch and can help ensure you are headed in the right direction.
#. When you're ready to push your changes, please squash your commits to keep the change history concise, and write a commit message that conforms to our :doc:`guidelines <../development/commit_message>`.
#. Generate a pull request from your forked repository and our team will review it.
#. Please incorporate code review feedback and update your pull request as needed. Once your change has passed code review, one of the project maintainers will merge your change to the repo.
#. Resolve the relevant issue(s) in Jira.

Developing and Contributing Your Module
=======================================
#. If you have your own module that you would like to contribute, let us know by sending email on motech-dev@googlegroups.com (or post on our `mailing list <https://groups.google.com/forum/?fromgroups#!forum/motech-dev>`_). It should contain basic information about your module, such as description, what the module does, if it needs any external configuration and how to test it properly.
#. We will create a Jira issue with high priority for reviewing the module. It will be reviewed and discussed on our weekly call.
#. The Jira issue will be assigned to someone, who will work with the contributor to fix any possible issues. At this time only major bugs or blocking issues will be addressed. Minor issues will get their own Jira tickets and will be resolved in later time, depending on the priority we assign them.
#. When the module is ready, we will fork it to the `Community Modules Repository <https://github.com/motech-community-modules>`_ and we will give the contributor rights to administer his module.
#. Note that we can reject a module contribution if we deem it improper to maintain it on our side. In case of any further questions feel free to ask on our mailing list.