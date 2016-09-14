#!/usr/bin/env bash

if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
    if [ "$DB" = "psql" ]; then
        mvn -Dmotech.sql.password=password -Dmotech.sql.user=postgres -Dmaven.test.failure.ignore=false -Dmotech.sql.driver=org.postgresql.Driver -Dmotech.sql.dbtype=psql -Dmotech.sql.url=jdbc:postgresql://localhost:5432/ clean install -PIT -U
    else
        mvn clean install -PIT -U
    fi
elif [ "$TRAVIS_BRANCH" = "master" ] && [ "$DB" = "mysql" ]; then
    MOTECH_LOCATION=`pwd`

    #Download and test Modules
    git clone https://github.com/motech/modules.git ../modules -b master --single-branch
    cd ../modules/
    mvn clean install -PIT -U

    #If build fails, return error code
    if [ "$?" -ne 0 ]; then
        exit 1
    fi

    #Deploy MOTECH
    cd $MOTECH_LOCATION
    mvn clean deploy --settings deploy-settings.xml -Dmaven.test.skip=true -U
fi