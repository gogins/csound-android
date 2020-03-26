#!/bin/bash
echo "Cleaning all Linux artifacts from `pwd`..."
find .. -name "*.o" -delete
find ndk-libraries -name "*.a" -delete
find ndk-libraries -name "*.so" -delete
cd ..
