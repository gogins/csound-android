APP_CFLAGS += -Wno-error=format-security
APP_ABI := armeabi-v7a arm64-v8a x86_64
APP_CPPFLAGS += -fexceptions -frtti -Ofast -std=c++14
APP_OPTIM := release
APP_PLATFORM := android-33
APP_STL := c++_shared
NDK_TOOLCHAIN_VERSION := clang
