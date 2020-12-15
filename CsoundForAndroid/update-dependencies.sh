#!/bin/bash
echo "Updating all source dependencies of Csound for Android..."
git submodule update --init --recursive
git submodule update --recursive --remote
git submodule status --recursive
git pull --recurse-submodules
git branch
echo "Finished updating all submodules for CsoundForAndroid."
ls -ll
echo "Downloading some examples and WebAssemby files from the csound-extended repository..."
wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/WebAssembly/examples/parachronic.html -P CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins
wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/WebAssembly/examples/scrims.html -P CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins
wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/WebAssembly/examples/csound_loader.js -P CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins
wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/docs/CsoundAC.js -P CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins
# wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/WebAssembly/examples/CsoundAudioNode.js -P CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins
# wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/WebAssembly/examples/CsoundAudioProcessor.js -P CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins
# wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/WebAssembly/examples/CsoundAudioProcessor.wasm -P CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins
# wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/WebAssembly/examples/CsoundAudioProcessor.wasm.map -P CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins
wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/WebAssembly/examples/piano-roll.js -P CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins
ls -ll CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins
echo "Finished updating all source dependencies of Csound for Android."
