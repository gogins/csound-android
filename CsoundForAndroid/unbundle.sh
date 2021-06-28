#!/bin/bash
echo "Unbundling and signing apks..."

rm -rf csound-for-android.apks
java -jar ~/Downloads/bundletool-all-1.4.0.jar build-apks --bundle=CsoundForAndroid/CsoundApplication/release/CsoundApplication-release.aab --output=csound-for-android.apks --ks=CsoundForAndroid/CsoundForAndroid.jks --ks-pass=file:CsoundForAndroid.pwd --ks-key-alias=CsoundApplication --key-pass=file:CsoundForAndroid.key

echo "Finished unbundling and signing apks."