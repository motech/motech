==========================
Frequently Asked Questions
==========================

This page contains frequently asked questions about MOTECH with short answers to them.

.. contents::
    :depth: 2

Can Motech run on Windows?
-----------------------------
All of the components of the MOTECH are avaliable on Windows and it is possible to run the service on it,
but it is not actively supported.

Is MDS using any caching? How does that affect a cluster deployment?
-----------------------------------------------------------------------
MDS is using datanucleus, which by default uses level1 and level2 cache. Sometimes it can cause data inconsistency
across multiple server nodes in cluster deployment. To resolve this issue you can turn off level2 cache by setting
``datanucleus.cache.level2.type`` property to ``none``. There may be slight performance drop per server in result of
turning off level2 cache. Level1 cache should be kept as it works within one transaction, therefore not affecting
cluster deployment.

How to use SSL for database connections in Motech?
--------------------------------------------------
In order to establish secure SSL connection JDBC URL needs to be updated with JDBC driver property ``'useSSL'=true'``
eg. if you are using mySQL at localhost JDBC URL should be ``jdbc:mysql://localhost:3306/?useSSL=true``. As the URL
entered on the MOTECH bootstrap page is only the base for actual database connections urls, you should provide URL with
SSL parameter to appropriate config files.

For MDS it can be set by editing ``~/.motech/config/datanucleus.properties`` and changing the property ``javax.jdo.option.ConnectionURL``.
For quartz you have to edit the variable ``org.quartz.dataSource.motechDS.URL`` in ``quartz.properties``. If your config
source is FILE, then you have to edit the file in scheduler directory in ``~/.motech/config``. If your source is UI, you
have to change the quartz configuration through the manage modules section in the Admin UI.

Motech is not working as expected, how can I check what's wrong?
----------------------------------------------------------------
All the the problems that MOTECH encounters are logged. The logs can be checked in two ways. If MOTECH is working you can
access logs by choosing ``Server Log`` from ``Admin`` panel on the UI. There you can also specify logging level for
specific packages - this is done by selecting wanted log level in ``Server Log -> Log options`` panel.

If in order of error access to the UI is not possible, for standard MOTECH installation on Tomcat logs can be also found
in ``catalina.out`` file located in Tomcat installation logs folder. For example for MOTECH deployed on Tomcat 7.0.62 the
``catalina.out`` is located in ``~/apache-tomcat-7.0.62/logs`` folder. The content of the log file can be displayed by
any text editor or in Ubuntu console by for example ``less catalina.out`` command.

I am getting blueprint timeout error - what does it mean?
---------------------------------------------------------
Blueprint timeout error is encountered when one of the modules has reference to the service from another module defined in
``blueprint.xml`` file and that service has not been made available. By default module waits 5 minutes for required service,
if in this time service won't be delivered the timeout error will be thrown. First step in resolving blueprint timeout
errors should be checking if the problematic service is correctly registered as OSGi service. Below you can se an example
of properly defined OSGi service exporting:

.. code-block:: xml

    <osgi:service ref="subscriptionService"
            interface="org.motechproject.hub.service.SubscriptionService"
            auto-export="interfaces" />

and importing:

.. code-block:: xml

    <osgi:reference id="hubTopicMDSService"
          interface="org.motechproject.hub.mds.service.HubTopicMDSService" />

If service is defined correctly and blueprint timeout error is still encountered it probably means that module which
should deliver the service didn't start and further investigation for origin error cause should be taken at that module.

How to create a web-service with Motech?
----------------------------------------
Creating web-service with MOTECH is done by using Spring Web Services. After adding all required dependencies for
Spring Web Services you should provide contract definition as XSD file and endpoint classes for your exposed services.
For example if you want to expose simple service returning information about book, your XSD file could look like this:

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

Then you should have defined endpoint class for handling getBook element, which could look like this:

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
Build the container with ``--no-cache``.