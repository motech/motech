#!/bin/bash

MOTECH_VERSION=0.11_1
TMP_DIR=/tmp/$$
BUILD_DIR=`pwd`
WARNAME=motech-platform-server.war
WAR_PATCHDIR=$BUILD_DIR/warpatch
MOTECH_PACKAGENAME="motech-base_$MOTECH_VERSION.deb"

# exit on non-zero exit code
set -e

# Set motech directory
MOTECH_BASE=../..
if [ $# -gt 1 ]; then
	MOTECH_BASE=$1
fi

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
mv $WARNAME $BUILD_DIR/motech-base/var/lib/motech/webapps/

# clean up
cd $BUILD_DIR
rm -r $TMP_DIR

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

if [ ! -d target ]; then
	mkdir target
fi

mv motech-base.deb target/$MOTECH_PACKAGENAME

# Check package for problems
echo "Checking package with lintian"
lintian -i target/$MOTECH_PACKAGENAME

echo "Done! Created $MOTECH_PACKAGENAME"

#clean up war
rm $BUILD_DIR/motech-base/var/lib/motech/webapps/$WARNAME
