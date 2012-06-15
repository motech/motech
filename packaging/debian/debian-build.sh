#!/bin/bash

MOTECH_VERSION=0.11_1
TMP_DIR=/tmp/motech-debian-build-$$
WARNAME=motech-platform-server.war
MOTECH_PACKAGENAME="motech-base_$MOTECH_VERSION.deb"
CURRENT_DIR=`pwd`

# exit on non-zero exit code
set -e

# Set motech directory
MOTECH_BASE=../..
if [ $# -gt 0 ]; then
	MOTECH_BASE=$1
fi

cd $MOTECH_BASE
MOTECH_BASE=`pwd`

PACKAGING_DIR=$MOTECH_BASE/packaging/debian
WAR_PATCHDIR=$PACKAGING_DIR/warpatch
MOTECH_WAR=$MOTECH_BASE/motech-platform-server/target/$WARNAME

if [ ! -f $MOTECH_WAR ]; then
	echo $MOTECH_WAR does not exist
	exit 1
fi

echo "Patching $WARNAME"

# unzip .war file
mkdir $TMP_DIR
cp $MOTECH_WAR $TMP_DIR
cd $TMP_DIR
unzip $WARNAME 1>/dev/null
rm $WARNAME

# apply patches
for PATCH in $WAR_PATCHDIR/*
do
	patch -p0 -i $PATCH
done

# zip .war
zip -r $WARNAME * 1>/dev/null

# copy motech-base
cp -r $PACKAGING_DIR/motech-base .
mv $WARNAME ./motech-base/var/lib/motech/webapps/

# set up permissions
find ./motech-base -type d | xargs chmod 755  # for directories
find ./motech-base -type f | xargs chmod 644  # for files
chmod 755 ./motech-base/DEBIAN/postinst
chmod 755 ./motech-base/DEBIAN/prerm
chmod 755 ./motech-base/DEBIAN/postrm
chmod 755 ./motech-base/DEBIAN/control
chmod 755 ./motech-base/etc/init.d/motech

# Build package
echo "Building package"
fakeroot dpkg-deb --build motech-base

if [ ! -d $PACKAGING_DIR/target ]; then
	mkdir $PACKAGING_DIR/target
fi

mv motech-base.deb $PACKAGING_DIR/target/$MOTECH_PACKAGENAME

# Check package for problems
echo "Checking package with lintian"
lintian -i $PACKAGING_DIR/target/$MOTECH_PACKAGENAME

echo "Done! Created $MOTECH_PACKAGENAME"

#clean up 
cd $CURRENT_DIR
rm -r $TMP_DIR
