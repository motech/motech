#!/bin/sh

# Exit on non-zero exit code
set -e

# Motech demo
echo "====================="
echo "Building motech-demo"
echo "====================="

MODULE_DIR=$CONTENT_DIR/modules/motech-demo

# Copy control
mkdir -p $TMP_DIR/motech-demo/DEBIAN
cp $MODULE_DIR/control $TMP_DIR/motech-demo/DEBIAN/control
# Update version
perl -p -i -e "s/\\$\\{version\\}/$MOTECH_VERSION/g" $TMP_DIR/motech-demo/DEBIAN/control

# Copy copyright
mkdir -p $TMP_DIR/motech-demo/usr/share/doc/motech-demo
cp $CONTENT_DIR/motech/usr/share/doc/motech/copyright $TMP_DIR/motech-demo/usr/share/doc/motech-demo/copyright
# Copy changelog
cp $MODULE_DIR/changelog* $TMP_DIR/motech-demo/usr/share/doc/motech-demo
gzip --best $TMP_DIR/motech-demo/usr/share/doc/motech-demo/changelog*

# Copy bundle
mkdir -p $TMP_DIR/motech-demo/$BUNDLE_DIR
cp $ARTIFACT_DIR/motech-demo-bundle*.jar $TMP_DIR/motech-demo/$BUNDLE_DIR

# Copy scripts
cp $MODULE_DIR/../common/post* $TMP_DIR/motech-demo/DEBIAN

# Permissions
find $TMP_DIR/motech-demo -type d | xargs chmod 755 # directories
find $TMP_DIR/motech-demo -type f | xargs chmod 644 # files
chmod 755 $TMP_DIR/motech-demo/DEBIAN/*

# Build
echo "Building package"
PACKAGE_NAME=motech-demo_$MOTECH_VERSION.deb
fakeroot dpkg-deb --build motech-demo
mv motech-demo.deb $BUILD_DIR/$PACKAGE_NAME

# Check for problems
echo "Checking package with lintian"
lintian -i $BUILD_DIR/$PACKAGE_NAME

# Clean up
rm -r $TMP_DIR/motech-demo

echo "Done. Finished building $PACKAGE_NAME"


