==========================
Frequently Asked Questions
==========================

This page contains frequently asked questions about MOTECH with short answers to them.

.. contents::
    :depth: 2

Can MOTECH run on Windows?
--------------------------
MOTECH is available and can be deployed on Windows, but Windows is not actively supported as a platform.

Is MDS using any caching? How does that affect a cluster deployment?
--------------------------------------------------------------------
MDS is using the DataNucleus persistence framework, which by default uses level1 and level2 cache. It can cause data inconsistency
across multiple server nodes in cluster deployment. To resolve this issue you can turn off level2 cache by setting
``datanucleus.cache.level2.type`` property to ``none``. There may be slight performance drop per server in result of
turning off level2 cache. Level1 cache should be kept as it works within one transaction, therefore not affecting
cluster deployment.

More information about the DataNucleus can be found on it project page (link_). There is also available caching doc_.

.. _link: http://www.datanucleus.org/
.. _doc: http://www.datanucleus.org/products/accessplatform_4_0/jpa/cache.html

How to use SSL for database connections in MOTECH?
--------------------------------------------------
First generate and install your SSL certificates. After this is done you will need to configure your database to use SSL.
For MySQL you can follow instructions_ from official MySQL page.
Next step is to point JVM variables to your SSL certificates. Look at the Oracle documentation_ for more information about
these variables.

In order to establish a secure SSL connection the JDBC URL needs to be updated with the JDBC driver property ``'useSSL'=true'``
eg. if you are using mySQL at localhost the JDBC URL should be ``jdbc:mysql://localhost:3306/dbname?useSSL=true``. As the URL
entered on the MOTECH bootstrap page is only the base for actual database connections urls, you should provide SSL parameter
to appropriate config files.

For MDS it can be set by editing ``~/.motech/config/datanucleus_data.properties`` and ``~/.motech/config/datanucleus_schema.properties``
files and changing the property ``javax.jdo.option.ConnectionURL``. For quartz you have to edit the variable
``org.quartz.dataSource.motechDS.URL`` in ``quartz.properties``. If your config source is FILE, then you have to edit the
file in scheduler directory in ``~/.motech/config``. If your source is UI, you have to change the quartz configuration
through the manage modules section in the Admin UI.

.. _instructions: http://dev.mysql.com/doc/refman/5.6/en/using-ssl-connections.html
.. _documentation: https://docs.oracle.com/cd/E29585_01/PlatformServices.61x/security/src/csec_ssl_jsp_start_server.html

MOTECH is not working as expected, how can I check what's wrong?
----------------------------------------------------------------
All the problems that MOTECH encounters are logged. The logs can be checked in two ways. If MOTECH is working you can
access logs by choosing ``Server Log`` from the ``Admin`` panel on the UI. There you can also specify logging levels for
specific packages - this is done by selecting the desired log level in ``Server Log -> Log options`` panel.

If access to UI is not possible, for standard MOTECH installations on Tomcat, logs can be also found in ``catalina.out``
file located in the logs folder in the Tomcat installation. For example for MOTECH deployed on Tomcat 7.0.62 the ``catalina.out``
is located in ``~/apache-tomcat-7.0.62/logs`` folder. The content of the log file can be displayed by any text editor.

I am getting blueprint timeout error - what does it mean?
---------------------------------------------------------
Blueprint timeout errors are encountered when one of the modules has a reference to a service from another module defined in
``blueprint.xml`` file and that service has not been made available. By default blueprint extender waits 5 minutes for a required service,
if in this time service won't be delivered the timeout error will be thrown. First step in resolving blueprint timeout
errors should be checking if the problematic service is correctly registered as an OSGi service. Below you can see an example
of a properly defined OSGi service:

.. code-block:: xml

    <osgi:service ref="subscriptionService"
            interface="org.motechproject.hub.service.SubscriptionService"
            auto-export="interfaces" />

and importing it:

.. code-block:: xml

    <osgi:reference id="hubTopicMDSService"
          interface="org.motechproject.hub.mds.service.HubTopicMDSService" />

If the service is defined correctly and a blueprint timeout error is still encountered, it probably means that the module which
should deliver the service didn't start and further investigation for origin of the error should conducted. The real
error will very often be printed in the logs above the timeout error.

I am getting IllegalArgumentException (object is not an instance of declaring class) - what does it mean?
---------------------------------------------------------------------------------------------------------
The cause is probably that you have different versions of bundles running at the same time. To solve this issue clean up your
bundles directory and WAR file. For standard MOTECH installations on Tomcat the bundles directory is ``~/.motech/bundles``
and deployed WAR file can be found in ``~/apache-tomcat-<version>/webapps``.

How to create a web-service with MOTECH?
----------------------------------------
Creating a web-service with MOTECH can be done using Spring Web Services framework, which is the suggested approach.
First add all required dependencies. The most important dependencies that you need to use are:

.. code-block:: xml

    <dependency>
        <groupId>org.springframework.ws</groupId>
        <artifactId>spring-ws-core</artifactId>
        <version>2.0.4.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>jaxen</groupId>
        <artifactId>jaxen</artifactId>
        <version>1.1.6</version>
    </dependency>
    <dependency>
        <groupId>org.apache.servicemix.bundles</groupId>
        <artifactId>org.apache.servicemix.bundles.saaj-impl</artifactId>
        <version>1.3.9_2</version>
    </dependency>

After adding all required dependencies for Spring Web Services you should provide a contract definition as XSD file
and endpoint classes for your exposed services.
For example if you want to expose a simple service returning information about books, your XSD file could look like this:

.. code-block:: xml

    <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified" targetNamespace="urn:books">
        <xs:element name="getBook">
            <xs:complexType>
                <xs:sequence>
                    <xs:element name="author" type="xs:string"/>
                    <xs:element name="year" type="xs:int"/>
                </xs:sequence>
            </xs:complexType>
        </xs:element>
    </xs:schema>

Then you should define an endpoint class for handling the getBook element, which could look like this:

.. code-block:: java

    @Endpoint
    public class BookEndpoint {
        private BookService bookservice;

        //constructors

        @PayloadRoot(namespace = "urn:books", localPart = "getBook")
        @ResponsePayload
        public GetBookResponse getBook(@RequestPayload GetBookRequest request) {
            GetBookResponse response = new GetBookResponse();
            response.setBook(bookservice.getBook());
            return response;
        }
    }

More details about Spring Web Services can be found at it project page_.

.. _page: http://projects.spring.io/spring-ws/

I'm unable to build docker containers because of missing packages in Ubuntu repositories.
-----------------------------------------------------------------------------------------
Try building the container with ``--no-cache``.

How to debug the T7 plugin instance of Tomcat during integration tests? (in platform/server for example)
--------------------------------------------------------------------------------------------------------

First export the *CATALINA_OPTS* variable with a value that will enable debugging, for example:

.. code-block:: bash

    export CATALINA_OPTS=-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n

Next, run the integration tests as you would normally do, for example:

.. code-block:: bash

    mvn clean install -PIT

Then connect to Tomcat using a remote debugger on port 8000, same as when normally debugging Tomcat.

Why am I not seeing anything in "Admin Queues and Topics"?
----------------------------------------------------------

1. Edit ActiveMQ broker configuration, which is in the file ``/etc/activemq/instances-enabled/main/activemq.xml``.
Change or add the following lines in the broker tag;

.. code-block:: xml

    <broker useJmx="true">

The next step is to restart ActiveMQ.
To restart ActiveMQ use

    .. code-block:: bash

        sudo service activemq restart

If the issue still appears, restart MOTECH (Tomcat).

2. The most likely cause is a RMI connection error. By default RMI is using a random port.
To set fixed port you have to edit ActiveMQ broker configuration, which is in the file ``/etc/activemq/instances-enabled/main/activemq.xml``.
Add the following lines to the broker configuration (the name of the broker in use is in ``Admin/Settings/JMX/Broker name``):

.. code-block:: xml

	<managementContext>
	    <managementContext createConnector="true" connectorPort="1099" rmiServerPort="1099" />
	</managementContext>

The XML elements inside the ``<broker>`` element must be ordered alphabetically.
The next step is to restart ActiveMQ.
To restart ActiveMQ use:

.. code-block:: bash

    sudo /etc/init.d/activemq restart

If the issue still appears, restart MOTECH (Tomcat).

