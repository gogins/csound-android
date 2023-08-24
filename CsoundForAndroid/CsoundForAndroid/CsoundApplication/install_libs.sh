#!/bin/sh

# Simple script to copy "local" shared libs to the installation libs directories.
# This because I can't seem to get the canonical way to do this to work.

JNILIBS=src/main/jniLibs

mkdir -p $JNILIBS 
mkdir -p $JNILIBS/armeabi-v7a
mkdir -p $JNILIBS/arm64-v8a
mkdir -p $JNILIBS/x86_64

CSOUND_SRC_ROOT=$NDK_MODULE_PATH/csound

echo "Copying ndk-build shared libraries..."
find $NDK_MODULE_PATH -wholename "*/libs/arm64-v8a/*.so" -exec cp -f {} $JNILIBS/arm64-v8a/ ';' 
find $NDK_MODULE_PATH -wholename "*/libs/armeabi-v7a/*.so" -exec cp -f {} $JNILIBS/armeabi-v7a/ ';'
find $NDK_MODULE_PATH -wholename "*/libs/x86_64/*.so" -exec cp -f {} $JNILIBS/x86_64/ ';'
echo "Copying cmake shared libraries..."
find $NDK_MODULE_PATH -wholename "*/release/arm64-v8a/*.so" -exec cp -f {} $JNILIBS/arm64-v8a/ ';' 
find $NDK_MODULE_PATH -wholename "*/release/armeabi-v7a/*.so" -exec cp -f {} $JNILIBS/armeabi-v7a/ ';'
find $NDK_MODULE_PATH -wholename "*/release/x86_64/*.so" -exec cp -f {} $JNILIBS/x86_64/ ';'
echo "Copying CsoundAndroid shared libraries..."
find $NDK_MODULE_PATH/.. -wholename "*/local/arm64-v8a/*.so" -exec cp -f {} $JNILIBS/arm64-v8a/ ';' 
find $NDK_MODULE_PATH/.. -wholename "*/local/armeabi-v7a/*.so" -exec cp -f {} $JNILIBS/armeabi-v7a/ ';'
find $NDK_MODULE_PATH/.. -wholename "*/local/x86_64/*.so" -exec cp -f {} $JNILIBS/x86_64/ ';'

echo "These are the built and copied libs for the Csound for Android app:"
find $JNILIBS -name "*.so" -ls
find ../../CsoundAndroid/libs -name "*.so" -ls

# Also copy other assets used by Csound opcodes or the Csound app.

mkdir -p src/main/assets/samples/
cp -f $CSOUND_SRC_ROOT/samples/* src/main/assets/samples

rm -rf src/main/assets/examples
mkdir -p src/main/assets/examples
cp -rf ../../csound-examples/csound-for-android/* src/main/assets/examples/


