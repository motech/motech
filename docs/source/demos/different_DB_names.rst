.. _different_DB_names:

============================================
Running MOTECH with different database names
============================================

.. contents:: Table of Contents
   :depth: 3

Background
==========
One of our partners requested a tutorial on running MOTECH with different database names so they can run multiple MOTECH instances in `Docker <http://www.docker.com>`_ containers pointing to a single SQL database. MOTECH is not setup to run as a multi-tenant SaaS system. That is, we assume one MOTECH instance maps to one information owner. This architecture was chosen to retain a separation of information and access to serve multiple customers. 

We created the capability to modify database names in MOTECH v0.28. This tutorial identifies the files and steps involved in changing the database names on Ubuntu 14.04. To be clear, MOTECH doesn't have the capability to create the database names before bootstrap, so we will proceed in the following three parts:

- *Part 1*: Setup and run a MOTECH default instance, configure it as you see fit
- *Part 2*: Copy the databases and .motech/config folder to create your default system backup
- *Part 3*: Modify the database names and deploy the new configuration

Part 1
------
The first step is to follow the :doc: `implementer's setup guide <../get_started/installing>`_ to create your default instance. You don't need to install any modules, but you'll need to make sure you complete the following steps:
- When you complete the bootstrap form **make sure to choose Configuration Mode: File** 
- After you click the verify button, you'll get an error about the web security activator not being able to start. That's because we need to modify some of the properties for file based configuration. Follow these steps to get everything configured correctly:

.. code-block:: bash

   #Stop the tomcat7 server
   sudo service tomcat7 stop
   #Create a file called motech-settings.properties that has the configuration variables
   echo -e "system.language=en\nlogin.mode=repository" | sudo tee --append /usr/share/tomcat7/.motech/config/motech-settings.properties
   #Change the file permissions to tomcat7:tomcat7
   sudo chown tomcat7:tomcat7 /usr/share/tomcat7/.motech/config/motech-settings.properties
   #Start tomcat7
   sudo service tomcat7 start

Next, login to the website, create a user and login to MOTECH.

Now, you have the following items that we're interested in working with:
- a .motech folder in the /usr/share/tomcat7/ directory
- 3 SQL databases named motechquartz, motechschema and motechdata

The .motech directory contains all of the configuration files in the .motech/config directory. We will modify files in this directory. We will also export these SQL databases, change the names and import them to the SQL server in the next parts.

Part 2
------
We now have a running instance of MOTECH on our machine. During this part, we will stop the Tomcat server, copy the appropriate files and create our default configuration folder.

.. code-block:: bash

   #Stop the tomcat7 server
   sudo service tomcat7 stop
   #Make a directory to hold the default config files and SQL dumps
   mkdir default_config
   cd default_config
   #Copy the .motech directory to the default_config folder
   cp -R /usr/share/tomcat7/.motech .
   #Remove the mds_entities.jar bundle (this is created each time MOTECH is run by MDS)
   rm /usr/share/tomcat7/.motech/bundles/mds-entities.jar
   #Perform the SQL Dump
   #MySQL Commands
      mysqldump -u {ENTER YOUR MYSQL USERNAME HERE} -p motechdata > motechdata.sql
      mysqldump -u {ENTER YOUR MYSQL USERNAME HERE} -p motechquartz > motechquartz.sql
      mysqldump -u {ENTER YOUR MYSQL USERNAME HERE} -p motechschema > motechschema.sql
   #PostgreSQL Commands (note that this assumes the present user has access)
      sudo -u postgres pg_dump motechdata > motechdata.sql
      sudo -u postgres pg_dump motechquartz > motechquartz.sql
      sudo -u postgres pg_dump motechschema > motechschema.sql

Now, we have copy of the default .motech directory and a copy of all three databases. Proceed to the next part to change the configuration files and setup the databases.

Part 3
------
Now it's time to change the configuration files, upload the SQL databases and start MOTECH with the new database names. The first step is to copy the default configuration .motech folder to the appropriate location /usr/share/tomcat7/.motech. This could be in a docker container or in another user.

Here's a basic structure of the .motech/config folder:

- *bootstrap.properties* - This file contains all of the bootstrap properties
- *datanucleus_data.properties* - **need to change** - This file contains the database settings for the 'data' database
- *datanucleus_schema.properties* - **need to change** - This file contains the database settings for the 'schema' database
- *flyway_data.properties* - This file contains flyway migration settings for the 'data' database
- *flyway_schema.properties* - This file contains flyway migration settings for the 'schema' database
- *log4j.properties* - This file contains logger settings
- *motech-settings.properties* - You created this file and it contains MOTECH system settings
- *org.motechproject.motech-platform-email/* - This directory contains email settings
- *org.motechproject.motech-platform-web-security/* - This directory contains web-security settings
- *org.motechproject.motech-scheduler/* - **need to change** - This directory contains settings for the quartz scheduler

As you can see, we need to change three files. As we change these files, we're going to change the word motech to 'node1' in each database for easy identification. So, motechdata will become node1data, motechquartz will become node1quartz and motechschema will become node1schema.

File 1: datanucleus_data.properties
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
.. code-block:: bash

   #Open the file
   nano /usr/share/tomcat7/.motech/config/datanucleus_data.properties
   #Look for the following line:
   #javax.jdo.option.ConnectionURL=${sql.url}motechdata
   #Change the 'motechdata' to 'node1data' and save the file

File 2: datanucleus_schema.properties
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
.. code-block:: bash

   #Open the file
   nano /usr/share/tomcat7/.motech/config/datanucleus_schema.properties
   #Look for the following line:
   #javax.jdo.option.ConnectionURL=${sql.url}motechschema
   #Change the 'motechschema' to 'node1schema' and save the file

File 3: org.motechproject.motech-scheduler/quartz.properties
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
.. code-block:: bash

   #Open the file
   nano /usr/share/tomcat7/.motech/config/org.motechproject.motech-scheduler/quartz.properties
   #Look for the following line:
   #org.quartz.dataSource.motechDS.URL=${sql.url}motechquartz
   #Change the 'motechquartz' to 'node1quartz' and save the file

Now, we need to create the databases:

.. code-block:: bash

   #MySQL Command
   mysql -u {ENTER YOUR MYSQL USERNAME HERE} -p -e "CREATE DATABASE node1data; CREATE DATABASE node1schema; CREATE DATABASE node1quartz;"
   #Or PostgreSQL Command (assuming the postgres user)
   sudo -u postgres psql --command 'CREATE DATABASE node1data WITH OWNER = postgres;CREATE DATABASE node1schema WITH OWNER = postgres;CREATE DATABASE node1quartz WITH OWNER = postgres;'

Then, import the files into each database:

.. code-block:: bash

   #MySQL Command
   mysql -u {ENTER YOUR MYSQL USERNAME HERE} -p node1data < motechdata.sql
   mysql -u {ENTER YOUR MYSQL USERNAME HERE} -p node1schema < motechschema.sql
   mysql -u {ENTER YOUR MYSQL USERNAME HERE} -p node1quartz < motechquartz.sql
   #Or PostgreSQL Command (assuming the postgres user)
   sudo -u postgres psql node1data < motechdata.sql
   sudo -u postgres psql node1schema < motechschema.sql
   sudo -u postgres psql node1quartz < motechquartz.sql

Finally, we need to start the tomcat7 server and everything should be up and running.

.. code-block:: bash

   sudo service tomcat7 start

At this point, everything should startup successfully pointing to the new databases.
