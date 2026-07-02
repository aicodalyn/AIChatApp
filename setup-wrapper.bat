@echo off
echo ========================================
echo  Gradle Wrapper Setup Script
echo ========================================
echo.

REM Check if gradle-wrapper.jar already exists
if exist "gradle\wrapper\gradle-wrapper.jar" (
    echo gradle-wrapper.jar already exists!
    goto :end
)

echo Downloading gradle-wrapper.jar...
echo.

REM Try using curl
curl -L -o "gradle\wrapper\gradle-wrapper.jar" "https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar"

if %ERRORLEVEL% equ 0 (
    echo Successfully downloaded gradle-wrapper.jar
    goto :end
)

echo curl failed, trying PowerShell...
powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'"

if %ERRORLEVEL% equ 0 (
    echo Successfully downloaded gradle-wrapper.jar
    goto :end
)

echo.
echo ERROR: Could not download gradle-wrapper.jar
echo.
echo Please download it manually from:
echo https://services.gradle.org/distributions/gradle-8.9-bin.zip
echo.
echo Or run: gradle wrapper
echo.

:end
echo.
echo Setup complete!
echo.
pause
