#!/bin/bash
echo "Starting clean rebuild of native libraries for Csound for Android..."
bash clean-android.sh
bash build-android.sh
echo "Finished clean rebuild of native libraries for Csound for Android."
