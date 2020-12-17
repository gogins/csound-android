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
wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/WebAssembly/examples/csound_loader.js -O CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins/csound_loader.js
wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/docs/CsoundAC.js -O CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins/CsoundAC.js
wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/silencio/js/dat.gui.js -O CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins/js/dat.gui.js
wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/silencio/js/jquery.js -O CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins/js/jquery.js
wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/silencio/js/numeric.js -O CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins/js/numeric.js
wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/silencio/js/piano-roll.js -O CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins/js/piano-roll.js
wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/silencio/js/sprintf.js -O CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins/js/sprintf.js
wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/silencio/js/three.js -O CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins/js/three.js 
wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/silencio/js/tinycolor.js -O CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins/js/tinycolor.js
wget -L https://raw.githubusercontent.com/gogins/csound-extended/develop/silencio/js/TrackballControls.js -O CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins/js/TrackballControls.js
find CsoundForAndroid/CsoundApplication/src/main/assets/examples -name "*.js.*" -delete
find CsoundForAndroid/CsoundApplication/src/main/assets/examples -name "*.html.*" -delete
ls -ll CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins CsoundForAndroid/CsoundApplication/src/main/assets/examples/Gogins/js
echo "Finished updating all source dependencies of Csound for Android."
