=================================
Developing and Submitting a Patch
=================================

We use GitHub to manage our source code. MOTECH org members are able to create branches and commit changes to the MOTECH code repositories. Community members utilize the `fork and pull GitHub workflow <https://help.github.com/articles/using-pull-requests/>`_.

Community Member Development Workflow
=====================================
This is the most straightforward way to submit a patch as a community member. Note that GitHub allows you to submit only one pull request per branch at a time. If you wish to work on multiple features at once, it's best to create a branch for each feature and create a pull request from that branch to our repository.

#. Fork the :doc:`MOTECH repository <repositories>` you wish to develop
#. Clone your fork on your development machine
#. Checkout a new feature branch

    .. code-block:: bash

        git checkout -b newfeature

#. Make changes/test/multiple commits
#. When ready to submit changes: update master, squash commits and merge the feature branch

    .. code-block:: bash

        git checkout master && git pull --rebase
        git merge --squash newfeature
        git gui

#. Edit commit message using the proper :doc:`commit message format <commit_message>`
#. Push changes

    .. code-block:: bash

        git push origin
#. Submit a pull request

Submitting Changes to Incorporate Review Comments
=================================================

Our team will review your pull request in GitHub. If you received feedback during this process, GitHub allows you to add commits to your pull request. Just commit your changes and the pull request will automatically update.