========================================
Add CORS headers to server configuration
========================================

.. contents:: Table of Contents
    :depth: 3

Overview
========

If the MOTECH-UI is hosted on different a domain than MOTECH-CORE, we have to share resources between different domains.
XMLHttpRequest requests have traditionally been limited to accessing the same domain as the parent web page (as per the same-origin security policy).
Cross-site requests are forbidden by default because of their ability to perform advanced requests that introduce many cross-site scripting security issues.
Cross-Origin Resource Sharing (CORS) can define a way in which MOTECH-UI and MOTECH-CORE interact to determine safely whether or not to allow the cross-origin request.

It gives possibilities to specify which domains will have access to resources. This documentation page explains how MOTECH-CORE can configure its headers to support CORS.

Cross-site requests
===================

If we want to share resources, the MOTECH-CORE(server) must enable CORS.
However sending a cross-site request does not require setting any cross-origing sharing request headers programmatically.
CORS headers are set for you when making invocations to the server. Here you can find more information about it `the HTTP request headers <https://developer.mozilla.org/en-US/docs/Web/HTTP/Access_control_CORS#The_HTTP_request_headers>`_.

Enable CORS in MOTECH-CORE
==========================

By default when a cross-site request is sent to MOTECH-CORE, the following error occurs :

.. code-block::

	Origin http://example.domain.com is not allowed by Access-Control-Allow-Origin.

To enable CORS we have to change configuration in the web server on which MOTECH-CORE is hosted. We can add the following CORS headers :

Access-Control-Allow-Origin
----------------------------

In this header we can specify which domains have access to resources. We can allow access from any origin using "*". But if you’d like finer control over who can access your data, use an actual value in the header. Examples:

.. code-block::

   Access-Control-Allow-Origin: "*" (allow access from any origin)
   Access-Control-Allow-Origin: "http://motech-ui.example" (allow access from only "http://motech-ui.example" origin)
   Access-Control-Allow-Origin: "http://motech-ui.example | http://other.domain" (allow access from two mentioned origins)

Access-Control-Allow-Methods
-----------------------------

Comma-delimited list of the supported HTTP methods (`GET`, `POST`, `PUT`, `DELETE`, `HEAD`,  `OPTIONS`). Examples:

.. code-block::

   Access-Control-Allow-Methods: "GET" (only GET method is allowed in request)
   Access-Control-Allow-Methods: "POST, GET" (POST and GET are allowed in request)

Access-Control-Allow-Headers
----------------------------

This header specifies which complex HTTP headers can be used in a request to MOTECH-CORE. Example :

.. code-block::

   Access-Control-Allow-Headers: "Content-Type,X-Requested-With,Accept,Authorization,Origin,Access-Control-Request-Method,Access-Control-Request-Headers"

Access-Control-Expose-Headers
-----------------------------

During a CORS request, client(MOTECH-UI) can only access simple response headers. Simple response headers are defined as follows:
    - Cache-Control
    - Content-Language
    - Content-Type
    - Expires
    - Last-Modified
    - Pragma

If you want clients to be able to access other headers, you have to use the Access-Control-Expose-Headers header. The value of this header is a comma-delimited list of response headers you want to expose to the client. Example :

.. code-block::

   Access-Control-Expose-Headers: "Access-Control-Allow-Origin,Access-Control-Allow-Credentials" (client has access to values of mentioned headers)

Apache Web Server Config
========================

Apache Web Server includes support for CORS. To enable CORS support we have to running the following command which enable necessary for CORS mod headers :

	a2enmod headers

Now we can add the CORS headers into server configuration (usually /etc/apache*/apache*.conf). Inside a tag '<LocationMatch "/path">', we set up CORS headers which we want to add into server configuration.

Here is a config example "allow-all-origins":

.. code-block::

    <LocationMatch "/motech-platform-server">
        Header always set Access-Control-Allow-Origin "*"
    </LocationMatch>

Here is a different config example (let's assume that http://motech-ui.example is an url to MOTECH-UI application):

.. code-block::

    <LocationMatch "/motech-platform-server">
        Header always set Access-Control-Allow-Origin "http://motech-ui.example"
        Header always set Access-Control-Allow-Methods "GET,POST,PUT,DELETE,HEAD,OPTIONS"
        Header always set Access-Control-Allow-Headers "Content-Type,X-Requested-With,Accept,Authorization,Origin,Access-Control-Request-Method,Access-Control-Request-Headers"
        Header always set Access-Control-Expose-Headers "Access-Control-Allow-Origin,Access-Control-Allow-Credentials"
    </LocationMatch>

If changes are made while the Apache server was running, you should restart Apache.

Tomcat Web Server Config
========================

If you use Tomcat from Apache and you set the configuration on the Apache Web Server, you may skip this part of configuration.

Tomcat includes support for CORS (starting from Tomcat version 7.0.41). To enable CORS support we have to use CORS Filter.

If you want to enable CORS for all webapps, add the filter into **$CATALINA_BASE/conf/web.xml**.

If you want to enable them only for the MOTECH application, add the filter into **$CATALINA_BASE/webapps/motech-platform-server/WEB-INF/web.xml**.

If you have not configured Tomcat for multiple instances by setting a CATALINA_BASE directory, then $CATALINA_BASE will be set to the value of $CATALINA_HOME, the directory into which you have installed Tomcat.

The minimal configuration required to use this filter is:

.. code-block::

    <filter>
        <filter-name>CorsFilter</filter-name>
        <filter-class>org.apache.catalina.filters.CorsFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>CorsFilter</filter-name>
        <url-pattern> /* </url-pattern>
    </filter-mapping>

By default CORS headers will be set like the following :

.. code-block::

 Access-Control-Allow-Origin: * (any domain)
 Access-Control-Allow-Methods: GET, POST, HEAD, OPTIONS
 Access-Control-Allow-Headers: Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers
 Access-Control-Expose-Headers:  (Non-simple headers are not exposed by default)

Here is a different config example (let's assume that http://motech-ui.example is an url to MOTECH-UI application):

.. code-block::

    <filter>
        <filter-name>CorsFilter</filter-name>
        <filter-class>org.apache.catalina.filters.CorsFilter</filter-class>
        <init-param>
            <param-name>cors.allowed.origins</param-name>
            <param-value>http://motech-ui.example</param-value>
        </init-param>
        <init-param>
            <param-name>cors.allowed.methods</param-name>
            <param-value>GET,POST,PUT,DELETE,HEAD,OPTIONS</param-value>
        </init-param>
        <init-param>
            <param-name>cors.allowed.headers</param-name>
            <param-value>Content-Type,X-Requested-With,Accept,Authorization,Origin,Access-Control-Request-Method,Access-Control-Request-Headers</param-value>
        </init-param>
        <init-param>
            <param-name>cors.exposed.headers</param-name>
            <param-value>Access-Control-Allow-Origin,Access-Control-Allow-Credentials</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>CorsFilter</filter-name>
        <url-pattern> /* </url-pattern>
    </filter-mapping>

If changes are made while the Tomcat server was running, you should restart Tomcat.