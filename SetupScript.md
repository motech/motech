This is still a work in progress.
Work still needs to be done on permissions and mysql config

```
#!/bin/sh


#
#
# This script is designed for Ubuntu 12.04-LTS and installs:
# Java 7, ActiveMQ, Tomcat 7, CouchDB, CouchDB-Lucene
#
# Authors: rlarubbio, fhuster
#
#


set -e



echo "***** MOTECH Machine Preparation"


echo "***** curl"
apt-get -y -qq install curl


# installing python-software-properties is how you install add-apt-repository on 12.04
# TODO install software-properties-common for 13.10 ?
echo "***** Java 7"
apt-get -y -qq install python-software-properties 1>/dev/null
add-apt-repository --yes ppa:webupd8team/java 1>/dev/null
apt-get -y -qq update
# Set some vars so the oracle installer doesn't ask us to accept the license
echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections
apt-get -y -qq install oracle-java7-installer
#echo "
#export PATH=\"\$HOME/jdk1.7.0_21/bin:\$PATH\"
#export JAVA_HOME=\$HOME/jdk1.7.0_21
#export MAVEN_OPTS=\"-Xmx512m -XX:MaxPermSize=128m\"
#" >> ~/.profile


echo "***** ActiveMQ"
if [ ! -h /etc/activemq/instances-enabled/main ]; then
	apt-get -y -qq install activemq 1>/dev/null
	ln -s /etc/activemq/instances-available/main /etc/activemq/instances-enabled/main
fi


echo "***** Tomcat 7"
if [ ! -f /etc/init.d/tomcat7 ]; then
	cd /opt
	wget -q http://apache.cs.utah.edu/tomcat/tomcat-7/v7.0.42/bin/apache-tomcat-7.0.42.tar.gz
	tar xzf apache-tomcat-7.0.42.tar.gz
	sed -i 's/<\/tomcat-users>/  <user username=\"motech\" password=\"motech\" roles=\"manager-gui\"\/>\n&/' /opt/apache-tomcat-7.0.42/conf/tomcat-users.xml
	printf "export CATALINA_HOME=/opt/tomcat7" >> ~/.profile
	ln -s /opt/apache-tomcat-7.0.42 /opt/tomcat7
	echo "case \$1 in
start)
sh /opt/tomcat7/bin/startup.sh
;;
stop) 
sh /opt/tomcat7/bin/shutdown.sh
;;
restart)
sh /opt/tomcat7/bin/shutdown.sh
sh /opt/tomcat7/bin/startup.sh
;;
esac 
exit 0
" > /etc/init.d/tomcat7
	chmod 755 /etc/init.d/tomcat7
	update-rc.d tomcat7 defaults 1>/dev/null
	service tomcat7 start
fi


echo "***** make"
apt-get -y -qq install make


echo "***** CouchDB"
if [ ! -h /etc/init.d/couchdb ]; then
	apt-get -y -qq install g++ erlang-dev erlang-manpages erlang-base-hipe erlang-eunit erlang-nox erlang-xmerl erlang-inets libmozjs185-dev libicu-dev libcurl4-gnutls-dev libtool wget
	cd /tmp
	wget -q http://www.bizdirusa.com/mirrors/apache/couchdb/source/1.3.1/apache-couchdb-1.3.1.tar.gz
	tar xzf apache-couchdb-1.3.1.tar.gz
	cd apache-couchdb-1.3.1
	./configure -q 1>/dev/null
	make --silent 1>/dev/null
	make --silent install  1>/dev/null
	useradd couchdb
    	chown -R couchdb:couchdb /usr/local/var/log/couchdb
    	chown -R couchdb:couchdb /usr/local/var/lib/couchdb
    	chown -R couchdb:couchdb /usr/local/var/run/couchdb
	ln -s /usr/local/etc/init.d/couchdb /etc/init.d
	update-rc.d couchdb defaults 1>/dev/null
	echo "[httpd]
port = 5984
bind_address = 0.0.0.0
[httpd_global_handlers]
_fti = {couch_httpd_proxy, handle_proxy_req, <<\"http://127.0.0.1:5985\">>}
" > /usr/local/etc/couchdb/local.d/motech.ini
fi


echo "***** maven"
apt-get -y -qq install maven


echo "***** CouchDB-Lucene"
if [ ! -h /etc/init.d/couchdb-lucene ]; then
	cd /tmp
	wget -q https://github.com/rnewson/couchdb-lucene/archive/v0.9.0.tar.gz
	tar xzf v0.9.0.tar.gz
	cd couchdb-lucene-0.9.0
	mvn -q 1>/dev/null
	cd target
	tar -xzf couchdb-lucene-0.9.0-dist.tar.gz
	mv /tmp/couchdb-lucene-0.9.0/target/couchdb-lucene-0.9.0 /usr/local
	sed -i "s/lucene-0.8.0/lucene-0.9.0/" /usr/local/couchdb-lucene-0.9.0/tools/etc/init.d/couchdb-lucene/couchdb-lucene
	ln -s /usr/local/couchdb-lucene-0.9.0/tools/etc/init.d/couchdb-lucene/couchdb-lucene /etc/init.d/couchdb-lucene
	update-rc.d couchdb-lucene defaults 1>/dev/null
fi
```