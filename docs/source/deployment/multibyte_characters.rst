======================================
Using MOTECH with multibyte characters
======================================

.. contents:: Table of Contents
   :depth: 2

Overview
========

In most of Tomcat, MySQL and PostgreSQL versions by default **ISO-8859-1** character encoding is used.
If you want to use multibyte characters like for example polish or chinese letters this tutorial will help you.

MySQL multibyte characters support
==================================

If you want to add support for a new character set that includes multi-byte characters, you need to
add the following lines into **my.cnf** file::

    [client]
    default-character-set = utf8

    [mysqld]
    collation-server = utf8_unicode_ci
    init-connect='SET NAMES utf8'
    character-set-server = utf8

After settings this, restart the MySQL server.

PostgreSQL multibyte characters support
=======================================

To create a new database with UTF-8 encoding you have to type the following line in terminal::

    postgres=# create database motech_data_services with encoding='UTF-8' lc_collate='en_US.utf8' lc_ctype='en_US.utf8';

However you can encounter the following error::

    ERROR: new encoding (UTF8) is incompatible with the encoding of the template database (SQL_ASCII)

It means that you have to change template's encoding to UTF-8. You can do this like so::

    postgres=# update pg_database set datallowconn = TRUE where datname = 'template0';
    postgres=# \c template0.
    template0=# update pg_database set datistemplate = FALSE where datname = 'template1';
    template0=# drop database template1;
    template0=# create database template1 with template = template0 encoding = 'UTF8';
    template0=# update pg_database set datistemplate = TRUE where datname = 'template1';
    template0=# \c template1
    template1=# update pg_database set datallowconn = FALSE where datname = 'template0';

After setting this, you can once again type the database creation command.

Tomcat multibyte characters support
===================================

To support the feature you have to configure the connector in Tomcat's **server.xml** like so :

.. code-block:: xml

    <Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443"
               URIEncoding="UTF-8"
               useBodyEncodingForURI="true"/>

Now if you want to use multibyte characters in a request url properly you can add manually the following header
to the request headers::

    Content-Type : application/x-www-form-urlencoded; charset=UTF-8

However you can set this globally by adding the following filter in Tomcat's **web.xml**,
otherwise your controller's request mapping will not work correctly :

.. code-block:: xml

    <filter>
        <filter-name>encodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
        <init-param>
            <param-name>forceEncoding</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>encodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
