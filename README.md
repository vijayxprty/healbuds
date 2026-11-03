# 👟 StepPulse — Free & Battery-Friendly Android Step Tracker

[![Build APK](https://github.com/vijayxprty/step-tracker/actions/workflows/build-and-release.yml/badge.svg)](https://github.com/vijayxprty/step-tracker/actions/workflows/build-and-release.yml)
[![Release](https://img.shields.io/github/v/release/vijayxprty/step-tracker?color=emerald&label=Release)](https://github.com/vijayxprty/step-tracker/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A **100% free**, open-source, privacy-first Android pedometer application built with **Kotlin** and **Jetpack Compose**. StepPulse uses the device's hardware step sensor (`Sensor.TYPE_STEP_COUNTER`) to deliver accurate step counting without background GPS drain or cloud server dependency.

🌐 **Public Landing Page**: [https://vijayxprty.github.io/step-tracker/](https://vijayxprty.github.io/step-tracker/)  
📥 **Download Latest APK**: [GitHub Releases](https://github.com/vijayxprty/step-tracker/releases/latest)

---

## ✨ Features

- **🔋 Zero GPS Battery Drain**: Uses hardware step-counter sensor coprocessor that listens at hardware level.
- **🛡️ 100% Offline & Private**: Zero tracking, zero analytics, zero network requests. Your data never leaves your device.
- **🔄 Auto Midnight Rollover**: Automatically commits the day's record to the local Room database at 12:00 AM and resets daily progress.
- **📊 Real-Time Metrics**:
  - Live Step Count & Percentage towards Daily Goal.
  - Estimated Distance Walked (km).
  - Estimated Calories Burned (kcal).
  - Active Walking Time (minutes).
  - 7-Day History Activity Bar Chart.
- **🔔 Foreground Health Notification**: Shows ongoing live steps, goal percentage, and quick metrics directly in your status bar.
- **🎯 Customizable Goals**: Quickly change your daily target (5,000 / 8,000 / 10,000 / 12,000 / 15,000 steps).
- **🚀 Reboots Supported**: Automatically recalibrates sensor offsets and restarts on phone reboot.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 1.9.24
- **UI Framework**: Jetpack Compose (Material 3)
- **Local Persistence**: Room SQLite Database + SharedPreferences
- **Sensors**: Android `SensorManager` (`Sensor.TYPE_STEP_COUNTER` with `Sensor.TYPE_STEP_DETECTOR` fallback)
- **Background Engine**: Android Foreground Service (`foregroundServiceType="health"`)
- **CI/CD**: GitHub Actions (auto-compiles APK and attaches to GitHub Releases on push)
- **Hosting**: GitHub Pages for public landing page

---

## 📱 How to Install the APK

1. Go to the [Releases page](https://github.com/vijayxprty/step-tracker/releases/latest) or download directly from the [Landing Page](https://vijayxprty.github.io/step-tracker/).
2. Download `app-debug.apk`.
3. When prompted by Android with *"File might be harmful"*, tap **Download anyway** (standard Android security prompt for direct APKs).
4. Tap the downloaded file and choose **Install** (enable *"Allow from this source"* if requested).
5. Open **StepPulse** and grant the *"Physical Activity"* permission.

---

## 💻 Building from Source

```bash
# Clone the repository
git clone https://github.com/vijayxprty/step-tracker.git
cd step-tracker

# Build the Debug APK
./gradlew assembleDebug

# Output APK location:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 License
This project is licensed under the MIT License.
