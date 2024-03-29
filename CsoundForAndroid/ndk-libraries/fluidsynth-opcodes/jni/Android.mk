LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
CSOUND_SRC_ROOT := $(NDK_MODULE_PATH)/csound

LIBSNDFILE_SRC_DIR := $(NDK_MODULE_PATH)/libsndfile-android/jni/
LIBFLUIDSYNTH_SRC_DIR := $(NDK_MODULE_PATH)/fluidsynth-android/jni/

LOCAL_MODULE   := fluidOpcodes

LOCAL_C_INCLUDES := $(CSOUND_SRC_ROOT)/H $(CSOUND_SRC_ROOT)/include $(CSOUND_SRC_ROOT)/ $(LIBSNDFILE_SRC_DIR) $(LIBFLUIDSYNTH_SRC_DIR)/include $(CSOUND_SRC_ROOT)/Opcodes/fluidOpcodes/ $(LOCAL_PATH)/../../../CsoundAndroid/jni 
LOCAL_CFLAGS := -D__BUILDING_LIBCSOUND -DENABLE_NEW_PARSER -DLINUX -DHAVE_DIRENT_H -DHAVE_FCNTL_H -DHAVE_UNISTD_H -DHAVE_STDINT_H -DHAVE_SYS_TIME_H -DHAVE_SYS_TYPES_H -DHAVE_TERMIOS_H 
LOCAL_CPPFLAGS :=$(LOCAL_CFLAGS)
LOCAL_CPPFLAGS += -std=c++11 -pthread -frtti -fexceptions

LOCAL_SRC_FILES := ../../plugins/src/fluidOpcodes/fluidOpcodes.cpp 

LOCAL_LDLIBS += -ldl

LOCAL_STATIC_LIBRARIES := fluidsynth-android 

include $(BUILD_SHARED_LIBRARY)

$(call import-module,fluidsynth-android/jni)
