$workflowDir = ".github\workflows"

if (!(Test-Path $workflowDir)) {
    New-Item -ItemType Directory -Path $workflowDir -Force
}

$yaml = @'
name: Build APK

on:
  workflow_dispatch:

jobs:
  build:

    runs-on: ubuntu-latest

    steps:

      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17

      - uses: android-actions/setup-android@v3

      - uses: gradle/actions/setup-gradle@v4

      - name: Grant Permission
        run: chmod +x gradlew

      - name: Accept Licenses
        run: yes | sdkmanager --licenses

      - name: Install SDK
        run: |
          sdkmanager \
          "platform-tools" \
          "platforms;android-35" \
          "build-tools;35.0.0"

      - name: Build
        run: ./gradlew assembleDebug --stacktrace

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: apk
          path: app/build/outputs/apk/debug/*.apk
'@

Set-Content ".github\workflows\android.yml" $yaml

Write-Host ""
Write-Host "Workflow created:"
Write-Host ".github/workflows/android.yml"
Write-Host ""
Write-Host "Next steps:"
Write-Host "1. Upload project to GitHub"
Write-Host "2. Open Actions tab"
Write-Host "3. Run workflow"
Write-Host "4. Download APK from Artifacts"