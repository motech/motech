#!/bin/sh

# Exit on non-zero exit code
set -e

export BUNDLE_DIR=usr/share/motech/.motech/bundles

export TMP_DIR=/tmp/motech-packaging-$$
mkdir $TMP_DIR

export START_DIR=`pwd`

export MOTECH_BASE
export CONTENT_DIR
export ARTIFACT_DIR
export MOTECH_VERSION
export BUILD_DIR

cd $TMP_DIR

$CONTENT_DIR/modules/motech-demo/build.sh

rm -r $TMP_DIR