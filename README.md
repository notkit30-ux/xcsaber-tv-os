# XCSABER TV OS

A lightweight, ad-free Android TV **launcher** for Android 9-compatible devices. This is a separate project from XCSABER Browser and does not replace the TV's underlying firmware.

## Features
- Clean dark navy interface with cyan focus states
- Quick access to XCSABER Browser, installed apps, and Android Settings
- Automatically lists apps available through Android's launcher intent
- Standard D-pad / remote focus navigation
- No ad SDK, recommendation feed, autoplay banner, or analytics code
- GitHub Actions workflow to build a debug APK in the cloud

## Compatibility
- Minimum Android API: 23
- Target API: 28
- Compile SDK: 35
- Java: 17
- Real MME Android 9 TV testing: not yet performed; vendor firmware and remote behavior may vary.

## Build APK on GitHub using a phone
1. Open the **Actions** tab in this repository.
2. Select **Build XCSABER TV OS APK**.
3. Tap **Run workflow** if it has not started automatically.
4. Open the completed run and download the **xcsaber-tv-os-debug-apk** artifact.
5. Extract the ZIP and install the APK on your TV. Allow installation from that source if Android asks.

The workflow also runs automatically after a push to main.

## Important safety note
This project is a normal Android launcher APK, not a custom ROM or firmware replacement. To return to the original home screen, use Android Settings to choose the original launcher/default Home app or uninstall XCSABER TV OS.

## Browser shortcut
The launcher looks for package ID **com.xcsaber.app**. If your browser uses a different application ID, update the package lookup in MainActivity.kt.
