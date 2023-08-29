# csound-android
![GitHub All Releases (total)](https://img.shields.io/github/downloads/gogins/csound-android/total.svg)<br>
Michael Gogins<br>
https://github.com/gogins<br>
http://michaelgogins.tumblr.com

## Introduction

This is the Android for Csound app, release code 35. It is the first major 
release of this app since 2021.

The Csound for Android app now targets Android version 13. This has required 
adding `MANAGE_EXTERNAL_STORAGE` permission, so that the app can now read and 
write all files on a device. Be assured that the app does not, on its own, 
read any private data or send any private data anywhere, although a user could 
write a .csd file that could do these things.

Android versions from 10 forward are still supported.

### Upgraded libraries

* Csound has been upgraded to version 6.18.1.
* The Ace Editor has been upgraded to version 1.24.1. 
* Various plugin opcode libraries have been upgraded.

### Enhanced Functionality

* The widgets are now arranged with one button to the left of each slider, 
  and the number of buttons and sliders has been increased to 12 each.
* The code editor now has visible scrollbars, faster scrolling, and a context 
  menu for search and replace, configuring the editor, etc.
* The ***User Guide*** has been improved with instructions for using an 
  Android emulator to run the app on notebooks or desktops.

### Bug Fixes

* The app no longer crashes when run without first loading a .csd file.
* The broken link to the online ***Csound Reference Manual*** has been fixed.

### Downgraded Functionality

* The Ableton Link opcodes have been removed.

