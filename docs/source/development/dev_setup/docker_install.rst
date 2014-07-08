=======================================
Installing MOTECH Using Docker ("Beta")
=======================================

.. note::
    These instructions assume you're running on Ubuntu. This setup is also possible on Mac OSX but the steps are slightly different. We hope to provide OSX instructions in future.

This document provides instructions for creating a MOTECH environment using `Docker <http://www.docker.io>`_ containers. These instructions are "in beta" (the *official* installation guide is still the one :doc:`here <dev_install>`), but many members of the MOTECH dev team have been following this approach with success. This installation method is much faster than the official route.

There are two supported ways to install MOTECH with Docker:

1. As an implementer - follow this approach if you want to install a released version of MOTECH.
2. As a developer - follow this approach if you will be developing MOTECH and want to build the platform and modules from source code.

Get Docker, Fig and motech-docker
=================================

Whether you're installing as an implementer or a developer, you'll need Docker and Fig:

Docker
------

1. Follow the instructions on the `Docker website <https://docs.docker.com/installation/ubuntulinux/>`_ to get the latest version of Docker.
2. Execute the following to configure Docker to work for non-root users:

    .. code-block:: bash

        sudo groupadd docker
        sudo gpasswd -a ${USER} docker (logout and re-login)
        sudo service docker restart

Fig
---

Execute the following to install Fig:

.. code-block:: bash

    sudo apt-get install python-pip python-dev build-essential
    sudo pip install -U fig

motech-docker
-------------

Clone the `motech-docker <https://github.com/motech/motech-docker>`_ project from GitHub or download it as a zip file and extract it. You'll need to run all Fig commands from the motech-docker directory.


Implementer Setup
=================

Go to your motech-docker directory. To setup as an implementer (everything is automagically installed):

.. code-block:: bash

    ./setup_as_imp.sh

Type the following to start MOTECH in the background:

.. code-block:: bash

    fig up -d

Voila! MOTECH has started. Wait a little bit (about 30s) then direct your browser to: http://localhost:8080/motech-platform-server

.. note::
    'fig up' ERASES ALL YOUR DATA (well not really all, but pretend it does). You have to run it at least once to setup MOTECH. If you run it again, it'll erase everything you did in MOTECH. It's useful to start afresh, but remember: it nukes everything!

Developer Setup
===============

Go to your motech-docker directory. To setup as a dev:

.. code-block:: bash

    ./setup_as_dev.sh

Type the following to start all the pieces that MOTECH needs to run in the background:

.. code-block:: bash

    fig up -d

Once you start the containers with the fig up -d command above and *before* you build MOTECH for the first time, you need to copy the MOTECH binaries into the container's /root/.motech/bundles directory.

Conveniently, the container's /root/.motech/bundles directory is exposed as the docker-motech-bundles directory (with a-rw access) in your home directory (also note that the container's /root/.motech/config dir is also exposed as ~/docker-motech-config). So, you can either manually copy the necessary binaries, or you can create a symbolic link to ~/docker-motech-bundles from ~/.motech/bundles.

Assuming the latter, and that you never built MOTECH before, you'd run the following commands:

.. code-block:: bash

    # go to your home dir
    cd
    # create the .motech dir
    mkdir .motech
    # create the symlink
    ln -s ~/docker-motech-bundles .motech/bundles

If you built MOTECH before, you can just delete the bundles directory and create the symlink using the command above.

Build, deploy and run MOTECH: see :doc:dev_install.

Some Useful Fig Commands
========================

Stop MOTECH
-----------

.. code-block:: bash

    fig stop

Restart MOTECH
--------------

.. code-block:: bash

    fig start

Watching logs
-------------

To watch all the logs (very verbose):

.. code-block:: bash

    fig logs

To watch only the tomcat logs:

.. code-block:: bash

    fig logs tomcat

See the sections in the generated fig.yml to see what other logs you can watch.