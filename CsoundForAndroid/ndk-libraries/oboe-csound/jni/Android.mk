LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
CSOUND_SRC_ROOT := $(NDK_MODULE_PATH)/csound
OBOE_SRC_ROOT := $(NDK_MODULE_PATH)/oboe/src

LOCAL_MODULE := oboe

LOCAL_C_INCLUDES := $(NDK_MODULE_PATH)/oboe/src $(NDK_MODULE_PATH)/oboe/include 
LOCAL_CFLAGS := -Ofast 
LOCAL_CPPFLAGS :=$(LOCAL_CFLAGS)
LOCAL_CPPFLAGS += -std=c++17 -Wall -frtti -fexceptions
LOCAL_LDFLAGS += -Wl,--export-dynamic
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lOpenSLES

LOCAL_SRC_FILES := \
$(OBOE_SRC_ROOT)/flowgraph/SourceI24.cpp \
$(OBOE_SRC_ROOT)/flowgraph/MonoToMultiConverter.cpp \
$(OBOE_SRC_ROOT)/flowgraph/ChannelCountConverter.cpp \
$(OBOE_SRC_ROOT)/flowgraph/SourceI32.cpp \
$(OBOE_SRC_ROOT)/flowgraph/FlowGraphNode.cpp \
$(OBOE_SRC_ROOT)/flowgraph/ClipToRange.cpp \
$(OBOE_SRC_ROOT)/flowgraph/SourceFloat.cpp \
$(OBOE_SRC_ROOT)/flowgraph/ManyToMultiConverter.cpp \
$(OBOE_SRC_ROOT)/flowgraph/SampleRateConverter.cpp \
$(OBOE_SRC_ROOT)/flowgraph/RampLinear.cpp \
$(OBOE_SRC_ROOT)/flowgraph/Limiter.cpp \
$(OBOE_SRC_ROOT)/flowgraph/SinkI24.cpp \
$(OBOE_SRC_ROOT)/flowgraph/MultiToMonoConverter.cpp \
$(OBOE_SRC_ROOT)/flowgraph/resampler/PolyphaseResampler.cpp \
$(OBOE_SRC_ROOT)/flowgraph/resampler/SincResamplerStereo.cpp \
$(OBOE_SRC_ROOT)/flowgraph/resampler/SincResampler.cpp \
$(OBOE_SRC_ROOT)/flowgraph/resampler/LinearResampler.cpp \
$(OBOE_SRC_ROOT)/flowgraph/resampler/IntegerRatio.cpp \
$(OBOE_SRC_ROOT)/flowgraph/resampler/MultiChannelResampler.cpp \
$(OBOE_SRC_ROOT)/flowgraph/resampler/PolyphaseResamplerMono.cpp \
$(OBOE_SRC_ROOT)/flowgraph/resampler/PolyphaseResamplerStereo.cpp \
$(OBOE_SRC_ROOT)/flowgraph/SinkI32.cpp \
$(OBOE_SRC_ROOT)/flowgraph/SinkI16.cpp \
$(OBOE_SRC_ROOT)/flowgraph/MonoBlend.cpp \
$(OBOE_SRC_ROOT)/flowgraph/MultiToManyConverter.cpp \
$(OBOE_SRC_ROOT)/flowgraph/SourceI16.cpp \
$(OBOE_SRC_ROOT)/flowgraph/SinkFloat.cpp \
$(OBOE_SRC_ROOT)/opensles/AudioStreamOpenSLES.cpp \
$(OBOE_SRC_ROOT)/opensles/AudioStreamBuffered.cpp \
$(OBOE_SRC_ROOT)/opensles/OpenSLESUtilities.cpp \
$(OBOE_SRC_ROOT)/opensles/OutputMixerOpenSLES.cpp \
$(OBOE_SRC_ROOT)/opensles/EngineOpenSLES.cpp \
$(OBOE_SRC_ROOT)/opensles/AudioOutputStreamOpenSLES.cpp \
$(OBOE_SRC_ROOT)/opensles/AudioInputStreamOpenSLES.cpp \
$(OBOE_SRC_ROOT)/aaudio/AAudioLoader.cpp \
$(OBOE_SRC_ROOT)/aaudio/AudioStreamAAudio.cpp \
$(OBOE_SRC_ROOT)/common/DataConversionFlowGraph.cpp \
$(OBOE_SRC_ROOT)/common/AudioStreamBuilder.cpp \
$(OBOE_SRC_ROOT)/common/SourceI32Caller.cpp \
$(OBOE_SRC_ROOT)/common/FixedBlockReader.cpp \
$(OBOE_SRC_ROOT)/common/Version.cpp \
$(OBOE_SRC_ROOT)/common/FilterAudioStream.cpp \
$(OBOE_SRC_ROOT)/common/SourceFloatCaller.cpp \
$(OBOE_SRC_ROOT)/common/Trace.cpp \
$(OBOE_SRC_ROOT)/common/StabilizedCallback.cpp \
$(OBOE_SRC_ROOT)/common/AudioStream.cpp \
$(OBOE_SRC_ROOT)/common/FixedBlockWriter.cpp \
$(OBOE_SRC_ROOT)/common/OboeExtensions.cpp \
$(OBOE_SRC_ROOT)/common/Utilities.cpp \
$(OBOE_SRC_ROOT)/common/FixedBlockAdapter.cpp \
$(OBOE_SRC_ROOT)/common/AudioSourceCaller.cpp \
$(OBOE_SRC_ROOT)/common/QuirksManager.cpp \
$(OBOE_SRC_ROOT)/common/SourceI24Caller.cpp \
$(OBOE_SRC_ROOT)/common/SourceI16Caller.cpp \
$(OBOE_SRC_ROOT)/common/AdpfWrapper.cpp \
$(OBOE_SRC_ROOT)/common/LatencyTuner.cpp \
$(OBOE_SRC_ROOT)/fifo/FifoControllerBase.cpp \
$(OBOE_SRC_ROOT)/fifo/FifoBuffer.cpp \
$(OBOE_SRC_ROOT)/fifo/FifoController.cpp \
$(OBOE_SRC_ROOT)/fifo/FifoControllerIndirect.cpp 

cmd-strip :=

include $(BUILD_SHARED_LIBRARY)
