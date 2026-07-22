# 💳 Finance Guide

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-7F52FF.svg?logo=kotlin)](https://kotlinlang.org/)
[![Android Studio](https://img.shields.io/badge/Android%20Studio-Jetpack%20Compose-3DDC84.svg?logo=androidstudio)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Finance Guide** is a modern, 100% offline-first personal finance Android application built using **Kotlin**, **Jetpack Compose**, and **Room Database**.

It helps you easily track your income, monitor daily expenses, set category budgets, analyze spending insights, and customize your app experience with vibrant themes.

---

## ✨ Key Features

- 💰 **Income & Expense Tracking**: Fast recording of daily transactions with category tags, custom notes, and date management.
- 🎯 **Smart Budget Management**: Set monthly budgets for categories (Food, Rent, Bills, Shopping, Transport) and track spending with visual progress bars.
- 📊 **Visual Insights & Analytics**: View clean spending breakdown donut charts and monthly balance summaries.
- 🎨 **Multi-Theme Customization**: Switch anytime between 3 visual themes:
  - 🕷️ **Spider-Man Theme**: Heroic red and dark slate styling.
  - ☠️ **One Piece Theme**: Pirate ocean blue and straw hat gold styling.
  - 💰 **Finance Guide (Default)**: Modern mint emerald and cyber teal theme.
- 📱 **100% Standalone & Offline**: All user profiles, settings, transactions, and budgets are saved locally on your device with instant responsiveness and zero network dependencies.

---

## 🛠️ Tech Stack

- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Clean Architecture
- **Local Database**: Room Database
- **Local Persistence**: SharedPreferences & SettingsManager
- **State Management**: Kotlin Coroutines & StateFlow

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Hedgehog / Iguana or newer)
- **JDK 17**
- Android Phone or Emulator running **Android 8.0 (API 26)** or higher

### Building & Running
1. Clone the repository:
   ```bash
   git clone https://github.com/Ravimodi-98/Finance-Guide.git
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle dependencies.
4. Run the app on your phone or emulator:
   ```bash
   ./gradlew installDebug
   ```

---

## 📄 License
This project is open source and available under the [MIT License](LICENSE).
