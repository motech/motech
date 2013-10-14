#!/bin/bash

function purge_motech() {
    $CHROOT apt-get purge motech-base -y --force-yes
    $CHROOT rm -rf /var/log/motech/motech-default
    $CHROOT rm -rf /var/cache/motech/motech-default
    $CHROOT rm -rf /usr/share/motech/motech-default
    $CHROOT rm -rf /etc/motech/motech-default
    $CHROOT rm -rf /var/lib/motech/motech-default
    $CHROOT rm -f /etc/init.d/motech-default
}

while getopts "d:b:e:p:t:" opt; do
    case $opt in
    d)
        CHROOT_DIR=$OPTARG
    ;;
    b)
        BUILD_DIR=$OPTARG
    ;;
    e)
        ERROR_LOG=$OPTARG
    ;;
    p)
        PORT=$OPTARG
    ;;
    t)
        TENANT_PORT=$OPTARG
    esac
done

PORT=${PORT-7010}
TENANT_PORT=${TENANT_PORT-7012}

SHUTDOWN_PORT=$(($PORT + 1))
TENANT_SHUTDOWN_PORT=$(($TENANT_PORT + 1))

echo "Using ports: $PORT/$SHUTDOWN_PORT, tenant ports: $TENANT_PORT/$TENANT_SHUTDOWN_PORT"

if [ -z $ERROR_LOG ]; then
    ERROR_LOG=$BUILD_DIR/err.log
fi

if [ -z $CHROOT_DIR ]; then
    echo "Chroot dir not defined" > $ERROR_LOG
    exit 1
fi

BASE_PACKAGE=`ls $BUILD_DIR | grep motech-base`

if [ ! -f $BUILD_DIR/$BASE_PACKAGE ]; then
    echo "Base package does not exist: $BASE_PACKAGE" > $ERROR_LOG
    exit 2
fi

MAKEROOT=""
if [[ $EUID -ne 0 ]];then
    MAKEROOT="sudo"
fi

CHROOT="$MAKEROOT chroot $CHROOT_DIR"

MOTECH_OWNED="/var/lib/motech/motech-default /var/cache/motech/motech-default"
NON_MOTECH_OWNED="/var/lib/motech/motech-default /var/cache/motech/motech-default"

# Remove previous installation if any
purge_motech

# Install package
cp $BUILD_DIR/$BASE_PACKAGE $CHROOT_DIR/tmp
$CHROOT dpkg -i /tmp/$BASE_PACKAGE
$CHROOT apt-get install -f $YES # install dependencies

# Change the ports
$CHROOT sed -i "s/8080/$PORT/i" /usr/share/motech/motech-default/conf/server.xml
$CHROOT sed -i "s/8005/$SHUTDOWN_PORT/i" /usr/share/motech/motech-default/conf/server.xml

$CHROOT service motech start

# Make sure files/directories exist with correct permissions

for dir in $MOTECH_OWNED; do
    if [ `$CHROOT stat -c %U /var/lib/motech/motech-default` != "motech-default" ]; then
        echo "$dir is not owned by motech-default" > $ERROR_LOG
        purge_motech
        exit 3
    fi
done

for dir in $NON_MOTECH_OWNED; do
    $CHROOT file $dir # returns 1 if failed
    RET=$?
    if [ $RET -ne 0 ]; then
        echo "$dir does not exist" > $ERROR_LOG
        purge_motech
        exit $RET
    fi
done

# Give motech some time
sleep 5

# Check the homepage
curl -L "localhost:$PORT" --retry 5 --connect-timeout 30 | grep -i motech
RET=$? # Success?
if [ $RET -ne 0 ]; then
    echo "Failed getting motech page" > $ERROR_LOG
    cat $CHROOT_DIR/var/log/motech/motech-default/catalina.out >> $ERROR_LOG
    purge_motech
    exit $RET
fi

# Tenant test
#

# Remove previous installation if any
$CHROOT sh /usr/share/motech/motech-manage-tenants remove test

# Install new tenant
$CHROOT sh /usr/share/motech/motech-manage-tenants add test $TENANT_PORT $TENANT_SHUTDOWN_PORT

# Change the ports
$CHROOT sed -i "s/$PORT/$TENANT_PORT/i" /usr/share/motech/motech-test/conf/server.xml
$CHROOT sed -i "s/$SHUTDOWN_PORT/$TENANT_SHUTDOWN_PORT/i" /usr/share/motech/motech-test/conf/server.xml

$CHROOT service motech-test start

# Give motech some time
sleep 5

# Check the homepage
curl -L "localhost:$TENANT_PORT" --retry 5 --connect-timeout 30 | grep -i motech
RET=$? # Success?
if [ $RET -ne 0 ]; then
    echo "Failed getting motech-tenant page" > $ERROR_LOG
    cat $CHROOT_DIR/var/log/motech/motech-test/catalina.out >> $ERROR_LOG
    $CHROOT sh /usr/share/motech/motech-manage-tenants remove test
    purge_motech
    exit $RET
fi

# Remove tenant
$CHROOT sh /usr/share/motech/motech-manage-tenants remove test

exit 0 # Victory
