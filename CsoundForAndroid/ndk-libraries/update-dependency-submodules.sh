#!/bin/bash 
clear 
echo "Updating all submodules for CsoundForAndroid..."
git submodule update --init --recursive
git submodule update --recursive
git submodule status --recursive
git submodule foreach git pull
git branch
echo "Finished updating all submodules for CsoundForAndroid."
