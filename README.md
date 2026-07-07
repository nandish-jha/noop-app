<p align="center">
  <img src="docs/assets/logo-v3.png" alt="NOOP" width="72">
</p>

<h1 align="center">NOOP for Android</h1>

<p align="center"><sub>Personal Android fork of <a href="https://github.com/NoopApp/noop">NoopApp/noop</a> — maintained by <a href="https://github.com/nandish-jha">nandish-jha</a>. See <a href="UPSTREAM.md">UPSTREAM.md</a> to sync upstream changes.</sub></p>

<p align="center"><b>Your strap. Your data. Your phone. Offline, on-device, no cloud.</b></p>

<p align="center">
  <img alt="Platform" src="https://img.shields.io/badge/platform-Android-E8B84B?style=flat-square">
  <img alt="Local first" src="https://img.shields.io/badge/local-first-E8B84B?style=flat-square">
  <img alt="Account free" src="https://img.shields.io/badge/account-free-C8902F?style=flat-square">
  <img alt="WHOOP 4 and 5" src="https://img.shields.io/badge/works%20with-WHOOP%204.0%20%26%205.0-6B737B?style=flat-square">
  <a href="LICENSE"><img alt="License: PolyForm Noncommercial 1.0.0" src="https://img.shields.io/badge/license-PolyForm%20Noncommercial%201.0.0-6B737B?style=flat-square"></a>
  <a href="https://github.com/nandish-jha/noop-app/releases/latest"><img alt="Latest release" src="https://img.shields.io/github/v/release/nandish-jha/noop-app?style=flat-square"></a>
</p>

<p align="center">
  <a href="https://github.com/nandish-jha/noop-app/releases/latest">⬇&nbsp;Download APK</a> ·
  <a href="#build-from-source">Build</a> ·
  <a href="docs/ANDROID.md">Android guide</a> ·
  <a href="docs/PROTOCOL.md">Protocol</a> ·
  <a href="UPSTREAM.md">Upstream sync</a>
</p>

---

## Download

Get the latest release APK from **[Releases](https://github.com/nandish-jha/noop-app/releases/latest)** — files are named `NOOP-full-v<version>.apk` (e.g. `NOOP-full-v8.2.3.apk`).

See **[RELEASE_NOTES.md](RELEASE_NOTES.md)** for a full list of fork customizations in this build.

| Requirement | Details |
|---|---|
| **Android** | 8.0+ (`minSdk 26`) |
| **Install** | Sideload — enable "Install unknown apps" for your browser or file manager |
| **Play Protect** | Release APKs are signed with the project's upload key. You may still see a one-time "Scan app?" prompt on first install — tap **Install without scanning** / **Install anyway**, or disable **Play Store → Play Protect → Settings → Scan apps with Play Protect** while installing |

> **Not affiliated with WHOOP.** Independent, experimental software. See [DISCLAIMER.md](DISCLAIMER.md).

---

## Features

NOOP pairs with a WHOOP 4.0 / 5.0 / MG strap over Bluetooth — no WHOOP subscription, no cloud account. Live heart rate, history sync, and on-device Charge / Effort / Rest scores, stored only on your phone.

See [docs/FEATURES.md](docs/FEATURES.md) for the full feature list.

---

## Build from source

**Prerequisites:** JDK 17, Android SDK 34 (see [docs/ANDROID.md](docs/ANDROID.md)).

```bash
cd android
# Create local.properties with: sdk.dir=/path/to/Android/sdk
./gradlew assembleFullRelease
```

Output: `android/app/build/outputs/apk/full/release/NOOP-full-v<version>.apk`

Debug build (installable via USB):

```bash
./gradlew installFullDebug
```

---

## Project layout

```
android/          # Android app (Kotlin, Jetpack Compose, Room)
docs/             # Protocol, architecture, Android build notes
```

All Android application code lives under `android/app/src/main/java/com/noop/`.

---

## License & attribution

Licensed under **PolyForm Noncommercial 1.0.0** — see [LICENSE](LICENSE) and [ATTRIBUTION.md](ATTRIBUTION.md). Original copyright: **NoopApp**.

Upstream project: [github.com/NoopApp/noop](https://github.com/NoopApp/noop)
