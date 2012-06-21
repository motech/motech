#!/bin/sh

# Exit on non-zero exit code
set -e

export VERSION=0.11_1
export BUNDLE_DIR=usr/share/motech/.motech/bundles

export TMP_DIR=/tmp/motech-packaging-$$
mkdir $TMP_DIR

export START_DIR=`pwd`

# Read motech base from the first parameter if present
if [ -z $MOTECH_BASE ]; then
    MOTECH_BASE=../../..
    if [ $# -gt 0 ]; then
        MOTECH_BASE=$1
    fi
fi
# Resolve path
cd $MOTECH_BASE
export MOTECH_BASE=`pwd`

export PACKAGING_DIR=$MOTECH_BASE/packaging/debian

cd $TMP_DIR
$PACKAGING_DIR/modules/motech-demo/build.sh