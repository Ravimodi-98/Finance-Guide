# 💳 Finance Guide - Personal Finance & Budget Tracker

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.20-7F52FF.svg?logo=kotlin)](https://kotlinlang.org/)
[![Android Studio](https://img.shields.io/badge/Android%20Studio-Jetpack%20Compose-3DDC84.svg?logo=androidstudio)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material--3-Compose%20M3-8250DF.svg)](https://m3.material.io/)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-orange.svg)](https://developer.android.com/topic/architecture)
[![Database](https://img.shields.io/badge/Database-Room%20%2B%20KSP-4285F4.svg)](https://developer.android.com/training/data-storage/room)
[![Download APK](https://img.shields.io/badge/Download-APK%20(v1.0)-success?style=for-the-badge&logo=android)](https://github.com/Ravimodi-98/Finance-Guide/raw/main/FinanceGuide.apk)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Finance Guide** is a modern, offline-first personal finance and wealth management Android application designed with cutting-edge mobile architecture. Combining **Jetpack Compose (Material 3)**, **Room Database**, and a customizable **Multi-Theme Engine**, Finance Guide offers seamless transaction tracking, budget limit control, visual spending analytics, and intelligent financial insights.

---

## 📥 Download App (APK)

Get the latest ready-to-install Android package directly:

[![Download APK](https://img.shields.io/badge/Download%20APK-v1.0%20(Direct%20Download)-2ea44f?style=for-the-badge&logo=android)](https://github.com/Ravimodi-98/Finance-Guide/raw/main/FinanceGuide.apk)

> 🔗 **Direct Download Link**: [Download FinanceGuide.apk](https://github.com/Ravimodi-98/Finance-Guide/raw/main/FinanceGuide.apk)  
> *Compatible with Android 7.0 (API 24) and higher.*

---

## 🌟 Key Features

### 💰 1. Income & Expense Management
- **Quick Logging**: Fast and fluid transaction logging for both income and expenses with customizable categories, notes, and timestamps.
- **Dynamic Balance Card**: Real-time overview of your net worth, total monthly income, and total expenses with interactive quick-action shortcuts.
- **Transaction History**: Comprehensive recent transaction lists with editing, category identification, and filtering.

---

### 🎯 2. Smart Budget Limits & Alerts
- **Category Budgets**: Set custom monthly spending thresholds for essential categories (Food, Bills, Transport, Entertainment, Shopping, Health, etc.).
- **Real-Time Progress Tracking**: Color-coded progress indicators warning you when you approach or exceed 80% and 100% of your allocated budget.

---

### 📊 3. Visual Analytics & Donut Charts
- **Interactive Donut Charts**: Beautiful custom Jetpack Compose charts visualizing spending and earning distributions.
- **Category Breakdowns**: Detailed percentage analytics and category totals to help identify spending leaks and boost savings potential.

---

### 🎨 4. Multi-Theme Universe Engine
Customize your entire UI experience across typography, gradients, animations, and icons:

| Theme | Aesthetic Palette | Persona & Icon |
| :--- | :--- | :--- |
| **Finance Pro** (Default) | Electric Mint Emerald, Cyber Cyan, Obsidian Dark Navy | Professional FinBot Growth Shield |
| **Spider-Man** | Crimson Red, Royal Electric Blue, Matte Carbon Slate | Web Slinger Custom Mask Vector |
| **One Piece** | Straw Hat Gold, Luffy Crimson, Ocean Navy | Pirate King Straw Hat Vector |

---

### 🔒 5. Privacy & Offline-First Storage
- **100% Offline-First**: All your financial data is stored locally on your device using **Room Database** and **DataStore / SharedPreferences**.
- **No Cloud Dependency**: Complete privacy with zero external tracking or mandatory server syncing.
- **Guest & User Modes**: Seamlessly switch between guest mode and personal profile management.

---

## 🛠️ Architecture & Tech Stack

```
app/src/main/java/com/example/financeguide/
 ├── data/
 │    ├── local/         # Room Database, DAOs, Entities, SettingsManager
 │    └── repository/    # FinanceRepository, AuthRepository
 ├── di/                 # AppContainer Dependency Injection
 ├── domain/
 │    ├── model/         # Transaction, Budget, Category, User domain models
 │    └── usecase/       # Financial business logic & suggestion algorithms
 ├── theme/              # Dynamic Theme System (Colors, Typography, ThemeStrings, Shapes)
 └── ui/
      ├── components/    # BalanceCard, DonutChart, Animations, AnimatedCounter
      ├── navigation/    # Jetpack Navigation Graph & Glassmorphic Bottom Bar
      └── screen/        # Dashboard, Budget, Insights, Settings, Auth, Onboarding
```

- **Language**: Kotlin 2.0.20
- **UI Framework**: Jetpack Compose (Material 3)
- **Local Persistence**: Room Database (KSP) + SharedPreferences
- **Architecture**: MVVM + Clean Architecture with Use Cases
- **Asynchronous Flow**: Kotlin Coroutines & StateFlow (Lifecycle Compose)
- **Navigation**: Jetpack Navigation Compose

---

## 🚀 Getting Started for Developers

### Prerequisites
- **Android Studio** (Koala / Ladybug or newer)
- **JDK 17**
- Android device or emulator running **Android 7.0 (API 24)** or higher

### Installation & Run

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/Ravimodi-98/Finance-Guide.git
   cd Finance-Guide
   ```

2. **Open in Android Studio:**
   - Select `Open an Existing Project` and choose the cloned folder.
   - Wait for Gradle to complete syncing.

3. **Build & Install:**
   ```bash
   ./gradlew installDebug
   ```

---

## 📄 License
This project is licensed under the [MIT License](LICENSE).
