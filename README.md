# FocusApp - Android Focus Tracking Application

## Building the APK

### Option 1: Using Android Studio (Recommended)
1. Open the project in Android Studio
2. Go to **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
3. Wait for the build to complete
4. Click **locate** in the notification, or find the APK at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

### Option 2: Using Gradle Command Line
Navigate to the project root directory and run:
```bash
./gradlew assembleDebug
```
or on Windows:
```bash
gradlew.bat assembleDebug
```

The APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## APK File Location

After building, your APK file will be stored at:
- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK** (if built): `app/build/outputs/apk/release/app-release.apk`

## Uploading to Appetize.io

1. Build the APK using one of the methods above
2. Go to https://appetize.io
3. Click "Upload" or "New App"
4. Select the APK file from: `app/build/outputs/apk/debug/app-debug.apk`
5. Wait for upload and processing
6. Test your app in the browser!

## Important Notes

Before building, make sure you have:
- Android SDK installed
- Minimum SDK 24 (Android 7.0)
- All dependencies synced (run `./gradlew build` first)

## Required Permissions Setup

The app requires these permissions that users must grant:
1. **Overlay Permission** - For blocking overlay
2. **Usage Stats Permission** - For tracking app usage
3. **Accessibility Service** - For app blocking functionality

These will be requested when the app first launches.



