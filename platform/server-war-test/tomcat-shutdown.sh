#!/bin/sh

TOMCAT_PROCESSES_COUNT=$(ps ux | grep 'target/tomcat' | grep -v grep | wc -l)

if [ "$TOMCAT_PROCESSES_COUNT" -gt "0" ]
then
    echo "~~~ WARNING ~~~"
    echo "There are $TOMCAT_PROCESSES_COUNT running Tomcat processes"
    echo "~~~ List of running tomcat processes ~~~"
    echo `ps ux | grep 'target/tomcat' | grep -v grep`
fi

exit ${TOMCAT_PROCESSES_COUNT}
