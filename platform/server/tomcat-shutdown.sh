#!/bin/sh

TOMCAT_PROCESS_DIR=`pwd`/target/`ls target/ | grep tomcat`
TOMCAT_PROCESSES_COUNT=$(ps ux | grep 'target/tomcat' | grep -v grep | wc -l)
T7_TOMCAT_INSTANCES=$(ps ux | grep ${TOMCAT_PROCESS_DIR} | grep -v grep | wc -l)

if [ "$TOMCAT_PROCESSES_COUNT" -gt "0" ]
then
    echo ""
    echo "~~~ WARNING ~~~"
    echo "There are $TOMCAT_PROCESSES_COUNT running Tomcat processes"
    echo ""
    echo "~~~ Working directory ~~~"
    echo `pwd`
    echo ""
    echo "~~~ List of running tomcat processes ~~~"
    echo `ps ux | grep 'target/tomcat' | grep -v grep`

    if [ "$T7_TOMCAT_INSTANCES" -gt "0" ]
    then

        TOMCAT_PID=$(ps aux | grep ${TOMCAT_PROCESS_DIR} | grep -v grep | awk '{print $2}')
        echo ""
        echo "Tomcat started by t7 plugin didn't stop."
        echo "Tomcat PID is $TOMCAT_PID"
        echo "Killing Tomcat process..."

        kill -9 ${TOMCAT_PID}

        if [ $? -eq "0" ]
        then
            echo "Tomcat process killed"
        else
            echo "Failed to kill tomcat process"
        fi
    fi
fi

exit $(ps ux | grep 'target/tomcat' | grep -v grep | wc -l)
