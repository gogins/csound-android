#!/bin/bash
echo "Updating all source dependencies of Csound for Android..."
git submodule update --init --recursive
git submodule update --recursive
git submodule status --recursive
git submodule foreach git pull
git branch
echo "Finished updating all submodules for CsoundForAndroid."
ls -ll
cd ..
echo "Finished updating all source dependencies of Csound for Android."
