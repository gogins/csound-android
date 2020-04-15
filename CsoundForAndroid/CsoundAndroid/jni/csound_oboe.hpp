/*
    csound_oboe.hpp:

    Copyright (C) 2017 Michael Gogins

    This file is part of Csound.

    The Csound Library is free software; you can redistribute it
    and/or modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    Csound is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with Csound; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
    02111-1307 USA

    As a special exception, if other files instantiate templates or
    use macros or inline functions from this file, this file does not
    by itself cause the resulting executable or library to be covered
    by the GNU Lesser General Public License. This exception does not
    however invalidate any other reasons why the library or executable
    file might be covered by the GNU Lesser General Public License.
*/

#ifndef __CSOUND_OBOE_HPP__
#define __CSOUND_OBOE_HPP__

#if defined(__GNUC__)
#if __cplusplus <= 199711L
  #error To use csound_oboe.hpp you need at least a C++11 compliant compiler.
#endif
#endif

#ifdef SWIG
%module csound_oboe
%{
#include "csound.hpp"
#include "csound_threaded.hpp"
%}
%pragma(java) jniclassimports = %{
import android.webkit.JavascriptInterface;
%}
%typemap(javaimports) CsoundOboe %{
import android.webkit.JavascriptInterface;
%}
#else
#include "csound.hpp"
#include "csound_threaded.hpp"
#include <oboe/Oboe.h>
#include "concurrentqueue.h"
#include "blockingconcurrentqueue.h"
#endif

/**
 * The purpose of this class is to expose as much as possible of the C++ form
 * of the Csound API found in the Csound class as a Java object in the
 * JavaScript context of the WebKit WebView in Android applications, such as
 * the Csound for Android app; and, to do this in a form that closely follows
 * the API signatures and the Csound performance lifecycle defined in the
 * CsoundThreaded class that is similarly exposed in csound.node and CsoundQt.
 * The Google Oboe library (https://github.com/google/oboe) is used to
 * interface with the Android audio driver.
 */

#ifdef SWIG

// All methods of the CsoundOboe object to be exposed to the JavaScript context
// must have their generated Java method signatures annotated here with
// "@JavascriptInterface". Please keep these in alphabetical order.
// This should expose the "core" of the Csound API without futher ado in a
// manner that is consistent with Csound's JavaScript interface in other HTML5
// environments.

%javamethodmodifiers CsoundOboe::cleanup()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::compileCsd(const char *csd)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::compileCsdText(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::compileOrc(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::evalCode(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::get0dBFS()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getAPIVersion()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getVersion()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getAudioChannel(const char *name, MYFLT *samples)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getChannel(const char *name)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getControlChannel(const char *name)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getCsound()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getCurrentTimeSamples()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getEnv(const char *name)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getKr()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getKsmps()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getMessageLevel()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getNchnls()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getNchnlsInput()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getOutputName()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getScoreOffsetSeconds()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getScoreTime()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getSr()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getStringChannel(const char *name, char *string)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getVersion()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::inputMessage(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::isScorePending()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::keyPressed(char c)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::message(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::perform()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::performAndReset()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::readScore(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::reset()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::rewindScore()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::setChannel(const char *name, double value)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::setControlChannel(const char *name, double value)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::setGlobalEnv(const char *name, const char *value)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::setInput(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::setOption(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::setOutput(const char *name,const char *type,const char *format)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::setScoreOffsetSeconds(double time)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::setScorePending(int pending)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::setStringChannel(const char *name, char *string)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::start()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::stop()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::tableGet(int table, int index)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::tableLength(int table)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::tableSet(int table, int index, double value)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::isPlaying()
%{@JavascriptInterface
public
%}

%javamethodmodifiers CsoundOboe::Cleanup()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::CompileCsd(const char *csd)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::CompileCsdText(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::CompileOrc(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::EvalCode(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::Get0dBFS()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetAPIVersion()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetVersion()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetAudioChannel(const char *name, MYFLT *samples)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetChannel(const char *name)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetControlChannel(const char *name)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetCsound()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetCurrentTimeSamples()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetEnv(const char *name)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetKr()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetKsmps()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetMessageLevel()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetNchnls()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetNchnlsInput()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetOutputName()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetScoreOffsetSeconds()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetScoreTime()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetSr()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetStringChannel(const char *name, char *string)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::GetVersion()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::InputMessage(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::IsScorePending()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::KeyPressed(char c)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::Message(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::Perform()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::ReadScore(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::Reset()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::RewindScore()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::SetChannel(const char *name, double value)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::SetControlChannel(const char *name, double value)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::SetGlobalEnv(const char *name, const char *value)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::SetInput(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::SetOption(const char *text)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::SetOutput(const char *name,const char *type,const char *format)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::SetScoreOffsetSeconds(double time)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::SetScorePending(int pending)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::SetStringChannel(const char *name, char *string)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::Start()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::Stop()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::TableGet(int table, int index)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::TableLength(int table)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::TableSet(int table, int index, double value)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::IsPlaying()
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::setOboeAudioApi(int value)
%{@JavascriptInterface
public
%}
%javamethodmodifiers CsoundOboe::getOboeAudioApi()
%{@JavascriptInterface
public
%}
#endif

// For the purpose of making the Csound API consistent across all of the
// JavaScript-enabled platforms supported by Csound, all methods exposed to 
// the JavaScript context are defined here in both camel case and initial 
// caps, and are implemented by delegating to the real definitions declared 
// here or in base classes with initial caps. All these methods also are 
// annotated for exposure to WebKit by SWIG at the head of this file. 
// NOTE: Please keep ALL of these methods in alphabetical order. Also, it is 
// IMPERATIVE to keep the semantics completely consistent with csound.hpp.

class PUBLIC CsoundOboe : public CsoundThreaded, public oboe::AudioStreamCallback
{
public:
    virtual ~CsoundOboe()
    {
    }
    
    CsoundOboe()
    {
        stabilized_callback = new oboe::StabilizedCallback(this);
        internal_reset();
    }
    
    virtual void internal_reset() {
        timeout_nanoseconds = 1000000;
        frames_per_kperiod = 0;
        is_playing = false;
        audio_stream_in = nullptr;
        spin = nullptr;
        input_channel_count = 0;
        spin_size = 0;
        spout = nullptr;
        output_channel_count = 0;
        spout_size = 0;
        audio_stream_out = nullptr;
        zero_dbfs = 32767.;
    }
    
    void oboeStopThreadRoutine()
    {
        if (audio_stream_out != nullptr) {
            audio_stream_out->close();
        }
    }
    
    /** 
     * This callback is used for both audio input from Oboe, and audio output 
     * to Oboe. We do not assume that Csound and Oboe use the same size of 
     * buffers, that the input uses the same number of channels as the output, 
     * or that the Oboe input callbacks perfectly interleave with the Oboe 
     * output callbacks. The mechanism is simple: when Oboe has processed 
     * all the frames from one Csound kperiod, the Csound frame index is reset 
     * to zero, and PerformKsmps is called again.
     */
    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboe_stream,
            void *oboe_buffer,
            int32_t frames_per_oboe_buffer)
    {
        int csound_result = 0;
        if (!is_thread_affinity_set) {
            setThreadAffinity();
        }
        int16_t *short_buffer = nullptr;
        float *float_buffer = nullptr;
        if (oboe_audio_format == oboe::AudioFormat::Float) {
            float_buffer = static_cast<float *>(oboe_buffer);
        } else {
            short_buffer = static_cast<int16_t *>(oboe_buffer);
        }
        if (oboe_stream->getDirection() == oboe::Direction::Input) {
            // Enqueue input samples to the audio input FIFO.
            // The FIFO enables the input callbacks to lag or 
            // lead the output callbacks.
            if (is_playing == false) {
                return oboe::DataCallbackResult::Stop;
            }
            if (input_channel_count > 0 && audio_stream_in) {
                if (frames_per_oboe_buffer > 0) {
                    if (oboe_audio_format == oboe::AudioFormat::Float) {
                        for (int oboe_frame_index = 0; oboe_frame_index < frames_per_oboe_buffer; oboe_frame_index++) {
                            for (int input_channel_index = 0; input_channel_index < input_channel_count; input_channel_index++) {
                                float sample = float_buffer[oboe_frame_index * input_channel_count + input_channel_index] * zero_dbfs;
                                audio_input_fifo.enqueue(sample);
                            }
                        }
                    } else {
                        for (int oboe_frame_index = 0; oboe_frame_index < frames_per_oboe_buffer; oboe_frame_index++) {
                            for (int input_channel_index = 0; input_channel_index < input_channel_count; input_channel_index++) {
                                float sample = short_buffer[oboe_frame_index * input_channel_count + input_channel_index];
                                audio_input_fifo.enqueue(sample);
                            }
                        }
                    }
                }
            }
         } else {
            for (int oboe_frame_index = 0; oboe_frame_index < frames_per_oboe_buffer; oboe_frame_index++) {
                if (audio_stream_in != nullptr) {
                    for (int input_channel_index = 0; input_channel_index < input_channel_count; input_channel_index++) {
                        float sample = 0;
                        audio_input_fifo.try_dequeue(sample);
                        spin[csound_frame_index * input_channel_count + input_channel_index] = sample;
                    }
                }
                if (audio_stream_out != nullptr) {
                    for (int output_channel_index = 0; output_channel_index < output_channel_count; output_channel_index++) {
                        if (oboe_audio_format == oboe::AudioFormat::Float) {
                            float_buffer[oboe_frame_index * output_channel_count + output_channel_index] = spout[csound_frame_index * output_channel_count + output_channel_index] / zero_dbfs;
                        } else {
                            short_buffer[oboe_frame_index * output_channel_count + output_channel_index] = spout[csound_frame_index * output_channel_count + output_channel_index];
                        }
                    }
                }
                csound_frame_index++;
                if (csound_frame_index >= frames_per_kperiod) {
                    csound_frame_index = 0;
                    // Copied from CsoundThreaded::PerformRoutine.
                    // Dispatch pending real-time events.
                    CsoundEvent *event = 0;
                    while (event_input_fifo.try_dequeue(event)) {
                        (*event)(csound);
                        delete event;
                    }
                    // Call any user-defined kperiod callback.
                    if (kperiod_callback != nullptr) {
                        kperiod_callback(csound, kperiod_callback_user_data);
                    }
                    // Compute the next low-level buffer of audio.
                    perform_ksmps_result = PerformKsmps();
                    // Shut down both Oboe and Csound if the Csound performance has ended.
                    if (perform_ksmps_result != 0) {
                        Message("CsoundOboe::onAudioReady: Csound::PerformKsmps ended with %d...\n", perform_ksmps_result);
                        is_playing = false;
                        ClearQueue();
                        csound_result = Cleanup();
                        Reset();
                        oboe_stop_thread = std::thread(&CsoundOboe::oboeStopThreadRoutine, this);
                        return oboe::DataCallbackResult::Stop;
                    }
                }
            }
        }
        return oboe::DataCallbackResult::Continue;
    }
    
    virtual int Cleanup(){
        return CsoundThreaded::Cleanup();
    }
    virtual int cleanup(){
        return Cleanup();
    }
    
    virtual int CompileCsd(const char *csd){
        return CsoundThreaded::CompileCsd(csd);
    }
    virtual int compileCsd(const char *csd){
        return CompileCsd(csd);
    }

    virtual int CompileCsdText(const char *csd_text){
        return CsoundThreaded::CompileCsdText(csd_text);
    }
    virtual int compileCsdText(const char *csd_text){
        return CompileCsdText(csd_text);
    }
    
    virtual int CompileOrc(const char *str){
        return CsoundThreaded::CompileOrc(str);
    }
    virtual int compileOrc(const char *str){
        return CompileOrc(str);
    }

    virtual MYFLT EvalCode(const char *str){
        return CsoundThreaded::EvalCode(str);
    }
    virtual MYFLT evalCode(const char *str){
        return EvalCode(str);
    }

    virtual MYFLT Get0dBFS(){
        return CsoundThreaded::Get0dBFS();
    }
    virtual MYFLT get0dBFS(){
        return Get0dBFS();
    }

    virtual int GetAPIVersion(){
        return CsoundThreaded::GetAPIVersion();
    }
    virtual int getAPIVersion(){
        return GetAPIVersion();
    }

    virtual void GetAudioChannel(const char *name, MYFLT *samples){
        CsoundThreaded::GetAudioChannel(name,samples);
    }
    virtual void getAudioChannel(const char *name, MYFLT *samples){
        GetAudioChannel(name,samples);
    }

    virtual CSOUND *GetCsound(){
        return CsoundThreaded::GetCsound();
    }
    virtual CSOUND *getCsound(){
        return GetCsound();
    }

    virtual MYFLT GetChannel(const char *name){
        return CsoundThreaded::GetControlChannel(name, 0);
    }
    virtual MYFLT getChannel(const char *name){
        return GetControlChannel(name, 0);
    }

    virtual MYFLT GetControlChannel(const char *name){
        return CsoundThreaded::GetControlChannel(name, 0);
    }
    virtual MYFLT getControlChannel(const char *name){
        return GetControlChannel(name, 0);
    }

    virtual long GetCurrentTimeSamples(){
        return CsoundThreaded::GetCurrentTimeSamples();
    }
    virtual long getCurrentTimeSamples(){
        return GetCurrentTimeSamples();
    }

    virtual const char *GetEnv(const char *name){
        return CsoundThreaded::GetEnv(name);
    }
    virtual const char *getEnv(const char *name){
        return GetEnv(name);
    }

    virtual MYFLT GetKr(){
        return CsoundThreaded::GetKr();
    }
    virtual MYFLT getKr(){
        return GetKr();
    }

    virtual int GetKsmps(){
        return CsoundThreaded::GetKsmps();
    }
    virtual int getKsmps(){
        return GetKsmps();
    }

    virtual int GetMessageLevel(){
        return CsoundThreaded::GetMessageLevel();
    }
    virtual int getMessageLevel(){
        return GetMessageLevel();
    }

    virtual int GetNchnls(){
        return CsoundThreaded::GetNchnls();
    }
    virtual int getNchnls(){
        return GetNchnls();
    }

    virtual int GetNchnlsInput(){
        return CsoundThreaded::GetNchnlsInput();
    }
    virtual int getNchnlsInput(){
        return GetNchnlsInput();
    }

    virtual const char *GetOutputName(){
        return CsoundThreaded::GetOutputName();
    }
    virtual const char *getOutputName(){
        return GetOutputName();
    }

    virtual MYFLT GetScoreOffsetSeconds(){
        return CsoundThreaded::GetScoreOffsetSeconds();
    }
    virtual MYFLT getScoreOffsetSeconds(){
        return GetScoreOffsetSeconds();
    }

    virtual double GetScoreTime(){
        return CsoundThreaded::GetScoreTime();
    }
    virtual double getScoreTime(){
        return GetScoreTime();
    }

    virtual MYFLT GetSr(){
        return CsoundThreaded::GetSr();
    }
    virtual MYFLT getSr(){
        return GetSr();
    }

    virtual void GetStringChannel(const char *name, char *string){
        CsoundThreaded::GetStringChannel(name,string);
    }
    virtual void getStringChannel(const char *name, char *string){
        GetStringChannel(name,string);
    }

    virtual int GetVersion(){
        return CsoundThreaded::GetVersion();
    }
    virtual int getVersion(){
        return GetVersion();
    }

    virtual void InputMessage(const char *message){
        CsoundThreaded::InputMessage(message);
    }
    virtual void inputMessage(const char *message){
        InputMessage(message);
    }

    virtual int IsScorePending(){
        return CsoundThreaded::IsScorePending();
    }
    virtual int isScorePending(){
        return IsScorePending();
    }

    virtual void KeyPressed(char c){
        CsoundThreaded::KeyPressed(c);
    }
    virtual void keyPressed(char c){
        KeyPressed(c);
    }

#if defined(SWIG)
%proxycode %{
    @JavascriptInterface
    public void Message(String text) {
        message(text);
    }
%}
#endif
    virtual void message(const char *text){
        Message(text);
    }
    
   /**
     * When Oboe is driving the performance, this is a dummy;
     * otherwise, Csound runs in a separate thread of execution.
     */
    virtual int Perform()
    {
        Message("CsoundOboe::Perform...\n");
        if (audio_stream_out != nullptr) {
            return 0;
        }
        if (audio_stream_in != nullptr) {
            return 0;
        }
        return CsoundThreaded::Perform();
    }
    virtual int perform(){
        return Perform();
    }
    
    virtual int PerformAndReset()
    {
        Message("CsoundOboe::PerformAndReset...\n");
        if (audio_stream_out != nullptr) {
            return 0;
        }
        if (audio_stream_in != nullptr) {
            return 0;
        }
        return CsoundThreaded::PerformAndReset();
    }
    virtual int performAndReset()
    {
        return PerformAndReset();
    }

    void Play() {
    }
    void play() {
    }
        
    virtual int PvsinSet(const PVSDATEXT* value, const char *name){
        return CsoundThreaded::PvsinSet(value, name);
    }
    virtual int pvsinSet(const PVSDATEXT* value, const char *name){
        return PvsinSet(value, name);
    }

    virtual int PvsoutGet(PVSDATEXT* value, const char *name){
        return CsoundThreaded::PvsoutGet(value, name);
    }
    virtual int pvsoutGet(PVSDATEXT* value, const char *name){
        return PvsoutGet(value, name);
    }

    virtual int ReadScore(const char *str) {
        return CsoundThreaded::ReadScore(str);
    }
    virtual int readScore(const char *str) {
        return ReadScore(str);
    }
    
    virtual void Reset(){
        CsoundThreaded::Reset();
    }
    virtual void reset(){
        Reset();
    }

    virtual void RewindScore(){
        CsoundThreaded::RewindScore();
    }
    virtual void rewindScore(){
        RewindScore();
    }

    virtual int RunUtility(const char *name, int argc, char **argv)
    {
        return CsoundThreaded::RunUtility(name, argc, argv);
    }
    virtual int runUtility(const char *name, int argc, char **argv)
    {
        return RunUtility(name, argc, argv);
    }

    virtual int ScoreEvent(char type, const MYFLT *pFields, long numFields){
        return CsoundThreaded::ScoreEvent(type, pFields, numFields);
    }
    virtual int scoreEvent(char type, const MYFLT *pFields, long numFields){
        return ScoreEvent(type, pFields, numFields);
    }
    
    virtual void SetAudioChannel(const char *name, MYFLT *samples){
        CsoundThreaded::SetChannel(name, samples);
    }
    virtual void setAudioChannel(const char *name, MYFLT *samples){
        SetAudioChannel(name, samples);
    }

    virtual void SetChannel(const char *name, char *string){
        CsoundThreaded::SetStringChannel(name, string);
    }
    virtual void setChannel(const char *name, char *string){
        SetStringChannel(name, string);
    }

    virtual void SetChannel(const char *name, double value){
        CsoundThreaded::SetControlChannel(name, value);
    }
    virtual void setChannel(const char *name, double value){
        setChannel(name, value);
    }

    virtual void SetControlChannel(const char *name, double value){
        CsoundThreaded::SetControlChannel(name, value);
    }
    virtual void setControlChannel(const char *name, double value){
        SetControlChannel(name, value);
    }

    virtual void SetInput(const char *name){
        CsoundThreaded::SetInput(name);
    }
    virtual void setInput(const char *name){
        SetInput(name);
    }

    virtual int SetGlobalEnv(const char *name, const char *value){
        return CsoundThreaded::SetGlobalEnv(name, value);
    }
    virtual int setGlobalEnv(const char *name, const char *value){
        return SetGlobalEnv(name, value);
    }
    
    virtual void SetMessageLevel(int messageLevel){
        CsoundThreaded::SetMessageLevel(messageLevel);
    }
    virtual void setMessageLevel(int messageLevel){
        SetMessageLevel(messageLevel);
    }

    virtual void SetMIDIFileInput(const char *name){
        CsoundThreaded::SetMIDIFileInput(name);
    }
    virtual void setMIDIFileInput(const char *name){
        SetMIDIFileInput(name);
    }

    virtual void SetMIDIFileOutput(const char *name){
        CsoundThreaded::SetMIDIFileOutput(name);
    }
    virtual void setMIDIFileOutput(const char *name){
        SetMIDIFileOutput(name);
    }

    virtual void SetMIDIInput(const char *name){
        CsoundThreaded::SetMIDIInput(name);
    }
    virtual void setMIDIInput(const char *name){
        SetMIDIInput(name);
    }

    virtual void SetMIDIOutput(const char *name){
        CsoundThreaded::SetMIDIOutput(name);
    }
    virtual void setMIDIOutput(const char *name){
        SetMIDIOutput(name);
    }

    virtual int SetOption(const char *option){
        return CsoundThreaded::SetOption(option);
    }
    virtual int setOption(const char *option){
        return SetOption(option);
    }

    virtual void SetOutput(const char *name,const char *type,const char *format){
        CsoundThreaded::SetOutput(name, type, format);
    }
    virtual void setOutput(const char *name,const char *type,const char *format){
        SetOutput(name, type, format);
    }

    virtual void SetScoreOffsetSeconds(double time){
        CsoundThreaded::SetScoreOffsetSeconds((MYFLT) time);
    }
    virtual void setScoreOffsetSeconds(double time){
        SetScoreOffsetSeconds((MYFLT) time);
    }

    virtual void SetScorePending(int pending){
        CsoundThreaded::SetScorePending(pending);
    }
    virtual void setScorePending(int pending){
        SetScorePending(pending);
    }
    
    virtual void SetStringChannel(const char *name, char *string){
        CsoundThreaded::SetStringChannel(name, string);
    }
    virtual void setStringChannel(const char *name, char *string){
        SetStringChannel(name, string);
    }

    virtual int Start()
    {
        Message("CsoundOboe::Start...\n");
        int csound_result = 0;
        internal_reset();
        // If and only if -odac, enable host-implemented audio.
        // Need a better way to identify input and output.
        const char *output_name = GetOutputName();
        if (output_name == nullptr) {
            return -1;
        }
        std::string output_name_string = output_name;
        auto position = output_name_string.find("dac");
        if (position != std::string::npos) {
            internal_reset();
            SetHostImplementedAudioIO(1, 0);
            csound_result = Csound::Start();
            if (csound_result != 0) {
                Message("Csound::Start error: %d.\n", csound_result);
                return csound_result;
            }
            oboe::Result result;
            csound_frame_index = 0;
            zero_dbfs = Get0dBFS();
            auto kr = GetKr();
            frames_per_kperiod = GetKsmps();
            input_channel_count = GetNchnlsInput();
            output_channel_count = GetNchnls();
            spin = GetSpin();
            spout = GetSpout();            
            const char *input_name = GetInputName();
            if (input_name != nullptr) {
                std::string input_name_string = input_name;
                auto position = input_name_string.find("adc");
                if (position != std::string::npos) {
                    Message("CsoundOboe::Start: Creating Oboe input stream stream...\n");
                    float sample = 0;
                    while(audio_input_fifo.try_dequeue(sample)) {};
                    input_channel_count = GetNchnlsInput();
                    spin_size = sizeof(MYFLT) * frames_per_kperiod * input_channel_count;
                    audio_stream_builder.setAudioApi(oboe_api_index);
                    audio_stream_builder.setSharingMode(oboe::SharingMode::Exclusive);
                    audio_stream_builder.setPerformanceMode(oboe::PerformanceMode::LowLatency);
                    audio_stream_builder.setCallback(stabilized_callback);
                    audio_stream_builder.setSampleRate(GetSr());
                    ///audio_stream_builder.setFramesPerCallback(frames_per_kperiod);
                    audio_stream_builder.setChannelCount(input_channel_count);
                    audio_stream_builder.setDirection(oboe::Direction::Input);
                    result = audio_stream_builder.openStream(&audio_stream_in);
                    if (result != oboe::Result::OK){
                        Message("CsoundOboe::Start: Failed to create Oboe input stream. Error: %s.\n", oboe::convertToText(result));
                        return -1;
                    }
                    // We assume that Oboe's input format is always the same as
                    // its output format! But input and output may open without
                    // the other.
                    oboe_audio_format = audio_stream_in->getFormat();
                    Message("CsoundOboe::Start: Audio input stream format is: %s.\n", oboe::convertToText(oboe_audio_format));
                }
            }
            spout_size = sizeof(MYFLT) * frames_per_kperiod * output_channel_count;
            audio_stream_builder.setAudioApi(oboe_api_index);
            audio_stream_builder.setSharingMode(oboe::SharingMode::Exclusive);
            audio_stream_builder.setPerformanceMode(oboe::PerformanceMode::LowLatency);
            audio_stream_builder.setCallback(stabilized_callback);
            audio_stream_builder.setSampleRate(GetSr());
            audio_stream_builder.setBufferCapacityInFrames(frames_per_kperiod * 12);
            audio_stream_builder.setFramesPerCallback(frames_per_kperiod);           
            audio_stream_builder.setChannelCount(output_channel_count);
            audio_stream_builder.setDirection(oboe::Direction::Output);
            result = audio_stream_builder.openStream(&audio_stream_out);
            if (result != oboe::Result::OK) {
                Message("CsoundOboe::Start: Failed to create Oboe output stream. Error: %s.\n", oboe::convertToText(result));
                return -1;
            }
            bool aaudio_is_supported = audio_stream_builder.isAAudioSupported();
            Message("CsoundOboe::Start: AAudio is supported: %s.\n", aaudio_is_supported ? "true" : "false");
            bool aaudio_is_recommended = audio_stream_builder.isAAudioRecommended();
            Message("CsoundOboe::Start: AAudio is recommended: %s.\n", aaudio_is_recommended ? "true" : "false");
            // Start oboe.
            oboe_audio_format = audio_stream_out->getFormat();
            Message("CsoundOboe::Start: Audio output stream format is: %s.\n", oboe::convertToText(oboe_audio_format));
            is_playing = true;
            if(audio_stream_in != nullptr) {
                audio_stream_in->start();
                Message("CsoundOboe::Start: Started Oboe audio input stream...\n");
            }
            auto frames_per_burst = audio_stream_out->getFramesPerBurst();
            Message("CsoundOboe::Start: Frames per burst: %6d.\n", frames_per_burst);
            audio_stream_out->start();
            oboe::AudioApi audioApi = audio_stream_out->getAudioApi();
            if (audioApi == oboe::AudioApi::AAudio) {
                audio_stream_out->setBufferSizeInFrames(frames_per_burst * 6);
            } else {
               audio_stream_out->setBufferSizeInFrames(frames_per_burst * 2);
            }
            Message("CsoundOboe::Start: Oboe audio API is: %s.\n", audioApi == oboe::AudioApi::AAudio ? "AAudio" : "OpenSLES");
            Message("CsoundOboe::Start: Started Oboe audio output stream...\n");
            oboe::ResultWithValue<double> latency = audio_stream_out->calculateLatencyMillis();
            if (latency) {
                Message("CsoundOboe::Start: Output stream latency is: %9.4f milliseconds.\n", latency.value());
            }
            ///oboe_performance_thread = std::thread(&CsoundOboe::OboePerform, this);
        } else {
            csound_result = Csound::Start();
            if (csound_result != 0) {
                Message("CsoundOboe::Start returned: %d.\n", csound_result);
                return csound_result;
            }
            is_playing = true;
         }
        return 0;
    }
    virtual int start() 
    {
        return Start();
    }
    
    virtual void Stop()
    {
        Message("CsoundOboe::Stop...\n");
        if (audio_stream_in) {
            audio_stream_in->requestStop();
            audio_stream_in = nullptr;
        }
        if (audio_stream_out) {
            audio_stream_out->requestStop();
            audio_stream_out = nullptr;
        }
        Csound::Stop();
    }
    virtual void stop() 
    {
        Stop();
    }
    
    virtual int TableLength(int table){
        return CsoundThreaded::TableLength(table);
    }
    virtual int tableLength(int table){
        return TableLength(table);
    }

    virtual MYFLT TableGet(int table, int index){
        return CsoundThreaded::TableGet(table, index);
    }
    virtual MYFLT tableGet(int table, int index){
        return TableGet(table, index);
    }

    virtual void TableSet(int table, int index, double value){
        CsoundThreaded::TableSet(table, index, (MYFLT) value);
    }
    virtual void tableSet(int table, int index, double value){
        TableSet(table, index, (MYFLT) value);
    }
    
    // The methods below are not exposed in JavaScript.

    virtual void setOboeApi(int oboe_api_index_) {
        oboe_api_index = (oboe::AudioApi) oboe_api_index_;
    }
    virtual int getOboeApi() const {
        return (int) oboe_api_index;
    }
    virtual void setThreadAffinity() {
        pid_t current_thread_id = gettid();
        cpu_set_t cpu_set;
        CPU_ZERO(&cpu_set);
        // If the callback cpu ids aren't specified, then bind to the current cpu.
        if (cpu_ids.empty()) {
            int current_cpu_id = sched_getcpu();
            Message("Current CPU ID is %d.\n", current_cpu_id);
            CPU_SET(current_cpu_id, &cpu_set);
        } else {
            for (size_t i = 0; i < cpu_ids.size(); i++) {
                int cpu_id = cpu_ids.at(i);
                Message("CPU ID %d added to cores set.\n", cpu_id);
                CPU_SET(cpu_id, &cpu_set);
            }
        }
        int result = sched_setaffinity(current_thread_id, sizeof(cpu_set_t), &cpu_set);
        if (result == 0) {
            Message("Thread affinity set.\n");
        } else {
            Message("Error setting thread affinity: %d.\n", result);
        }
        is_thread_affinity_set = true;
    }    
protected:
    int timeout_nanoseconds;
    uint32_t frames_per_kperiod;
    std::atomic<bool> is_playing;
    oboe::AudioStream *audio_stream_in;
    MYFLT *spin;
    uint32_t input_channel_count;
    size_t spin_size;
    MYFLT *spout;
    uint32_t output_channel_count;
    size_t spout_size;
    oboe::AudioStream *audio_stream_out;
    oboe::AudioFormat oboe_audio_format;
    oboe::AudioStreamBuilder audio_stream_builder;
    float zero_dbfs;
    oboe::AudioApi oboe_api_index = oboe::AudioApi::Unspecified;
    bool is_thread_affinity_set = false;
    std::vector<int> cpu_ids;
    moodycamel::ConcurrentQueue<float> audio_input_fifo = moodycamel::ConcurrentQueue<float>(1000000);
    int perform_ksmps_result = 0;
    oboe::StabilizedCallback *stabilized_callback = nullptr;
    size_t csound_frame_index = 0;
    std::thread oboe_stop_thread;
 };

#endif  // __CSOUND_OBOE_HPP__
