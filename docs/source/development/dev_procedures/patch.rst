=================================
Developing and Submitting a Patch
=================================

We use a web-based code review system called `Gerrit <https://code.google.com/p/gerrit/>`_. Using this system, anyone can comment on a proposed change before it is merged to our Git repository. It's pretty easy to use; the instructions below will get you started.

Create a Gerrit Account
=======================
1. Navigate to http://review.motechproject.org/
2. Click sign in on top-right
3. Select Open ID provider (only Google accounts are enabled)
4. Select user name
5. Upload your SSH public key

Configuring Your Git Client to Use Gerrit
=========================================

Follow these steps once for each MOTECH code repository that you clone.
 
1. Get source code 

    .. code-block:: bash
    
	    git clone ssh://<userid>@review.motechproject.org:29418/motech

2. Set up review branch

    .. code-block:: bash
    
        cd motech
        git config remote.origin.push refs/heads/*:refs/for/*

3. Install change-id generation hook

    .. code-block:: bash
    
	    scp -p -P 29418 <userid>@review.motechproject.org:hooks/commit-msg .git/hooks/

Development Workflow
====================
    
1. Checkout to feature branch

    .. code-block:: bash
    
        git checkout -b newfeature

2. Make changes/test/multiple commits
3. When ready to submit changes: update master, squash commits and merge feature branch

    .. code-block:: bash
    
        git checkout master && git pull --rebase
        git merge --squash newfeature
        git gui 

4. Edit commit message using the proper :doc:`commit message format <commit_message.txt>`
5. Push changes

    .. code-block:: bash

        git push origin

Submitting Changes (Patch Set) to Incorporate Review Comments
=============================================================

If you've received some code review feedback and you'd like to make some changes, follow the steps below to add your changes as a new "patch set" to the existing Gerrit code review.

1. Checkout patch from gerrit change:
    a. Navigate to http://review.motechproject.org/#/c/<change id>/
    b. Copy pull url under patch set section and run
2. Make changes
3. Copy change ID from Gerrit (top section in Gerrit change page)
4. Amend change ID in commit message
5. Push changes

Pushing to Remote Branches (Not for Review)
===========================================
This practice enables developers to share in-progress feature work with others without actually submitting the changes for review.

1. Use branch namespace dev

    .. code-block:: bash
    
        git checkout -b dev/newfeature
        git add . && git commit -m "message" 
        git push -u origin dev/newfeature

2. Once done with feature, squash commits and merge with master. Submit for review as mentioned above.

Additional Information
======================
* http://review.motechproject.org/Documentation/user-upload.html
* http://review.motechproject.org/Documentation/user-changeid.html