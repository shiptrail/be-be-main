#!/usr/bin/env bash

echo "HANLDE WITH EXTREME CARE. UNSUPPORTED"
read -n1 -r -p "Press space to continue or CTRL+C to abort..." key

FEBUILDDIR=$(mktemp -d -t "fe-build_XXXXX")
FEARTIFACTDIR="fe-root/fe"

function cleanup {
  rm -rf "$WORK_DIR"
  echo "Deleted temp working directory $WORK_DIR"
}
trap cleanup EXIT

rm -rf $FEBUILDDIR
mkdir -p $FEBUILDDIR
git clone https://belize.imp.fu-berlin.de/r/fe/fe-main $FEBUILDDIR
pushd $FEBUILDDIR
npm install || exit 1
bower install || exit 1
typings install || exit 1
gulp || exit 1
popd
rm -rf $FEARTIFACTDIR
mkdir -p $FEARTIFACTDIR
cp -pr $FEBUILDDIR/dist/* $FEARTIFACTDIR
rm -rf $FEBUILDDIR
sbt clean
sbt debian:packageBin
sbt rpm:packageBin
