#!/bin/bash

# Usage debian_build.sh [-v version] [-b motech_trunk_directory] [-d destination_directory]

MOTECH_VERSION=0.12-SNAPSHOT
TMP_DIR=/tmp/motech-debian-build-$$
WARNAME=motech-platform-server.war
CURRENT_DIR=`pwd`

# exit on non-zero exit code
set -e

# Set motech directory
MOTECH_BASE=.

while getopts "v:b:d:" opt; do
	case $opt in
	v)
		MOTECH_VERSION=$OPTARG
	;;
	b)
		MOTECH_BASE=$OPTARG
	;;
	d)
	    DEST_DIR=$OPTARG
	;;
	esac
done

if [ -z $DEST_DIR ]; then
    DEST_DIR=$MOTECH_BASE/motech-deb/target
fi

mkdir -p $DEST_DIR

CONTENT_DIR=$MOTECH_BASE/motech-deb/src/main/debian

MOTECH_PACKAGENAME="motech_$MOTECH_VERSION.deb"
MOTECH_BASE_PACKAGENAME="motech-base_$MOTECH_VERSION.deb"

cd $MOTECH_BASE
MOTECH_BASE=`pwd`

MOTECH_WAR=$MOTECH_BASE/motech-platform-server/target/$WARNAME

echo "====================="
echo "Building motech-base"
echo "====================="

if [ ! -f $MOTECH_WAR ]; then
    echo $MOTECH_WAR does not exist
    exit 1
fi

# Create a temp dir for package building
mkdir $TMP_DIR
cp $MOTECH_WAR $TMP_DIR
cd $TMP_DIR

# Create empty dirs if missing
mkdir -p motech-base/var/cache/motech/work/Catalina/localhost
mkdir -p motech-base/var/cache/motech/temp
mkdir -p motech-base/var/cache/motech/felix-cache
mkdir -p motech-base/var/lib/motech/webapps
mkdir -p motech-base/var/log/motech
mkdir -p motech-base/usr/share/motech/.motech/bundles
mkdir -p motech-base/usr/share/motech/.motech/rules

# copy motech-base
cp -r $CONTENT_DIR/motech-base .
mv $WARNAME ./motech-base/var/lib/motech/webapps/ROOT.war

# Update version
perl -p -i -e "s/\\$\\{version\\}/$MOTECH_VERSION/g" ./motech-base/DEBIAN/control

#Copy config
cp -r $MOTECH_BASE/motech-platform-server-config/src/main/config ./motech-base/usr/share/motech/.motech

mkdir -p ./motech-base/usr/share/motech/.motech/bundles
# Include motech-admin
cp -r $MOTECH_BASE/motech-admin-bundle/target/motech-admin-bundle*.jar ./motech-base/usr/share/motech/.motech/bundles

# set up permissions
find ./motech-base -type d | xargs chmod 755  # for directories
find ./motech-base -type f | xargs chmod 644  # for files
# special permissions for executbale files
chmod 755 ./motech-base/DEBIAN/postinst
chmod 755 ./motech-base/DEBIAN/prerm
chmod 755 ./motech-base/DEBIAN/postrm
chmod 755 ./motech-base/DEBIAN/control
chmod 755 ./motech-base/etc/init.d/motech

# Build package
echo "Building package"
fakeroot dpkg-deb --build motech-base

mv motech-base.deb $DEST_DIR/$MOTECH_BASE_PACKAGENAME

# Check package for problems
echo "Checking package with lintian"
lintian -i $DEST_DIR/$MOTECH_BASE_PACKAGENAME

echo "Done! Created $MOTECH_PACKAGENAME"

#clean up
rm -r $TMP_DIR/*

echo "====================="
echo "Building motech"
echo "====================="

# copy files
cp -r $CONTENT_DIR/motech .
# Update version
perl -p -i -e "s/\\$\\{version\\}/$MOTECH_VERSION/g" ./motech/DEBIAN/control

# set up permissions
find ./motech -type d | xargs chmod 755  # for directories
find ./motech -type f | xargs chmod 644  # for files
# special permissions for executbale files
chmod 755 ./motech/DEBIAN/control

echo "Building package"

fakeroot dpkg-deb --build motech
mv motech.deb $DEST_DIR/$MOTECH_PACKAGENAME

echo "Checking package with lintian"
lintian -i $DEST_DIR/$MOTECH_PACKAGENAME

echo "Done! Created $MOTECH_PACKAGENAME"

# clean up
cd $CURRENT_DIR
rm -r $TMP_DIR

# build modules
export MOTECH_BASE
export MOTECH_VERSION
export DEST_DIR
export CONTENT_DIR

$CONTENT_DIR/modules/build-modules.sh