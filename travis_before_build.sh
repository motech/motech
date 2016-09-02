#!/usr/bin/env bash

if [ "$DB" = "mysql" ]; then
    echo "USE mysql;\nUPDATE user SET password=PASSWORD('password') WHERE user='root';\nFLUSH PRIVILEGES;\n" | mysql -u root
elif [ "$DB" = "psql" ]; then
    sed -i 's/motech.sql.dbtype=mysql/motech.sql.dbtype=psql/g; s/root/postgres/g; s/com.mysql.jdbc.Driver/org.postgresql.Driver/g; s#jdbc:mysql://localhost:3306/#jdbc:postgresql://localhost:5432/#g;' ./maven.properties
fi