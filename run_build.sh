#!/usr/bin/env bash

if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
    if [ "$DB" = "mysql" ]; then
        echo "USE mysql;\nUPDATE user SET password=PASSWORD('password') WHERE user='root';\nFLUSH PRIVILEGES;\n" | mysql -u root
        mvn clean install -PIT -U -q
    elif [ "$DB" = "psql" ]; then
        mvn -Dmotech.sql.password=password -Dmotech.sql.user=postgres -Dmaven.test.failure.ignore=false -Dmotech.sql.driver=org.postgresql.Driver -Dmotech.sql.dbtype=psql -Dmotech.sql.url=jdbc:postgresql://localhost:5432/ clean install -PIT -U
    fi
fi