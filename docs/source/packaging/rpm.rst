==========================
MOTECH packaging using DEB
==========================

This is a description of building process for RPM packages on CentOS 7.0

Requirements
============

* Apache Tomcat 7.0 or greater, can be installed in terminal with "yum" command

.. code-block:: bash

    yum install tomcat.(architecture)

* Java 1.8 or greater, can be installed in terminal with "yum" command

.. code-block:: bash

    yum install java-1.8.0-openjdk-src.(architecture)

* Apache Maven 3 (needed only for building package)

Building process
================

1) move to motech project directory
2) change directory to ./packaging/rpm
3) run maven command with "RMP" profile

.. code-block:: bash

    mvn clean install -PRPM

4) in the folder "./target/rpm/motech-base/RPMS/noarch/" should appear file

- "motech-base_(release number)-SNAPSHOT(some number).noarch.rpm"

Installing Motech with RPM packages
===================================

Before installation be sure that all Tomcat processes are stopped

You can install Motech from RPM packages in terminal using "rpm" command

.. code-block:: bash

    sudo rpm -i FILE_NAME

where FILE_NAME is name of package with .rpm extension you want to install

Running Motech service
======================

Before running Motech service be sure that all Tomcat processes are stopped

You can run installed Motech on default Apache Tomcat server from terminal by using "service" command

.. code-block:: bash

    sudo service motech start

Motech can now be accessed in web browser under "localhost:8080"