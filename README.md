MOTECH Platform
---------------

The MOTECH Platform is an open source enterprise software package that connects popular eHealth technologies to unlock new outcomes toward strengthening health systems. MOTECH has been deployed across the globe in numerous health domains including maternal and child health, treatment adherence, frontline worker education and information collection.

MOTECH consists of a core platform and optional modules, each providing use of a technology such as SMS or email, or access to an external system such as CommCare or OpenMRS. Implementers can choose to install one or more modules, and developers can extend MOTECH by writing new modules. This repository contains the code for the core platform, which comes with a few essential modules. Many additional modules may be found in our [Modules repo](http://github.com/motech/modules).

Interested in learning more about MOTECH? Try the following resources:
* [MOTECH Project Website](http://motechproject.org)
* [MOTECH Documentation](http://docs.motechproject.org)
* [Issue Tracker](https://applab.atlassian.net/projects/MOTECH/summary)
* [Mailing List](https://groups.google.com/forum/?fromgroups#!forum/motech-dev)

Installation
------------

### Platform

If you'd like to install and run the latest MOTECH binaries, go [here](http://docs.motechproject.org/en/latest/get_started/installing.html).

If you'd prefer to build MOTECH yourself, try [these instructions](http://docs.motechproject.org/en/latest/development/dev_setup/dev_install.html) instead.

### Modules

The Platform war file contains all modules required for starting and managing MOTECH. To install additional modules, you can either use the Admin UI to install them at runtime or place them in the `~/.motech/`bundles directory and restart MOTECH. Note that doing a `mvn clean install` on any of our modules will place that module in the `~/.motech/bundles` directory automatically. Modules from that directory always override the ones contained in the war if their Bundle-Version and Bundle-SymbolicName are the same.

Contributing
------------

We welcome contributions from the open source community. For instructions on how to get started as a MOTECH contributor, please check out the [Contribute](http://docs.motechproject.org/en/latest/contribute/index.html) section of our documentation.

Disclaimer Text Required By Our Legal Team
------------------------------------------

Third party technology may be necessary for use of MOTECH 2.0. This agreement does not modify or abridge any rights or obligations you have in open source technology under applicable open source licenses.

Open source technology programs that are separate from MOTECH are provided as a courtesy to you and are licensed solely under the relevant open source license. Any distribution by you of code licensed under an open source license, whether alone or with MOTECH, must be under the applicable open source license.
