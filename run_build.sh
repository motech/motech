#!/usr/bin/env bash

if [ "$TRAVIS_EVENT_TYPE" = "cron" ]; then
    git clone https://github.com/motech/motech.git ../motech-master -b master --single-branch

    mkdir ~/.motech
    cp ../motech-master/testdata/config-locations.properties ~/.motech/

    cd ../motech-master/platform/mds/mds-performance-tests/
    mvn -Dmds.performance.quantity=10000 clean install -U -PMDSP
fi
