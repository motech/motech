  #!/usr/bin/env bash

if [ "$TRAVIS_EVENT_TYPE" = "cron" ]; then
    git clone https://github.com/motech/motech.git ../motech-master -b master --single-branch
    mkdir ~/.motech
    cp ../motech-master/testdata/config-locations.properties ~/.motech/
    cd ../motech-master/modules/admin/
    mvn -Dmysql.password=password -Dmaven.test.failure.ignore=false -Dmysql.user=root -DunitTests.skip=true clean install -PFT -U
fi