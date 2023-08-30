LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
CSOUND_SRC_ROOT := $(NDK_MODULE_PATH)/csound

LIBSNDFILE_SRC_DIR := $(NDK_MODULE_PATH)/libsndfile-android/jni/
CSOUND_SRC_ROOT := $(NDK_MODULE_PATH)/csound

LOCAL_MODULE   := stdutil
LOCAL_C_INCLUDES := $(CSOUND_SRC_ROOT)/Engine $(CSOUND_SRC_ROOT)/H $(CSOUND_SRC_ROOT)/include $(CSOUND_SRC_ROOT)/ $(LIBSNDFILE_SRC_DIR) $(LOCAL_PATH)/../../../CsoundAndroid/jni
LOCAL_CFLAGS := -D__BUILDING_LIBCSOUND -DENABLE_NEW_PARSER -DLINUX -DHAVE_DIRENT_H -DHAVE_FCNTL_H -DHAVE_UNISTD_H -DHAVE_STDINT_H -DHAVE_SYS_TIME_H -DHAVE_SYS_TYPES_H -DHAVE_TERMIOS_H
LOCAL_CPPFLAGS := $(LOCAL_CFLAGS)
LOCAL_CPPFLAGS += -std=c++17 -pthread -frtti -fexceptions

###

LOCAL_SRC_FILES := $(CSOUND_SRC_ROOT)/util/atsa.c \
$(CSOUND_SRC_ROOT)/util/cvanal.c \
$(CSOUND_SRC_ROOT)/util/dnoise.c \
$(CSOUND_SRC_ROOT)/util/envext.c \
$(CSOUND_SRC_ROOT)/util/het_export.c \
$(CSOUND_SRC_ROOT)/util/het_import.c \
$(CSOUND_SRC_ROOT)/util/hetro.c  \
$(CSOUND_SRC_ROOT)/util/lpanal.c \
$(CSOUND_SRC_ROOT)/util/lpc_export.c \
$(CSOUND_SRC_ROOT)/util/lpc_import.c  \
$(CSOUND_SRC_ROOT)/util/mixer.c     \
$(CSOUND_SRC_ROOT)/util/pvanal.c     \
$(CSOUND_SRC_ROOT)/util/pv_export.c   \
$(CSOUND_SRC_ROOT)/util/pv_import.c    \
$(CSOUND_SRC_ROOT)/util/pvlook.c    \
$(CSOUND_SRC_ROOT)/util/scale.c \
$(CSOUND_SRC_ROOT)/Engine/scope.c \
$(CSOUND_SRC_ROOT)/util/sndinfo.c \
$(CSOUND_SRC_ROOT)/util/srconv.c \
$(CSOUND_SRC_ROOT)/util/std_util.c \
$(CSOUND_SRC_ROOT)/util/xtrct.c \
$(CSOUND_SRC_ROOT)/util/SDIF/sdif.c \
$(CSOUND_SRC_ROOT)/util/SDIF/sdif2adsyn.c \
$(CSOUND_SRC_ROOT)/util/SDIF/sdif-mem.c

LOCAL_LDLIBS += -ldl

LOCAL_SHARED_LIBRARIES += sndfile

include $(BUILD_SHARED_LIBRARY)

$(call import-module,libsndfile-android/jni)
