#!/bin/bash
echo "Updating all source dependencies of Csound for Android..."
git submodule update --init --recursive
git submodule update --recursive --remote
git submodule status --recursive
git pull --recurse-submodules
git branch
echo "Finished updating all submodules for CsoundForAndroid."
ls -ll
# The idea here is to drop all of cloud-5 into the html directory of the app.
wget https://github.com/gogins/gogins.github.io/archive/refs/heads/main.zip -O cloud-5.zip
mkdir -p CsoundForAndroid/CsoundApplication/src/main/assets/cloud-5
tar -xf cloud-5.zip --strip-components 1 -C CsoundForAndroid/CsoundApplication/src/main/assets/cloud-5
cp -rf csound-examples/csound-for-android/* CsoundForAndroid/CsoundApplication/src/main/assets/examples
cp -rf csound-examples/docs/* CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins
find CsoundForAndroid/CsoundApplication/src/main/assets/ -name "*.lisp" -delete
find CsoundForAndroid/CsoundApplication/src/main/assets/ -name "*.saved" -delete
find CsoundForAndroid/CsoundApplication/src/main/assets/ -name "*.mkg" -delete
find CsoundForAndroid/CsoundApplication/src/main/assets/ -name "*.backup" -delete
find CsoundForAndroid/CsoundApplication/src/main/assets/ -name "*_postjs.js" -delete
find CsoundForAndroid/CsoundApplication/src/main/assets/ -name "*_prejs.js" -delete
find CsoundForAndroid/CsoundApplication/src/main/assets/ -name "*.pem" -delete
find CsoundForAndroid/CsoundApplication/src/main/assets/ -name "*-generated.csd" -delete
find CsoundForAndroid/CsoundApplication/src/main/assets/ -name "*.generated.csd" -delete
find CsoundForAndroid/CsoundApplication/src/main/assets/ -name "CsoundAudioProcessor.*" -delete
find CsoundForAndroid/CsoundApplication/src/main/assets/ -name "CsoundAudioNode.*" -delete
find CsoundForAndroid/CsoundApplication/src/main/assets/
echo "Finished updating all source dependencies of Csound for Android."
