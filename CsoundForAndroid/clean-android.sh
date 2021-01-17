#!/bin/bash
echo "Cleaning all Linux artifacts from `pwd`..."
find .. -name "*.o" -delete
find -name "*.a" -delete
find -name "*.so" -delete
cd ..
