#!/bin/bash
echo "Updating all source dependencies of Csound for Android..."
git submodule update --init --recursive
git submodule update --recursive --remote
git submodule status --recursive
git pull --recurse-submodules
git branch
echo "Finished updating all submodules for CsoundForAndroid."
ls -ll
cd ..
echo "Finished updating all source dependencies of Csound for Android."
