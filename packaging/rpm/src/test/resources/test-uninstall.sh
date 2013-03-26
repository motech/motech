#!/bin/bash

function purge_motech() {
    $CHROOT yum remove motech-base -y
    $CHROOT rm -rf /var/log/motech/motech-default
    $CHROOT rm -rf /var/cache/motech/motech-default
    $CHROOT rm -rf /usr/share/motech/motech-default
    $CHROOT rm -rf /etc/motech/motech-default
    $CHROOT rm -rf /var/lib/motech/motech-default
    $CHROOT rm -f /etc/init.d/motech-default
}

while getopts "d:b:e:" opt; do
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
    esac
done

PORT=${PORT-8099}

if [ -z $ERROR_LOG ]; then
    ERROR_LOG=$BUILD_DIR/err.log
fi

if [ -z $CHROOT_DIR ]; then
    echo "Chroot dir not defined" > $ERROR_LOG
    exit 1
fi

MAKEROOT=""
if [[ $EUID -ne 0 ]];then
    MAKEROOT="sudo"
fi

CHROOT="$MAKEROOT chroot $CHROOT_DIR"

MOTECH_OWNED="/var/lib/motech/motech-default /var/cache/motech/motech-default"
NON_MOTECH_OWNED="/var/lib/motech/motech-default /var/cache/motech/motech-default"

$CHROOT service motech stop

# Make sure some dirs are empty, so they can be removed
$CHROOT rm -rf /var/log/motech/*

# Remove motech
$CHROOT yum remove motech-base -y

for dir in $MOTECH_OWNED; do
    if [ -d $CHROOT_DIR/$dir ]; then
        echo "$dir still exists after uninstall" > $ERROR_LOG
        purge_motech
        exit 1
    fi
done

exit 0 # Victory
