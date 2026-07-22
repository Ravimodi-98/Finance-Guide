# 💳 Finance Guide - Personal Finance & Budget Tracker

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-7F52FF.svg?logo=kotlin)](https://kotlinlang.org/)
[![Android Studio](https://img.shields.io/badge/Android%20Studio-Jetpack%20Compose-3DDC84.svg?logo=androidstudio)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-orange.svg)](https://developer.android.com/topic/architecture)
[![Database](https://img.shields.io/badge/Database-Room-4285F4.svg)](https://developer.android.com/training/data-storage/room)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Finance Guide** is a modern, fast, and 100% offline-first Android application designed to help you manage your personal finances with ease. Built with **Kotlin**, **Jetpack Compose**, and **Room Database**, it provides an intuitive interface to track income, control budgets, analyze spending habits, and switch between customizable visual themes.

---

## 🌟 Highlights & Features

- 💵 **Income & Expense Tracker**: Easily log daily earnings and spending with custom notes, category tags, and date filters.
- 🎯 **Smart Budget Manager**: Set monthly limits across key spending categories (Food, Rent, Bills, Shopping, Transport) and monitor your remaining budget with visual progress bars.
- 📊 **Visual Analytics & Donut Charts**: Instantly visualize your income-to-expense ratio and category breakdowns with dynamic Compose charts.
- 🎨 **Multi-Theme Customization**: Personalize your app experience anytime with 3 distinct themes:
  - 🕷️ **Spider-Man Theme**: Heroic comic red & dark slate aesthetic.
  - ☠️ **One Piece Theme**: Pirate ocean blue & straw hat gold aesthetic.
  - 💰 **Finance Guide Theme (Default)**: Sleek mint emerald & cyber teal aesthetic.
- ⚡ **100% Offline & Private**: All data is stored securely on your local device via Room DB and SharedPreferences. No sign-in required, zero tracking, and zero network dependency.

---

## 🛠️ Architecture & Tech Stack

The application follows **Clean Architecture** and the **MVVM (Model-View-ViewModel)** design pattern for optimal modularity and code maintainability.

```
app/
 ├── data/            # Local Room Database, DAOs, Entities, SettingsManager
 ├── domain/          # UseCases, Business Logic Models
 ├── ui/
 │    ├── components/ # Reusable Compose UI widgets (BalanceCard, DonutChart, Animations)
 │    ├── navigation/ # Jetpack Navigation host & bottom nav bar
 │    ├── screen/     # Dashboard, Budgets, Insights, AddTransaction, Settings
 │    └── theme/      # Custom color palettes, typography, theme providers
```

- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose (Material 3)
- **Local Persistence**: Room Database, SharedPreferences
- **Asynchronous Processing**: Kotlin Coroutines & StateFlow

---

## 🚀 How to Run

### Prerequisites
- **Android Studio** (Hedgehog / Iguana or newer)
- **JDK 17**
- Android device or emulator running **Android 8.0 (API 26)** or higher

### Build & Deploy
1. **Clone the repository:**
   ```bash
   git clone https://github.com/Ravimodi-98/Finance-Guide.git
   ```
2. **Open in Android Studio:** Open `Finance Guide` directory.
3. **Sync & Build:** Let Gradle sync all dependencies.
4. **Deploy to Device:**
   ```bash
   ./gradlew installDebug
   ```

---

## 📄 License
This project is open-source software licensed under the [MIT License](LICENSE).
