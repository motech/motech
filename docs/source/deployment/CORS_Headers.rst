========================================
Add CORS Headers to MOTECH Configuration
========================================

.. contents:: Table of Contents
    :depth: 2

Add CORS support to Apache server
=================================

First step is enable mod headers for Apache. Do it by running the following command:

    a2enmod headers

Configuration CORS, if you want set this globally, add following line into apache server configuration (usually /etc/apache*.conf)
into <directory **PATH**> </directory>

.. code-block:

	Header always set Access-Control-Allow-Origin "*"
	Header always set Access-Control-Allow-Headers **REQUEST HEADERS**
	Header always set Access-Control-Expose-Headers **RESPONSE HEADERS**
	Header always set Access-Control-Allow-Methods **METHODS**

**REQUEST HEADERS** is the list of headers separated by comma that can be used to prepare request. Example:
 "Content-Type,X-Requested-With,accept,authorization,Origin,Access-Control-Request-Method,Access-Control-Request-Headers"

**RESPONSE HEADERS** is the list of headers separated by comma, other than simple response headers that browsers are allowed to access. Example:
 "Access-Control-Allow-Origin,Access-Control-Allow-Credentials"

**METHODS** is the list of methods separated by comma that can be used. Example: "POST, GET, OPTIONS, DELETE, PUT"

If changes are made while the apache was enabled, you should restart apache.

Add CORS support to Tomcat server
=================================

Configuration CORS, if you want set this globally, add following filter into $CATALINA_BASE/conf/web.xml
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

**REQUEST HEADERS** is the list of headers separated by comma that can be used to prepare request. Example:
 <param-value>Content-Type,X-Requested-With,accept,authorization,Origin,Access-Control-Request-Method,Access-Control-Request-Headers</param-value>

**RESPONSE HEADERS** is the list of headers separated by comma, other than simple response headers that browsers are allowed to access. Example:
 <param-value>Access-Control-Allow-Origin,Access-Control-Allow-Credentials</param-value>

If changes are made while the Tomcat was enabled, you should restart Tomcat.

Add CORS support to Jetty server
================================

It seems similar like add CORS to Tomcat server, but you have to copy jetty*.jar from $JETTY_BASE/lib to $JETTY_BASE/webapps/**motechDir**/lib .
Add following filter into $JETTY_BASE/webapps/**motechDir**/WEB-INF/web.xml
..code-block:

    <filter>
        <filter-name>cross-origin</filter-name>
        <filter-class>org.eclipse.jetty.servlets.CrossOriginFilter</filter-class>
        <init-param>
            <param-name>allowedOrigins</param-name>
            <param-value>*</param-value>
        </init-param>
        <init-param>
            <param-name>allowedHeaders</param-name>
            <param-value>*</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>cross-origin</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

allowedHeaders, a comma separated list of HTTP headers that are allowed to be specified when accessing the resources. Default value is X-Requested-With. Example:
 <param-value>Content-Type,X-Requested-With,accept,authorization,Origin,Access-Control-Request-Method,Access-Control-Request-Headers</param-value>

If changes are made while the Jetty was enabled, you should restart Jetty.
