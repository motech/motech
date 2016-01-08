=================================
Developing and Submitting a Patch
=================================

We use GitHub to manage our source code. MOTECH org members are able to create branches and commit changes to the MOTECH code repositories. Community members utilize the `fork and pull GitHub workflow <https://help.github.com/articles/using-pull-requests/>`_.

Community Member Development Workflow
=====================================
This is the most straightforward way to submit a patch as a community member. Note that GitHub allows you to submit only one pull request per branch at a time. If you wish to work on multiple features at once, it's best to create a branch for each feature and create a pull request from that branch to our repository.

#. Fork the :doc:`MOTECH repository <repositories>` you wish to develop
#. Clone your fork on your development machine
#. `Add an upstream branch <https://help.github.com/articles/configuring-a-remote-for-a-fork/>`_ to your fork for easy syncing
#. Checkout a new feature branch

    .. code-block:: bash

        git checkout -b newfeature

#. Make changes/test/multiple commits
#. When ready to submit changes: update master from upstream, squash commits and merge the feature branch

    .. code-block:: bash

        git checkout master
        git fetch upstream
        git merge upstream/master
        #This merge is optional because you can push your local branch to your fork and create a pull request from there
        git merge --squash newfeature
        git gui

#. Edit commit message using the proper :doc:`commit message format <commit_message>`
#. Push changes

    .. code-block:: bash

        git push origin

#. Submit a pull request from your fork to the motech repo.

Submitting Changes to Incorporate Review Comments
=================================================

Our team will review your pull request in GitHub. If you received feedback during this process, GitHub allows you to add commits to your pull request. Just commit your changes and the pull request will automatically update.