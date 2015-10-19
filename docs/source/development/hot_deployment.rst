========================================
Static resources - faster UI development
========================================

This short manual explains how to enable hot deployment of static files (.js,.css files etc). This will help to view
the changes made to the static files without having to redeploy the module/bundle each time a change is made. After
following the described steps, static files will be read from specified location on your local machine, rather than from
bundle resources.


Details
=======

You need to do two things:

- Set environment as development
- For the bundle for which static resources are to be hot deployed, create an environment variable with
  the "bundle symbolic name" but after replacing all special characters and spaces with underscore; this variable
  should contain a path to the module resources directory

If you add a new javascript file, you need to deploy the bundle with the new file at least once to register it.
Otherwise the script will not be registered on the UI.

.. note::

    By default, the bundle symbolic name is constructed, based on the groupId and artifactId, in the following
    way: **{groupId}.{artifactId}**.


Example
=======

Let's assume the following properties of a module:

+-------------------------+-----------------------------------------------------+
|groupId:                 |org.motechproject                                    |
+-------------------------+-----------------------------------------------------+
|artifactId:              |sms                                                  |
+-------------------------+-----------------------------------------------------+
|bundle symbolic name:    |org.motechproject.sms                                |
+-------------------------+-----------------------------------------------------+
|path to resources:       |/home/me/modules/sms/src/main/resources              |
+-------------------------+-----------------------------------------------------+

Set up two environment variables (from the same shell which starts up tomcat). Remember to replace all spaces and
special characters with an underscore. Note, that the configuration is case-sensitive.

.. code-block:: bash

    export ENVIRONMENT=DEVELOPMENT
    export org_motechproject_sms=/home/me/modules/sms/src/main/resources

In case you find the changes are not being reflected even after correctly setting up the environment variables, clear
browser cache and delete the directory ${tomcat_installation_dir}/work/Catalina/localhost/${motech_dir}/


Hot deployment with Docker container
====================================

It is also possible to configure hot deployment of static files running MOTECH with the Docker container. To do
so, you must make some edits in the **fig.yml** file.

First of all, you must link your local directory, containing module resources to a volume visible in the Docker container.
This can be achieved by adding an appropriate entry in the volumes section of the tomcat configuration. The entry must be in the
form of "yourLocalDirectory: virtualDirectoryOnDocker". This is how exposing your local "/home/you/modules/sms/src/main/resources"
directory could look like:

.. code-block:: yaml

    volumes:
        - /home/you/modules/sms/src/main/resources:/home/modules/sms/resources

Adding this entry will cause that your local directory will be visible in the Docker container, under the virtual path
"home/modules/sms/resources".

You must also set up the environment variables in your configuration. In your file find tomcat section and then "environment".
Add required entries. The names and values of the environment variables must follow the rules stated above. Note, however,
that Docker will only see resources that you have manually exposed as volumes (virtual directories within Docker).
It might look like this:

.. code-block:: yaml

    environment:
        JAVA_OPTS: -Xms1024m -Xmx2048m -XX:MaxPermSize=1024m
        DB_TYPE: mysql
        DB_USER: root
        DB_PASSWORD: password
        DB_DRIVER: com.mysql.jdbc.Driver
        ENVIRONMENT: DEVELOPMENT
        org_motechproject_sms: home/modules/sms/resources

The variables must be added in the "variableName: variableValue" format. Set environment as development and add paths to
the resource directories, for the modules you wish to have hot deployment for.

The complete configuration for the tomcat section in the **fig.yml** could look like this:

.. code-block:: yaml

    tomcat:
        image: motech/tomcat:7.0.53
        ports:
            - "8080:8080"
            - "8000:8000"
        links:
            - couchdb
            - db
            - activemq
        environment:
            JAVA_OPTS: -Xms1024m -Xmx2048m -XX:MaxPermSize=1024m
            DB_TYPE: mysql
            DB_USER: root
            DB_PASSWORD: password
            DB_DRIVER: com.mysql.jdbc.Driver
            ENVIRONMENT: DEVELOPMENT
            org_motechproject_sms: home/modules/sms/resources
        volumes:
            - /home/you/docker-motech-config:/root/.motech/config
            - /home/you/docker-motech-bundles:/root/.motech/bundles
            - /home/you/modules/sms/src/main/resources:/home/modules/sms/resources


