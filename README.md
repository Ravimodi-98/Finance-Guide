# 💳 Finance Guide - AI-Powered Personal Finance & Budget Tracker

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.20-7F52FF.svg?logo=kotlin)](https://kotlinlang.org/)
[![Android Studio](https://img.shields.io/badge/Android%20Studio-Jetpack%20Compose-3DDC84.svg?logo=androidstudio)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material--3-Compose%20M3-8250DF.svg)](https://m3.material.io/)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-orange.svg)](https://developer.android.com/topic/architecture)
[![Database](https://img.shields.io/badge/Database-Room%20%2B%20KSP-4285F4.svg)](https://developer.android.com/training/data-storage/room)
[![AI Engine](https://img.shields.io/badge/AI-Gemini%203.5%20Flash-00B4D8.svg?logo=google)](https://deepmind.google/technologies/gemini/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Finance Guide** is an intelligent, feature-packed personal finance and wealth management Android application designed with modern aesthetics and cutting-edge mobile architecture. Combining **Jetpack Compose (Material 3)**, **Room Database**, and **Google Gemini AI**, Finance Guide offers real-time financial tracking, budget control, visual spending analytics, and an interactive **AI Financial Advisor** adapted to multiple themed personas.

---

## 🌟 Key Features

### 🤖 1. Creative & Modern AI Financial Advisor (Gemini Powered)
- **Interactive Multi-Persona AI**: Chat with your financial advisor tailored to your active theme:
  - 🏴‍☠️ **Nami (One Piece Navigator)**: Treasure management, pirate market wisdom, and savings routes.
  - 🕷️ **Spider-Man (Neighborhood Web Guide)**: Suit budget management, mission costs, and investing with great responsibility.
  - 📈 **FinBot (Finance Pro Advisor)**: Quantitative market insights, 50/30/20 budget allocations, and wealth strategies.
- **Interactive Topic Hub**: One-tap query suggestions across 4 core financial pillars:
  - 📊 *Market & Growth* (ETFs, Stocks, Compound Interest, IPOs)
  - 💡 *Smart Budgeting* (50/30/20 Rule, Impulse spending control)
  - 🛡️ *Security & Debt* (Emergency funds, Avalanche vs Snowball debt payoff, Inflation protection)
  - ⚡ *Quick Log & Wealth* (Actionable financial rules, bonus investment strategies)
- **Dynamic AI Soundwave Visualizer**: Real-time 4-bar equalizer and rotating thinking statuses (*"Analyzing financial signals..."*, *"Formulating smart advice..."*).
- **Rich Markdown Formatting**: Automatic parsing and formatting of bold headers, structured bullet lists, numeric steps, monospace financial calculation cards, and 1-tap message copying with visual checkmark feedback.
- **Flawless Keyboard Inset Handling**: Smooth window insets integration (`WindowInsets.ime.union(navigationBars)`) with zero dead space and automatic list scrolling.

---

### 🎨 2. Multi-Theme Universe Engine
Customize your entire UI experience across typography, gradients, animations, and icons:
| Theme | Aesthetic Palette | Persona & Icon |
| :--- | :--- | :--- |
| **Finance Pro** (Default) | Electric Mint Emerald, Cyber Cyan, Obsidian Dark Navy | FinBot • High-Tech Growth Shield Logo |
| **Spider-Man** | Crimson Red, Royal Electric Blue, Matte Carbon Slate | Spider-Man • Custom Mask Vector Logo |
| **One Piece** | Straw Hat Gold, Luffy Crimson, Ocean Navy | Nami • Straw Hat Vector Logo |

---

### 💰 3. Income & Expense Management
- Quick logging of Income & Expense transactions with customizable categories, custom notes, and timestamps.
- Dynamic balance cards displaying total net worth, total income, and total expenses.
- One-tap quick actions directly from the home dashboard.

---

### 🎯 4. Smart Budget Limits & Monitoring
- Define monthly spending limits across primary life categories (Food, Rent, Bills, Shopping, Transport, etc.).
- Real-time budget consumption progress bars with percentage indicators and warning states.

---

### 📊 5. Visual Insights & Donut Analytics
- Dynamic Compose **Donut Charts** illustrating your exact expense and income distribution.
- Interactive category breakdown cards and trend analytics.
- Direct shortcut to launch AI financial analysis on your current spending.

---

### 🔒 6. Privacy & Offline-First Storage
- Fully functional in offline mode with complete local persistence via **Room DB** and **DataStore / SharedPreferences**.
- Secure authentication system supporting guest/offline mode as well as registered user profiles.

---

## 🛠️ Architecture & Tech Stack

```
app/src/main/java/com/example/financeguide/
 ├── data/
 │    ├── local/         # Room Database, DAOs, Entities, SettingsManager
 │    ├── remote/        # Gemini API Service & Request/Response Models (Retrofit + OkHttp)
 │    └── repository/    # FinanceRepository, AuthRepository, ChatbotRepository
 ├── di/                 # AppContainer Dependency Injection
 ├── domain/
 │    ├── model/         # Transaction, Budget, ChatMessage, User domain models
 │    └── usecase/       # Financial business logic & calculation rules
 ├── theme/              # Dynamic Theme System (Colors, Typography, ThemeStrings, Shapes)
 └── ui/
      ├── components/    # BalanceCard, DonutChart, Animations, AnimatedCounter, Logotypes
      ├── navigation/    # Jetpack Navigation Graph & Glassmorphic Bottom Bar
      └── screen/        # Dashboard, Budget, Insights, Chatbot, Settings, Auth, Onboarding
```

- **Language**: Kotlin 2.0.20
- **UI Framework**: Jetpack Compose with Material 3
- **Network**: Retrofit 2.11 + OkHttp 4.12 + Gson
- **Local Persistence**: Room Database (KSP) + Jetpack DataStore / SharedPreferences
- **Asynchronous Flow**: Kotlin Coroutines & StateFlow (Lifecycle Compose)
- **Image Loading**: Coil Compose

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Ladybug / Koala / Iguana or newer)
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
   - Let Gradle sync all dependencies.

3. **Run on Device or Emulator:**
   ```bash
   ./gradlew installDebug
   ```

---

## 📄 License
This project is licensed under the [MIT License](LICENSE).
