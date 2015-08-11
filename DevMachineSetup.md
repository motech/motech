# Dependent Software #

  * jdk 7
  * maven
  * mysql
  * activemq
  * couchdb
  * couchdb-lucene

You can find instructions for installing most of these packages on a Debian-based machine on our [Installing MOTECH](http://motechsuite.org/index.php/installing-motech) page.

# Configuration #

## Maven Config ##
set MAVEN\_OPTS="-Xmx512m -XX:MaxPermSize=128m" Add more ram if needed (-Xmx1024m for example, if you hit maven issues)

## MySQL ##
Set MySQL Password : root / password

e.g.

mysql -uroot

UPDATE mysql.user SET password=password('password') WHERE user='root'

## ActiveMQ Scheduler ##
enable scheduler support to true

## CouchDB Lucene Configuration ##
After successful installation of couchdb-lucene open your /usr/local/etc/couchdb/local.ini (default location for local.ini file) under the header  [httpd`_`global`_`handlers]

Add this line

`_`fti = {couch\_httpd\_proxy, handle\_proxy\_req, <<"http://127.0.0.1:5985">>}

## Java Configuration ##
JAVA\_HOME is set to /Library/Java/JavaVirtualMachines/jdk1.7.0\_21.jdk/Contents/Home (or whatever the path of your java is)