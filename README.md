# csound-android
![GitHub All Releases (total)](https://img.shields.io/github/downloads/gogins/csound-android/total.svg)<br>
Michael Gogins<br>
https://github.com/gogins<br>
http://michaelgogins.tumblr.com

## Introduction

This repository contains Csound for Android: almost all features of Csound, 
in an Android app with a built-in code editor that also integrates Csound with 
HTML5 and JavaScript.

Csound for Android used to live in the 
[csound-extended](https://github.com/gogins/csound-extended) repository, but 
has been moved here into its own repository for easier maintenance. 

For earlier releases and tags, please use 
[csound-extended](https://github.com/gogins/csound-extended)

This repository uses the core Csound packages, and some other third-party
dependencies, as Git submodules, packages, or direct source downloads. These 
dependencies are rebuilt using the Android NDK.

See https://github.com/gogins/csound-android/commits/develop for the commit
log.

Please log any bug reports or requests for enhancements at
https://github.com/gogins/csound-android/issues.

## Supported Platforms

The Csound for Android app is built for 64 bit CPU architectures only. The app 
runs on the following platforms:

- Devices running Android 5.1 or later with the following application binary 
  interfaces (ABIs):

  - armeabi-v7a
  - arm64-v8a
  - x86_64

For those wishing to develop the same Csound compositions on both Android and 
desktop computers, various Android emulators should work. The Android emulator 
that comes with Android Studio does work, and is recommended, but others also 
should work.

## Installation

The Csound for Android app is available from the Google Play Store, or may
be installed from the CsoundApplication-release.apk package released from this 
repository, or may be installed from a local build of the .apk file.

## Building

First, please understand some general features of the build system, which 
otherwise might seem odd. Some details are provided below, but in general, all 
native build steps are performed by shell scripts calling ndk-build and SWIG, 
as integrating these steps into Android Studio would be complex. Only once all 
native libraries have been built and installed, can the Csound for Android app 
be built in Android Studio.

First clone the Git repository at https://github.com/gogins/csound-android.

Please note, some NDK dependencies are built in their own subdirectories,
and some are built in OTHER subdirectories with their own makefiles that
refer to source files in the ORIGINAL subdirectories. There is a naming
convention, e.g. `link` is the original Git repository for the Ableton Link
Kit which we do not build, and `link-opcodes` is our subdirectory which we do
build and which includes files from the `link` subdirectory.

Prerequisites for building Csound for Android include:

1.  You must install up to date versions of Android Studio, Android SDK, 
    and the current SDK, GDB, LLDB, the NDK, and build tools. Install these 
    using Android Studio's SDK Manager.

2.  In order to enable local NDK builds (i.e. in individual subdirectories),
    you must set the following environment variables, probably in your
    .profile script:

    2.1.    ANDROID_NDK_ROOT with the full pathname of your Android Native
            Development kit, typically $ANDROID_SDK_ROOT/ndk/{version-number}.

    2.2.    ANDROID_SDK_ROOT with the full pathname of your Android Software
            Development kit, perhaps something like ~/Android/sdk.

    2.3.    NDK_MODULE_PATH with the full pathname to this repository's
            CsoundForAndroid/ndk-libraries subdirectory.

To build for Android on Linux or macOS for the first time, change to the
CsoundForAndroid subdirectory of this repository and execute
`bash fresh-build-android.sh`, which does the following:

1.  Execute `bash update-dependencies`. Do this periodically or whenever
    you think a dependency has changed.
    
2.  In the ndk-libraries/csound/include directory, create a valid `version.h`
    file from `version.h.in`, and a valid `float-version.h` from 
    float-version.h.in.

3.  Execute `bash build-android.sh`. The build system compiles all native
    libraries, including the Csound library libcsoundandroid.so and its 
    plugin opcode libraries, required by the Csound for Android app, and 
    copies them to the appropriate subdirectories for further building and 
    packaging.

Run Android Studio and load the CsoundForAndroid/build.gradle project.

Attach an Android device, enable USB debugging on it, and run or debug the
CsoundApplication project.

If you need to make a production build, apply to me for the signing key, 
build for release, and generate a signed .aab or .apk. The signing key should 
be placed in the csound-android/CsoundForAndroid/CsoundForAndroid directory.

