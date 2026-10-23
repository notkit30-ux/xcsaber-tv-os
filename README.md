# XCSABER TV OS

An ad-free, privacy-first home screen (launcher) for Android TV, built for Android 9 (API 28) TVs.

- **No ads, no tracking, no analytics.**
- **Requests zero permissions**, including internet. The launcher cannot phone home.
- Works with a normal TV remote (D-pad, OK, Back, Menu).

## What's on the home screen

| Section | What it does |
| --- | --- |
| Clock and date | Live clock using the TV's own time zone and 12/24-hour setting |
| Quick access | **Browser** (opens your XCSABER browser app if installed, otherwise any browser), **All apps**, **Wi-Fi**, **Settings** |
| Favorites | Apps you pinned |
| Recent | The last apps you opened |

**All apps** shows every app on the TV (TV apps with their banners, phone-style apps with their icons).
Hold **OK** (or press **Menu**) on any app to pin it to Favorites, hide it, or open its App info page.

**Settings** has one-press shortcuts to Wi-Fi, Bluetooth, Display, Sound, app manager and the TV's own settings,
plus "Show hidden apps" and "Clear recent apps".

### The Browser tile

It looks for an installed app whose name contains "XCSABER" (or whose package starts with `com.xcsaber`)
and opens it. Install your XCSABER browser APK on the TV and the tile will find it automatically.

## Build the APK (no computer needed)

1. Open this repository on GitHub and tap the **Actions** tab.
2. Choose **Build XCSABER TV OS APK**, then **Run workflow**. (It also runs on every push to `main`.)
3. Wait for the green check, open the run, and download **XCSABER-TV-OS-APK** from *Artifacts*.
4. Unzip it to get `app-debug.apk`.

## Install on the TV

Pick whichever is easiest:

- **USB drive:** copy `app-debug.apk` to a USB stick, plug it into the TV, and open it with a file manager app.
- **Send Files to TV** (free app on Play Store, on both your phone and the TV): send the APK from your phone.
- **ADB over Wi-Fi:** `adb connect <tv-ip>` then `adb install app-debug.apk`.

You may need to allow *Install unknown apps* for the file manager you use.

## Make it your home screen

1. Open **XCSABER TV OS** from the TV's app list once.
2. Press **Home** on the remote. Android asks which home app to use; choose **XCSABER TV OS** and **Always**.
   If it doesn't ask, go to **Settings > Choose your home screen** inside XCSABER TV OS.

Some TV makers lock the home screen. If yours does, XCSABER TV OS still works as an app you can open from the app list.

To go back to the original home screen, open the TV's Settings > Apps > XCSABER TV OS and clear defaults.

## Project layout

```
app/src/main/java/com/xcsaber/tv/
  MainActivity.kt       Home screen (HOME launcher)
  AllAppsActivity.kt    All apps grid
  SettingsActivity.kt   Launcher options and system shortcuts
  AppRepository.kt      Finds installed apps (on-device only)
  LauncherPrefs.kt      Favorites, hidden and recent apps (private storage)
  TileAdapter.kt        Focusable cards for apps and shortcuts
  AppActions.kt         Open / pin / hide / app info
  SystemSettings.kt     Safe launcher for system settings screens
.github/workflows/build-apk.yml   Cloud build for the debug APK
```

## Tech

Kotlin, Gradle (Kotlin DSL), AndroidX RecyclerView. `minSdk 23`, `targetSdk 28`, `compileSdk 35`.
