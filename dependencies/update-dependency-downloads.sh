#!/bin/bash
echo "Updating all direct downloads required for csound-android..."
echo
echo "Updating ASIO SDK..."
wget "http://www.steinberg.net/sdk_downloads/asiosdk2.3.zip"
7z x -y "asiosdk2.3.zip"
sudo -k
echo "Finished updating all direct downloads required for csound-android."
