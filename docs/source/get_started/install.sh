#!/bin/bash
# This script installs the latest released version of MOTECH Platform Server to a fresh Ubuntu 14.04 installation
# Note that these steps are for v. 0.26 moving forward. Previous versions of MOTECH Platform Server required additional installation steps
# To run this script, download it to the server or copy and paste it in to a text file (with nano vim)
# Type chmod +x install.sh
# Then sudo ./install.sh
# Note that it will prompt you for MySQL root password

sudo apt-get update
sudo apt-get install -y tomcat7
sudo service tomcat7 stop
sudo apt-get install -y activemq mysql-server
sudo chown -R tomcat7:tomcat7 /var/lib/tomcat7/ /usr/share/tomcat7/
sudo ln -s /etc/activemq/instances-available/main /etc/activemq/instances-enabled/main
sudo sed -e 's/<broker /<broker schedulerSupport="true" /' -i /etc/activemq/instances-enabled/main/activemq.xml

#Then start ActiveMQ
sudo service activemq restart
sudo curl -L http://nexus.motechproject.org/service/local/artifact/maven/redirect?r=releases\&g=org.motechproject\&a=motech-platform-server\&v=RELEASE\&e=war -o motech-platform-server.war
sudo cp motech-platform-server.war /var/lib/tomcat7/webapps/
sudo service tomcat7 start
echo "Setup Complete! Navigate to http://localhost:8080/motech-platform-server to configure the bootstrap settings."