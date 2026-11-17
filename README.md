# Insta Fullscreen — No Reels

A fullscreen Android WebView app for Instagram with best-effort Reels hiding. This is not affiliated with Instagram/Meta.

## Build an APK automatically on GitHub (no Android Studio)

1. Create a new **private** repository at https://github.com/new (do not select a README).
2. Upload **the contents of this project folder** to the repository root. Keep the `.github/workflows/build-apk.yml` file and its folders; GitHub's web uploader may hide dot-folders on some devices, so use GitHub Desktop or git if needed.
3. Open the repository's **Actions** tab. If asked, enable workflows.
4. Choose **Build Android APK** and click **Run workflow** (or push to the `main` branch to trigger it automatically).
5. Once the run is green, open it and download the **InstaFullscreen-NoReels-APK** artifact from the Artifacts section.
6. Extract the downloaded artifact ZIP and install `app-debug.apk` on Android. Allow installs from your file manager if prompted.

The debug APK is signed with the standard Android debug key and is for personal testing, not Play Store distribution. Subsequent GitHub Actions runs may use different debug keys, so reinstalling may require removing the previous version (which clears its app data).

## Alternative local build

With Android SDK and Gradle 8.10.2 installed, run `gradle :app:assembleDebug` from this folder. The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Limitations

- Reels links and some Reel cards are hidden; Instagram can change its web UI and break filters.
- Instagram may reject login inside Android WebView.
- Other video posts may still appear.
