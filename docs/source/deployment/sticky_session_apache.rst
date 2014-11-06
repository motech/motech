=======================================================
Load Balancing with Apache 2 WebServer - Sticky Session
=======================================================

.. contents:: Table of Contents
   :depth: 2

Overview
========

Sticky Session is a method used with Load Balancing, to achieve server affinity. In other words, it assigns a particular client with a particular server instance behind Load Balancer, so that HTTP session doesn't get lost across application instances. It is essential if we are deploying Motech in a cluster configuration and we want to be able to access its UI. This tutorial describes two methods of setting up Sticky Session feature with Apache Server used for Load Balancing.

Method 1: Using existing session cookie
=======================================

.. attention::

    This method requires Apache Tomcat to be used as instance server.

In this approach you will have to configure both Load Balance Server (LBS) and all Tomcat Instance Servers (IS). For session tracking, we will use existing session cookie - JSESSIONID - and let IS to modify it a bit.

In LBS VirtualHost configuration, you have to provide names for your instances (If you haven't done so yet). To do so, for each BalancerMembers you have to specify a route parameter:

.. code-block:: xml

    BalancerMember http://{i'th instance ip:port} route=tomcat_instance_i

where tomcat_instance_i is the i'th instance unique name.

In the same file you have to provide session ID cookie name to LBS under its VirtualHost Proxy definition:

.. code-block:: xml

    <Proxy balancer://mycluster>
        (...)
        ProxySet stickysession=JSESSIONID
        (...)
    </Proxy>

Finally, for each tomcat, you have to edit $CATALINA_HOME/conf/server.xml. In the <Engine> node add jvmRoute attribute which corresponds with BalancerMember route of this particular IS from LBS configuration:


.. code-block:: xml

    (...)
    <Engine name="Catalina" defaultHost="localhost" jvmRoute="tomcat_instance_i">
    (...)

After restarting all Instance Servers and Load Balance Server, sticky session should work.

Method 2: Using additional session cookie
=========================================

This approach requires changes only to Load Balance Server (LBS) VirtualHost configuration. For session tracking, a brand new cookie named ROUTEID will be used. It will be managed by LBS, so we need to enable its ability to modify headers (thus cookies). The advantage of this method is that it doesn't require any Instance Server (IS) configuration, so it potentially can be used with any backend web servers.

Same as in previous method, you have to name your instances:

.. code-block:: xml

    BalancerMember http://{i'th instance ip:port} route=tomcat_instance_i

Then, ensure that 'headers' Apache module is enabled. To enable this module in Ubuntu, you can use following command:

.. code-block:: bash

    sudo a2enmod headers

Next, you have to tell LBS to store its own cookie (ROUTEID) on client side. That cookie will contain instance name (route):

.. code-block:: xml

    <VirtualHost *:80>
        (...)
        Header add Set-Cookie "ROUTEID=.%{BALANCER_WORKER_ROUTE}e; path=/" env=BALANCER_ROUTE_CHANGED
        (...)
    </VirtualHost>

Finally, tell LBS the cookie name:

.. code-block:: xml

    <Proxy balancer://mycluster>
        (...)
        ProxySet stickysession=ROUTEID
        (...)
    </Proxy>

After restarting Load Balance Server, sticky session should work.