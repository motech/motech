========================
MOTECH Code Repositories
========================

Each repository can be cloned from either GitHub/Google Code or Gerrit. If you're only interested in a copy of the code and will not be contributing, use the GitHub repo (no sign-in required). Otherwise use the Gerrit repo (sign-in required) where each commit will trigger a Jenkins build and be submitted for code review. Jenkins is our continuous integration (CI) system. Once your change is approved and merged by an authorized Gerrit reviewer, it will show up on the GitHub/Google Code repo.

MOTECH Platform
===============
The platform repo contains the motech-platform-server Tomcat servlet. In addition it also contains the essential Admin, Config, Tasks, Motech Data Services, Email, and Scheduler modules.

* Google Code Repository

    .. code-block:: bash

        git clone https://code.google.com/p/motech

* Gerrit Repository

    .. code-block:: bash

        git clone ssh://<userid>@review.motechproject.org:29418/motech


MOTECH Modules
==============
This repo is the future home of all optional MOTECH modules. It will replace the Campaigns, Medical Records, and Communications repos discussed later in this document.

* GitHub Repository

    .. code-block:: bash

        git clone https://github.com/motech/modules

* Gerrit Repository

    .. code-block:: bash

        git clone ssh://<userid>@review.motechproject.org:29418/modules


Campaigns
=========
*Note: This repo will soon be merged into the MOTECH Modules repo.*

The campaigns repo contains the Pill Reminder, Message Campaign, Schedule Tracking modules.

* GitHub Repository

    .. code-block:: bash

        git clone https://github.com/motech/platform-campaigns

* Gerrit Repository

    .. code-block:: bash

        git clone ssh://<userid>@review.motechproject.org:29418/platform-campaigns

Medical Records
===============
*Note: This repo will soon be merged into the MOTECH Modules repo.*

The medical records repo contains the MRS, OpenMRS, Appointments modules.

* GitHub Repository

    .. code-block:: bash

        git clone https://github.com/motech/platform-medical-records

* Gerrit Repository

    .. code-block:: bash

        git clone ssh://<userid>@review.motechproject.org:29418/platform-medical-records

Communications
==============
*Note: This repo will soon be merged into the MOTECH Modules repo.*

The communications repo contains the Alerts, Call Flow, CMS Lite, Decision Tree, Mobile Forms, IVR, Outbox, CommCare, SMS modules.

* GitHub Repository

    .. code-block:: bash

        git clone https://github.com/motech/platform-communications

* Gerrit Repository

    .. code-block:: bash

        git clone ssh://<userid>@review.motechproject.org:29418/platform-communications