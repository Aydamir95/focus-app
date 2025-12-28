# How to Build the APK

## Prerequisites
1. **Android Studio** installed (recommended)
   - Download from: https://developer.android.com/studio
   - Or use command line with Android SDK

2. **Java JDK 17** installed
   - Required for building Android apps

## Method 1: Using Android Studio (Easiest)

1. **Open the project:**
   - Open Android Studio
   - Click "Open" and select the `focus-app` folder
   - Wait for Gradle sync to complete

2. **Build the APK:**
   - Go to: **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
   - Wait for build to complete (first time may take several minutes)

3. **Find the APK:**
   - Click "locate" in the notification that appears
   - Or navigate to: `app/build/outputs/apk/debug/app-debug.apk`

## Method 2: Using Command Line

1. **Open Command Prompt or PowerShell** in the `focus-app` folder

2. **Run the build command:**
   ```bash
   gradlew.bat assembleDebug
   ```

3. **Find the APK:**
   After build completes, the APK will be at:
   ```
   C:\Users\aydam\vibe coding\focus-app\app\build\outputs\apk\debug\app-debug.apk
   ```

## Important Notes

- **First build takes longer** (downloads dependencies)
- The `build` folder is **created automatically** during build
- If build fails, check that you have:
  - Android SDK installed
  - Java JDK 17 installed
  - Internet connection (for downloading dependencies)

## After Building

Once the APK is built, you can:
1. Upload to Appetize.io for testing
2. Transfer to your Android phone and install
3. Share with others for testing



