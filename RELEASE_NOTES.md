# Release notes — nandish-jha/noop-app fork

**Latest:** [v8.2.3](https://github.com/nandish-jha/noop-app/releases/tag/v8.2.3)

---

## v8.2.3

- **Strap battery:** Polls battery on connect, every ~45s while linked, and on keep-alive for both WHOOP 4 and 5/MG. UI keeps the last known % while connected or bonded (Today header, Settings, Devices).
- **App icon:** Boop-style thin circle (2dp) with angular **N** monogram.
- **Release APK:** Files are now named `NOOP-full-v8.2.3.apk` (version in the filename). Signed with a dedicated upload key so Play Protect is less likely to flag sideload installs — if prompted, tap **Install without scanning**.

---

## v8.2.2 — Fork customizations

### Platform

- **Android-only:** Removed iOS, macOS, watchOS, and Swift `Packages/` code. The repo builds and ships a single Android APK.
- **GitHub release:** `NOOP-full.apk` published at [v8.2.2](https://github.com/nandish-jha/noop-app/releases/tag/v8.2.2).

### Theme & visual design (Boop-inspired)

- **AMOLED dark theme:** True black (`#000000`) canvas with monochromatic white/gray accents (`#F0F0F0`), matching the Boop app palette.
- **Day-cycle background:** Disabled everywhere; Today uses a flat black canvas.
- **Charts:** Classic style only; Titanium chart option removed from Settings.
- **App icon:** Heart-shaped outline (Boop-style minimal stroke) replacing the WHOOP-like green ring / “N” monogram.
- **Bottom dock:** Boop-style layout — pill-shaped tab cluster plus a separate circular quick-action (+) button.
- **Motion:** Spring press feedback on dock tabs, collapsible Settings sections animate open/close, and existing tab crossfades retained.

### Today screen

- **Header:** Fixed clipped title/date; removed decorative heart from the header cluster.
- **Quick actions (+):** Moved from the Today header to the centre of the bottom dock.
- **Hydration card:** Enabled by default — hydration tracking defaults to ON and the Hydration dashboard card is included in the default “Your cards” selection.

### Settings

- **Collapsible sections:** Each settings card is now a dropdown (tap header to expand/collapse) instead of always taking full height.
- **Profile photo:** “Change photo” / “Remove photo” buttons stack full-width so label text is no longer clipped.

### Removed UI & references

- **Support / donations:** Donation nudge, Support card on Today, Support screen, and Support & contact in About removed.
- **Apple Health:** Apple Health navigation entry and screen removed from the app shell (Health Connect data paths remain for users who already imported).
- **WHOOP 4.0-specific UI:** Model comparison sheet, steps-estimate calibration flow, strap rename (4.0-only), and “WHOOP 4.0 protocol” attribution removed.

### Documentation

- `UPSTREAM.md` — how to sync from [NoopApp/noop](https://github.com/NoopApp/noop).
- `README.md` — Android-only fork notice and download links.

---

## Build

```bash
export JAVA_HOME=$HOME/.local/opt/jdk-17.0.19+10
export ANDROID_HOME=$HOME/.local/opt/android-sdk
cd android && ./gradlew assembleFullRelease
```

Output: `android/app/build/outputs/apk/full/release/NOOP-full-v<version>.apk`
