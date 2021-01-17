#!/bin/bash
echo "Updating all source dependencies of Csound for Android..."
git submodule update --init --recursive
git submodule update --recursive --remote
git submodule status --recursive
git pull --recurse-submodules
git branch
echo "Finished updating all submodules for CsoundForAndroid."
ls -ll
cp -rf csound-examples/csound-for-android/* CsoundForAndroid/CsoundApplication/src/main/assets/examples
cp -rf csound-examples/docs/* CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins
find CsoundForAndroid/CsoundApplication/src/main/assets/
echo "Finished updating all source dependencies of Csound for Android."
