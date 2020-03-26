#!/bin/bash 
echo "Updating all submodules for csound-android..."
git submodule update --init --recursive --remote
git submodule update --recursive
git submodule status --recursive
sudo -k
echo "Finished updating all submodules for csound-android."
