========================
MOTECH Code Repositories
========================

Each repository can be cloned from GitHub or Gerrit. If you're only interested in a copy of the code and will not be contributing, use the GitHub repo (no sign-in required). Otherwise use the Gerrit repo (sign-in required) where each commit will trigger a Jenkins build and be submitted for code review. Jenkins is our continuous integration (CI) system. Once your change is approved and merged by an authorized Gerrit reviewer, it will show up on the GitHub repo.

MOTECH Platform
===============
The platform repo contains the motech-platform-server Tomcat servlet. In addition it also contains the essential Admin, Config, Tasks, Motech Data Services, Email, and Scheduler modules.

* GitHub Repository

    .. code-block:: bash

        git clone https://github.com/motech/motech

* Gerrit Repository

    .. code-block:: bash

        git clone ssh://<userid>@review.motechproject.org:29418/motech


MOTECH Modules
==============
This repo is the home of all optional MOTECH modules.

* GitHub Repository

    .. code-block:: bash

        git clone https://github.com/motech/modules

* Gerrit Repository

    .. code-block:: bash

        git clone ssh://<userid>@review.motechproject.org:29418/modules