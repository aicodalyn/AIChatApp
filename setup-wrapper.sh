#!/bin/bash

echo "========================================"
echo " Gradle Wrapper Setup Script"
echo "========================================"
echo ""

# Check if gradle-wrapper.jar already exists
if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "gradle-wrapper.jar already exists!"
    exit 0
fi

echo "Downloading gradle-wrapper.jar..."
echo ""

# Try using curl
curl -L -o "gradle/wrapper/gradle-wrapper.jar" "https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar"

if [ $? -eq 0 ]; then
    echo "Successfully downloaded gradle-wrapper.jar"
    exit 0
fi

echo "curl failed, trying wget..."
wget -O "gradle/wrapper/gradle-wrapper.jar" "https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar"

if [ $? -eq 0 ]; then
    echo "Successfully downloaded gradle-wrapper.jar"
    exit 0
fi

echo ""
echo "ERROR: Could not download gradle-wrapper.jar"
echo ""
echo "Please download it manually or run: gradle wrapper"
echo ""
exit 1
