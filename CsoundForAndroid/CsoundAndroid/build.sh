#!/bin/sh
echo "Building libcsoundandroid.so..."
MACHINE="$(uname -s)"
case "${MACHINE}" in 
  MINGW*) NDK_BUILD_CMD=$ANDROID_NDK_ROOT/ndk-build.cmd;;
  *) NDK_BUILD_CMD=$ANDROID_NDK_ROOT/ndk-build
esac

echo "NDK_BUILD_COMMAND: $NDK_BUILD_CMD"
export CSOUND_SRC_ROOT=$NDK_MODULE_PATH/csound
echo "CSOUND_SRC_ROOT: $CSOUND_SRC_ROOT"

flex -B -t $CSOUND_SRC_ROOT/Engine/csound_orc.lex > jni/csound_orclex.c 
flex -B $CSOUND_SRC_ROOT/Engine/csound_pre.lex > jni/csound_prelex.c 
flex -B $CSOUND_SRC_ROOT/Engine/csound_prs.lex > jni/csound_prslex.c 
bison -d -pcsound_orc --report=itemset -o jni/csound_orcparse.c $CSOUND_SRC_ROOT/Engine/csound_orc.y

rm -rf src/csnd
mkdir -p src/csnd

swig -java -package csnd -D__BUILDING_LIBCSOUND -DENABLE_NEW_PARSER -DPARCS -DHAVE_DIRENT_H -DHAVE_FCNTL_H -DHAVE_UNISTD_H -DHAVE_STDINT_H -DHAVE_SYS_TIME_H -DHAVE_SYS_TYPES_H -DHAVE_TERMIOS_H -DHAVE_PTHREAD -DHAVE_BUILTIN_ATOMIC -DHAVE_ATOMIC_BUILTIN -includeall -verbose -outdir src/csnd -c++ -I./jni -I$NDK_MODULE_PATH/oboe/include -I$CSOUND_SRC_ROOT/H -I$CSOUND_SRC_ROOT/include -I$CSOUND_SRC_ROOT/Engine -I$CSOUND_SRC_ROOT/ -I$CSOUND_SRC_ROOT/interfaces -I/System/Library/Frameworks/Python.framework/Headers -I/System/Library/Frameworks/JavaVM.framework/Headers -o jni/java_interfaceJAVA_wrap.cpp android_interface.i

# ADJUST SWIG CODE FOR ANDROID and DIRECTORS
#-sed -i.bak "s/AttachCurrentThread((void \*\*)/AttachCurrentThread(/" jni/java_interfaceJAVA_wrap.cpp 

# Actually build Csound.
cd jni

$NDK_BUILD_CMD V=1 -j6 $1
echo "Finished building libcsoundandroid.so."


