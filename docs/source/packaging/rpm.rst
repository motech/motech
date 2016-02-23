==========================
MOTECH packaging using RPM
==========================

This is a description of building process for RPM packages on CentOS 7.0

Requirements
============

* for Apache Tomcat, Java 8 and Apache Maven you can view ` <http://docs.motechproject.org/en/latest/development/dev_setup/dev_install.rst>`_.

Building process
================

1) move to motech project directory
2) run maven command with "RMP" profile

.. code-block:: bash

    mvn clean install -PRPM

3) in the folder "./packaging/rpm/target/rpm/motech-base/RPMS/noarch/"

"motech-base-(release number)-SNAPSHOT(some number).noarch.rpm" file should appear

4) in the folder "./target/rpm/motech-base/RPMS/noarch/"

"motech-(release number)-SNAPSHOT(some number).noarch.rpm" file should appear

Both files are needed for installation.

Installing Motech with RPM packages
===================================

Before installation be sure that all Tomcat processes are stopped

Firstly you have to install "motech-base" package, then "motech".

You can install RPM packages in 2 ways:
- in terminal using "yum" command

.. code-block:: bash

    sudo yum -i FILE_NAME

where FILE_NAME is name of package with .rpm extension you want to install

- from folder explorer by mouse double click on .rpm file you want to install,
    then in the Application Installer window click on the "Install" button

.. image:: rpm-install.png
    :scale: 50%

Running Motech service
======================

Before running Motech service be sure that all Tomcat processes are stopped

You can run installed Motech on default Apache Tomcat server from terminal by using "service" command

.. code-block:: bash

    sudo service motech start

Motech can now be accessed in web browser under "localhost:8080"