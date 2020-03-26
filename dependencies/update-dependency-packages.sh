#!/bin/bash
echo "Updating all Debian packages required for csound-android..."
echo
sudo apt-get update
sudo apt-get upgrade
echo "Updating build-essential..."
sudo apt-get install build-essential
echo "Updating automake..."
sudo apt-get install automake
echo "Updating autoconf..."
sudo apt-get install autoconf
echo "Updating autotools..."
sudo apt-get install autotools
echo "Updating FFmpeg..."
sudo apt-get install ffmpeg
sudo apt-get install libavfilter-dev
sudo apt-get install libmp3lame-dev
echo "Updating libtool..."
sudo apt-get install libtool
echo "Updating autogen..."
sudo apt-get install autogen
echo "Updating ecl..."
sudo apt-get install ecl
echo "Updating libeigen3-dev..."
sudo apt-get install libeigen3-dev
echo "Updating FLTK 1.3..."
sudo apt-get install fluid
sudo apt-get install libfltk1.3-dev
sudo apt-get install libfltk-images1.3
echo "Updating fluidsynth..."
sudo apt-get install libfluidsynth-dev
echo "Updating libgit2-dev..."
sudo apt-get install libgit2-dev
echo "Updating gcc-arm-linux-gnueabi..."
sudo apt-get install gcc-arm-linux-gnueabi
echo "Updating PortAudio..."
sudo apt-get install portaudio19-dev
echo "Updating PortMidi..."
sudo apt-get install libportmidi-dev
echo "Updating PortSMF..."
sudo apt-get install libportsmf-dev
echo "Updating libsox-dev..."
sudo apt-get install libsox-dev
echo "Updating gcc-aarch64-linux-gnu..."
sudo apt-get install gcc-aarch64-linux-gnu
echo "Updating linux-libc-dev:i386..."
sudo apt-get install linux-libc-dev:i386
echo "Updating LLVM..."
wget -O - https://apt.llvm.org/llvm-snapshot.gpg.key | sudo apt-key add -
sudo apt-add-repository "deb http://apt.llvm.org/xenial/ llvm-toolchain-xenial-5.0 main"
sudo apt-get update
sudo apt-get install -y clang-5.0
sudo -k
echo "Updating python3.5-dev..."
sudo apt-get install python3.5-dev
echo "Updating sbcl..."
sudo apt-get install sbcl

echo "Finished updating all Debian packages required for csound-android."
