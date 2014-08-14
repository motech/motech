#!/bin/sh
echo "~~~ Starting tomcat-shutdown.sh ~~~"

echo "~~~ Current working directory ~~~"
echo ${PWD}

echo "~~~ List of running processes ~~~"
ps ux

echo "~~~ List of running tomcat processes ~~~"
ps ux | grep 'target/tomcat' | grep -v grep

echo "~~~ Number of running tomcat processes ~~~"
TOMCAT_PROCESSES_COUNT=$(ps ux | grep 'target/tomcat' | grep -v grep | wc -l)
echo ${TOMCAT_PROCESSES_COUNT}

echo "~~~ WARNING ~~~"
echo "There are $TOMCAT_PROCESSES_COUNT running Tomcat processes"

exit ${TOMCAT_PROCESSES_COUNT}
