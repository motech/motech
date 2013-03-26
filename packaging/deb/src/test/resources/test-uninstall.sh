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
$CHROOT apt-get remove motech-base -y --force-yes

for dir in $MOTECH_OWNED" "$NON_MOTECH_OWNED; do
    $CHROOT file $dir # will return 0 if exists
    RET=$?
    if [ $RET -eq 0 ]; then
        echo "$dir still exists after uninstall" > $ERROR_LOG
        purge_motech
        exit 1
    fi
done

exit 0 # Victory
