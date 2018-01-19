#!/bin/bash
echo "Updating all direct downloads required for csound-extended..."
echo
echo "Updating ASIO SDK..."
wget "http://www.steinberg.net/sdk_downloads/asiosdk2.3.zip"
7z x -y "asiosdk2.3.zip"
echo "Updating VST SDK..."
wget "http://download.steinberg.net/sdk_downloads/vstsdk368_08_11_2017_build_121.zip"
yz x -y "vstsdk368_08_11_2017_build_121.zip"
echo "Finished updating all direct downloads required for csound-extended."
