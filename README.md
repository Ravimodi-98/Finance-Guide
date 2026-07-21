# 💳 Finance Guide - Smart Multi-Theme Personal Finance & AI Assistant

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-7F52FF.svg?logo=kotlin)](https://kotlinlang.org/)
[![Android Studio](https://img.shields.io/badge/Android%20Studio-Jetpack%20Compose-3DDC84.svg?logo=androidstudio)](https://developer.android.com/jetpack/compose)
[![Node.js](https://img.shields.io/badge/Node.js-Express%20%2F%20TypeScript-339933.svg?logo=nodedotjs)](https://nodejs.org/)
[![Gemini AI](https://img.shields.io/badge/AI-Google%20Gemini-4285F4.svg?logo=google)](https://ai.google.dev/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Finance Guide** is a modern, feature-packed personal finance companion for Android built with **Jetpack Compose**, **Clean Architecture**, and **Google Gemini AI**. 

It combines robust expense tracking, smart budget management, peer-to-peer debt simplification, and an interactive **AI Financial Advisor** with multi-theme customization (Standard **Finance Pro**, **One Piece**, and **Spider-Man** themes).

---

## 🌟 What's New & Key Highlights

- 🎨 **Dynamic Multi-Theme Engine**: Switch between 3 unique visual identities & terminology packages:
  - **Finance Pro**: Sleek, modern indigo/emerald dark theme with **FinBot** AI advisor.
  - **One Piece (Pirate King's Ledger)**: Gold/crimson aesthetic, bounty budget limits, and **Nami - Finance Navigator**.
  - **Spider-Man (Hero's Ledger)**: Web-red/navy aesthetic, Canvas-drawn web & logo graphics, and **Spidey / Web-Slinger AI**.
- 💬 **WhatsApp-Style Chat UI**: Redesigned AI chat interface featuring dark mode chat bubbles, seamless keyboard docking (`imePadding`), automatic keyboard scroll, and hidden bottom navigation for maximum workspace.
- 📊 **Smart Expense & Budgeting Engine**: 
  - Dynamic Canvas-drawn budget donut charts and animated counter balance cards.
  - Category picker organized in a structured 3-column color-coded grid.
- 🤝 **Debt Simplifier & Sync**: Integrated peer-to-peer debt resolution module (`DebtSimplifier`) for group expenses and offline Room DB sync.
- 🛡️ **Vercel-Ready Cloud Backend Proxy**: Express/TypeScript proxy server supporting trust-proxy, IP rate-limiting (30 requests/min), and zero API key exposure on the Android app client.

---

## ⚓ System Architecture

The application uses a **Secure Proxy Pattern**. The Android client contains no API keys or secret tokens. All AI requests pass through a light Express TypeScript proxy server before communicating with Google Gemini API.

```mermaid
graph TD
    subgraph Mobile Client [Android Device (Jetpack Compose & Room DB)]
        App[Android App (Clean Architecture + MVVM)]
        ThemeEngine[Theme & Persona Engine]
        App --> ThemeEngine
    end

    subgraph Secure Cloud Backend [Node.js / Express Proxy]
        Server[Express Proxy Server (Vercel Serverless / Node)]
        RateLimiter[Express Rate Limiter]
        EnvConfig[.env Secrets Store]
        Server --> RateLimiter
        Server -.->|Loads GEMINI_API_KEY| EnvConfig
    end

    subgraph External AI Platform [Google Cloud]
        Gemini[Google Gemini API]
    end

    App <==>|1. REST Request /api/chat| Server
    Server <==>|2. Authenticated Gemini SDK Call| Gemini
```

---

## 🧭 Main Features

### 1. 🎨 Theme-Aware Customization & Personas
- **Dynamic Strings & Terms**: UI labels, empty states, transaction types, and AI system prompts update automatically based on the selected theme.
- **Custom Canvas Vector Art**: Includes hand-crafted Canvas graphics for Spider-Man logos, Straw Hat icons, and empty-state Donut Charts.

### 2. 💰 Complete Financial Management
- **Dashboard & Analytics**: Real-time balance summaries with animated balance counters, monthly spending charts, and recent transaction history.
- **Budgeting & Donut Charts**: Category-based budget limits with progress bars and interactive visual donut chart breakdowns.
- **Debt Simplifier (`DebtSimplifier.kt`)**: Algorithm to simplify net balances among multiple group members or split expenses cleanly.
- **Structured Category Picker**: 3-column grid layout with per-category color badges for fast transaction creation.

### 3. 🤖 AI Financial Advisor (Powered by Gemini)
- **Theme Personas**:
  - **FinBot** (Finance Pro mode): Professional, direct financial advisor.
  - **Nami** (One Piece mode): Energetic pirate navigator focused on growing your Beli and keeping bounties under control.
  - **Spidey** (Spider-Man mode): Friendly neighborhood AI giving practical money advice with web-slinger flair.
- **Financial Guardrails**: System instructions ensure the bot focuses on budgeting, savings, emergency funds, and debt reduction while ignoring off-topic requests.

### 4. 📲 Modern Android UI & UX Polish
- **WhatsApp Dark Mode Aesthetic**: Speech bubbles with timestamps, status indicators, and clean typography.
- **Soft Keyboard Docking**: Uses `imePadding` so input fields dock smoothly above the keyboard without layout jumps.
- **Immersive View**: Bottom navigation bar automatically hides on the chat screen to maximize chat context.

---

## 🛠️ Tech Stack

### Android Client
- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose, Material 3
- **Architecture**: MVVM + Clean Architecture (Data, Domain, Presentation)
- **Database**: Room Database (Local Persistence)
- **Networking**: Retrofit 2, OkHttp, Gson
- **Asynchronous**: Kotlin Coroutines, StateFlow, SharedFlow
- **Image & Vectors**: Custom Compose Canvas drawing & Vector graphics

### Cloud Proxy Backend
- **Runtime**: Node.js v18+ / TypeScript
- **Framework**: Express.js (Deployable to Vercel Serverless)
- **AI SDK**: `@google/genai` (Google Gemini API)
- **Security**: `express-rate-limit`, `cors`, `dotenv`, trust-proxy enabled

---

## 🚀 Getting Started

### 1. Backend Proxy Server Setup

1. Open a terminal and navigate to `backend/`:
   ```bash
   cd backend
   ```
2. Install Node dependencies:
   ```bash
   npm install
   ```
3. Create your `.env` configuration:
   ```bash
   cp .env.example .env
   ```
4. Edit `.env` and insert your Google Gemini API Key:
   ```env
   GEMINI_API_KEY=your_gemini_api_key_here
   PORT=3000
   ```
5. Start the backend locally:
   ```bash
   npm run dev
   ```
   *The proxy will start on `http://localhost:3000`.*

> **Vercel Deployment**: The backend includes `vercel.json` and Express `trust proxy` settings, allowing 1-click deployment to Vercel.

---

### 2. Android App Setup

1. Open the project folder in **Android Studio** (Jellyfish or newer recommended).
2. Configure the server URL in `app/src/main/java/com/example/financeguide/di/AppContainer.kt`:
   - **Android Emulator**: Uses default `http://10.0.2.2:3000/`
   - **Physical Device**: Set your local PC IP address (e.g., `http://192.168.1.X:3000/`)
   - **Production Cloud**: Set your Vercel host URL (e.g., `https://your-app.vercel.app/`)
3. Sync Gradle and run the app on your emulator or connected device!

---

## 🛡️ Security & Privacy

- **Zero Secret Leakage**: No Gemini API keys stored in APK build artifacts, `gradle.properties`, or client code.
- **Rate Limiting**: Protects your Gemini API quota via IP-based rate limiting (30 reqs/min).
- **Privacy First**: Local financial records stored in Room SQLite DB on your physical device.

---

## 📁 Repository Structure

```
Finance App/
├── app/                        # Android Client Application
│   └── src/main/java/com/example/financeguide/
│       ├── data/              # Room DB, DAOs, Retrofit API & Repositories
│       ├── di/                # Dependency Injection (AppContainer)
│       ├── domain/            # Use Cases (DebtSimplifier, AskFinanceQuestion)
│       ├── theme/             # Color tokens, Theme definitions, App Themes
│       └── ui/                # Jetpack Compose Screens, Navigation & Components
│           ├── components/    # DonutChart, AnimatedCounter, SpidermanLogo, etc.
│           ├── navigation/    # BottomNavBar, FinanceNavGraph
│           └── screen/        # AI Chat, Dashboard, Budget, Insights, Settings
└── backend/                   # Node.js TypeScript Gemini Proxy Server
    ├── src/
    │   ├── controllers/       # Chat controller
    │   ├── services/          # Gemini API service integration
    │   └── app.ts             # Express server setup & rate limiter
    └── vercel.json            # Deployment configuration for Vercel
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
