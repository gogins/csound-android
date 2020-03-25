# csound-android
![GitHub All Releases (total)](https://img.shields.io/github/downloads/gogins/csound-extended/total.svg)<br>
Michael Gogins<br>
https://github.com/gogins<br>
http://michaelgogins.tumblr.com

## Introduction

This repository contains Csound for Android, almost all features of Csound 
in an Android app that also integrates Csound with HTML5. Please note, 
dependencies of Csound for Android are fetched from the core Csound 
repository, and rebuilt using the Android NDK.

This code used to be maintained in the csound-extended repository but now 
lives in its own repository for easier maintenance. You may notice some 
oddities and leftovers...

This repository uses the core Csound packages, and some other third-party
dependencies, as Git submodules, packages, or direct source downloads. 

Please log any bug reports or requests for enhancements at
https://github.com/gogins/csound-android/issues.

See https://github.com/gogins/csound-android/commits/develop for the commit
log.

## Installation

The Csound for Android app is available from the Google Play Store, or may
be installed from the CsoundApplication-release.apk package released from this 
repository.

## Building

Currently, the supported platforms are Linux, Android, and WebAssembly.
The code is generally "cross-platform" in nature and this build system could
be adapted to build for Windows or OS X.

First clone the Git repository at https://github.com/gogins/csound-android.

Source `build-env.sh` to set useful environment variables, as below, for the 
build and runtime environment on Linux. You may need to copy and modify this 
script.
  
The following environment variables MUST be set before building, perhaps in
your .profile script. Obviously, modify the paths as required to suit your
home directory and installation details. These are exported in `build-env.sh` 
which you can source in your .profile script.

Please note, some NDK dependencies are built in their own subdirectories,
and some are built in OTHER subdirectories with their own makefiles that
refer to source files in the ORIGINAL subdirectories. There is a naming
convention, e.g. `link` is the original Git repository for the Ableton Link
Kit which we do not build, and `link-opcodes` is our subdirectory which we do
build# and which includes files from the `link` subdirectory.

Prerequisites for building Csound for Android include:

1.  You must install Android Studio 3.0.1, Android SDKs 28, 27.1.1, 23, and 21,
    GDB, LLDB, the NDK, and build tools 26.0.2.

2.  In order to enable local NDK builds (i.e. in individual subdirectories),
    you must set the following environment variables, probably in your
    .profile script:

    2.1.    ANDROID_NDK_ROOT with the full pathname of your Android Native
            Development kit, typically $ANDROID_SDK_ROOT/ndk-bundle.

    2.2.    ANDROID_SDK_ROOT with the full pathname of your Android Software
            Development kit, perhaps something like ~/Android/Sdk.

    2.3.    CSOUND_SRC_ROOT with the full pathname to this repository's
            dependencies/csound subdirectory.

    2.4.    NDK_MODULE_PATH with the full pathname to this repository's
            CsoundForAndroid/ndk-libraries subdirectory.

To build for Android on Linux for the first time, change to the
CsoundForAndroid subdirectory of this repository and execute
`bash fresh-build-android.sh`, which does the following:

1.  Execute `bash update-dependencies`. Do this periodically or whenever
    you think a dependency has changed.

2.  Execute `bash build-android.sh`. The build system compiles all native
    libraries, including the Csound library libcsoundandroid.so, required
    by the Csound for Android app, and copies them to the
    appropriate subdirectories for further building and packaging.

Run Android Studio and load the CsoundForAndroid/build.gradle project.

Attach an Android device, enable USB debugging on it, and run or debug the
CsoundApplication project.

For a production build, apply to me for the signing key, build for
release, and generate a signed .apk.

