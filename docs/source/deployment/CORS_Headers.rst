========================================
Add CORS Headers to server configuration
========================================

.. contents:: Table of Contents
    :depth: 2

Overview
========

Cross-origin resource sharing (CORS) is the mechanism for resource sharing between servers located in different domains.
CORS Headers are new HTTP access control headers that allows to indicate which servers have permission for communicate.
That Headers improves our security, because if we set them correctly, then we don't give access to our server for unknown domain.

Add CORS support to the Apache server
=====================================

First step is to enable mod headers for Apache. Do it by running the following command:

    a2enmod headers

Add the following lines into Apache server configuration (usually /etc/apache*/apache*.conf), into <directory **PATH**> </directory>, in this way:

.. code-block:
    <directory **PATH**> </directory>
        Header always set Access-Control-Allow-Origin "*"
        Header always set Access-Control-Allow-Headers **REQUEST HEADERS**
        Header always set Access-Control-Expose-Headers **RESPONSE HEADERS**
        Header always set Access-Control-Allow-Methods **METHODS**
    </directory>

**REQUEST HEADERS** is the list of headers separated by commas that can be used to prepare the request:
 "Content-Type,X-Requested-With,accept,authorization,Origin,Access-Control-Request-Method,Access-Control-Request-Headers"

**RESPONSE HEADERS** is the list of headers separated by commas, other than simple response headers that browsers are allowed to access:
 "Access-Control-Allow-Origin,Access-Control-Allow-Credentials"

**METHODS** is the list of methods separated by commas that can be used: "POST, GET, OPTIONS, DELETE, PUT"

If changes are made while the Apache server was running, you should restart apache.

Add CORS support to the Tomcat server
=====================================

Configuration CORS, if you want to set this globally, add the following filter into $CATALINA_BASE/conf/web.xml or if you want to set this only in motech it is $CATALINA_BASE/webapps/motech-platform-server/WEB-INF/web.xml.
$CATALINA_BASE is the main tomcat folder.

..code-block:

    <filter>
        <filter-name>CorsFilter</filter-name>
        <filter-class>org.apache.catalina.filters.CorsFilter</filter-class>
        <init-param>
            <param-name>cors.allowed.headers</param-name>
            <param-value>**REQUEST HEADERS**</param-value>
        </init-param>
        <init-param>
            <param-name>cors.exposed.headers</param-name>
            <param-value>**RESPONSE HEADERS**</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>CorsFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

**REQUEST HEADERS** is the list of headers separated by commas that can be used to prepare the request:
 <param-value>Content-Type,X-Requested-With,accept,authorization,Origin,Access-Control-Request-Method,Access-Control-Request-Headers</param-value>

**RESPONSE HEADERS** is the list of headers separated by commas, other than simple response headers that browsers are allowed to access:
 <param-value>Access-Control-Allow-Origin,Access-Control-Allow-Credentials</param-value>

If changes are made while the Tomcat server was running, you should restart Tomcat.