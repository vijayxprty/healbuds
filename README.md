# 🌱 Healbuds — Free, Private & Battery-Friendly Android Lifestyle Platform

[![Build APK](https://github.com/vijayxprty/healbuds/actions/workflows/build-and-release.yml/badge.svg)](https://github.com/vijayxprty/healbuds/actions/workflows/build-and-release.yml)
[![Release](https://img.shields.io/github/v/release/vijayxprty/healbuds?color=emerald&label=Release)](https://github.com/vijayxprty/healbuds/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A **100% free**, open-source, privacy-first Android health & lifestyle platform built with **Kotlin** and **Jetpack Compose**. Healbuds pairs a zero-battery hardware pedometer (`Sensor.TYPE_STEP_COUNTER` with 10-step cadence filter) with an authentic interactive Indian Thali diet planner, 5-meal nutrition coaching (Lose/Gain weight recommendations), custom food builder with emojis, smart streaks with 2 allowed rest days/week, hydration alarms, and an adorable mascot companion.

🌐 **Public Landing Page**: [https://vijayxprty.github.io/healbuds/](https://vijayxprty.github.io/healbuds/)  
🎮 **Live Web Prototype**: [https://vijayxprty.github.io/healbuds/demo.html](https://vijayxprty.github.io/healbuds/demo.html)  
📥 **Direct APK Download**: [https://vijayxprty.github.io/healbuds/Healbuds-v2.1.0.apk](https://vijayxprty.github.io/healbuds/Healbuds-v2.1.0.apk)

---

## ✨ Key Features

- **👟 Hardware Sensor Engine**: Android `TYPE_STEP_COUNTER` hardware pedometer with 10-step cadence filtering and vehicle vibration rejection. Zero background GPS battery drain.
- **🍱 Interactive Circular Indian Thali**: Visual stainless-steel plate layout with 6 interactive katoris (Dal, Rajma/Curry, Basmati Rice, 2x Roti, Fresh Curd, Salad & Achar). Tap any bowl to swap dishes in real-time with live calorie and macro recalculation!
- **🥗 My Diet Planner with 5 Meal Slots**: Curated authentic Indian foods across Breakfast, Lunch (Thali), Snacks, Dinner, and Late-Night Cravings, specifically tailored for **Lose Weight (Deficit)** or **Gain Weight (Surplus)**.
- **🥘 Custom Food & Clipart Builder**: Create custom foods (e.g. Daal Chawal, Roti & Pickle) with customizable emojis (🫓, 🍚, 🥣, 🍛, 🥬, 🧀, 🥛, 🍳, 🍗, 🍋, 🍵, ☕, 🍎, 🍿) and calories!
- **🎯 Step Goal Picker in Thousands & Any Custom Number**: Pick goals in thousands (5,000, 6,000, 7,000, 8,000, 9,000, 10,000, 11,000, 12,000, 13,000, 14,000, 15,000) or enter ANY custom number honestly with quick -500/+500 steppers.
- **🔥 Smart Streaks & Complete 30-Day Calendar**: 2 allowed rest days/week without breaking streak, complete 30-day November 2026 calendar grid, and 3-day inactivity rules.
- **🌙 Bedtime & Sleep Recovery Suggestions**: Calming suggestions with sleeping mascot illustration to optimize deep sleep and walking recovery.
- **💧 Hydration Tracker & Alarms**: Quick 1-tap logging (+250ml / +500ml), daily target progress ring, and customizable reminder alerts.
- **✨ VMS Buddy Mascot**: 9 authentic expressions across walking, victory celebrations, workouts, rest days, sleep, and celebrations.
- **♥ Support Developer Pack**: Direct native UPI payment chooser (`Vijay Kumar`, `jaynishad2010@okhdfcbank`) with voluntary custom amount, copy toast, and Instagram footer.
- **🎨 Official App Icon**: Custom mascot on pure white background packaged into all Android mipmap densities.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 1.9.24
- **UI Framework**: Jetpack Compose (Material 3)
- **Local Persistence**: Room SQLite Database + SharedPreferences
- **Sensors**: Android `SensorManager` (`Sensor.TYPE_STEP_COUNTER` with `Sensor.TYPE_STEP_DETECTOR` fallback)
- **Background Engine**: Android Foreground Service (`foregroundServiceType="health"`)
- **CI/CD**: GitHub Actions (auto-compiles APK and deploys to GitHub Pages & Releases on push)
- **Hosting**: GitHub Pages for public landing page

---

## 📱 How to Install the APK

1. Download directly from the [Landing Page](https://vijayxprty.github.io/healbuds/Healbuds-v2.1.0.apk) or [Releases page](https://github.com/vijayxprty/healbuds/releases/latest).
2. Tap the downloaded `Healbuds-v2.1.0.apk` file on your Android phone.
3. Tap **Install** (enable *"Allow from this source"* if prompted).
4. Open **Healbuds** and start tracking!

---

## 💻 Building from Source

```bash
# Clone the repository
git clone https://github.com/vijayxprty/healbuds.git
cd healbuds

# Build the Debug APK
./gradlew assembleDebug

# Output APK location:
# app/build/outputs/apk/debug/app-debug.apk
```
