# 🏴‍☠️ Finance Guide - The Pirate King's Ledger (w/ Gemini AI Navigator)

Welcome to **Finance Guide**, a premium personal finance companion application designed with a creative *One Piece* anime twist! Take control of your coins, set bounty limits (budgets), record your logbooks of transaction bounties, and sail safely with the help of **Nami - the Finance Navigator**, your personal AI finance assistant powered by Google Gemini.

---

## ⚓ Secure System Architecture

This project is built to be uploaded safely to public GitHub repositories without exposing private credentials. The Gemini API key remains **strictly hidden** inside a cloud backend proxy server. The Android application has no access to and no knowledge of the API key.

```mermaid
graph TD
    subgraph Mobile Client [Android Device (No Secrets)]
        App[Kotlin & Jetpack Compose App]
    end

    subgraph Secure Cloud [Proxy Layer]
        Server[Express Proxy Server (Node.js/TypeScript)]
        dotenv[Environment Configuration (.env)]
        Server -.->|Reads Key| dotenv
    end

    subgraph External [AI Platform]
        Gemini[Google Gemini API]
    end

    App <==>|1. HTTPS Request /api/chat| Server
    Server <==>|2. Secure SDK Call w/ API Key| Gemini
```

---

## 🧭 Features
- **Income & Expense Tracking**: Chart your gold and track where you spend your Beli.
- **Bounty Limits (Budgets)**: Set budget caps on expense categories to keep Nami from yelling at you!
- **Finance Navigator (AI Chat)**: A premium chat screen styled with a custom compass vector avatar. Nami strictly answers financial queries (e.g. saving, emergency funds, budget planning, debt reduction) while politely brushing off coding, homework, or random chat.
- **Premium Aesthetics**: Elegant gold and Luffy red dark-themed UI, custom Canvas-drawn Straw Hat and Gold Coin animations, and responsive spring physics effects.
- **Robust Error Handling**: Real-time feedback for rate limits, connection dropouts, server timeouts, and invalid API requests.
- **Privacy Controls**: Conversation clearing, clipboard logging, and chat sharing integrations.

---

## 🚀 Getting Started

### 1. Backend Server Setup
To stand up the AI proxy server locally:
1. Ensure [Node.js](https://nodejs.org/) v18+ is installed on your computer.
2. Open a terminal and enter the `backend/` directory:
   ```bash
   cd backend
   ```
3. Install the dependencies:
   ```bash
   npm install
   ```
4. Create a `.env` file from the example template:
   ```bash
   cp .env.example .env
   ```
5. Edit `.env` and fill in your Gemini API key:
   ```env
   GEMINI_API_KEY=AQ.Ab8RN6Knpdv...
   ```
6. Start the server in hot-reload development mode:
   ```bash
   npm run dev
   ```
   The backend will start listening at `http://localhost:3000`.

### 2. Android App Configuration
To hook the Android app up to the backend:
1. Open the project in Android Studio.
2. Open `app/src/main/java/com/example/financeguide/di/AppContainer.kt`.
3. Locate the `aiRetrofit` declaration:
   - For **Android Emulator testing**: The default base URL `http://10.0.2.2:3000/` works out of the box.
   - For **Physical Android Device testing**: Make sure both your phone and PC are connected to the same Wi-Fi network. Replace `10.0.2.2` with your computer's local area network IP address (e.g., `http://192.168.1.15:3000/`).
   - For **Production cloud testing**: Put in your deployed backend endpoint URL (e.g., `https://my-navigator-backend.onrender.com/`).
4. Build and run the app on your phone! Tap the **compass icon** at the top right of the Dashboard to launch the AI Navigator screen.

---

## 🛡️ Security Notes
- **API Keys**: Never write API keys in `gradle.properties`, `local.properties`, or source code files.
- **Git Protection**: The project is pre-configured with a `.gitignore` ignoring `.env` and compiler artifacts. Always check `git status` before pushing changes.
- **Limiting**: The Node backend implements `express-rate-limit` to block IP addresses making more than 30 API calls per minute, protecting your Gemini billing quotas.

---

## 🛠️ Code Structure
- **Backend**: Clean Node.js Express server using TypeScript. Controllers orchestrate requests, services call the SDK, and middlewares manage security, rate-limiting, and schema validation.
- **Android Client**: Clean Architecture with MVVM:
  - **Data Layer**: Retrofit interface `FinanceApiService` and `FinanceAIRepositoryImpl` mapping networking errors.
  - **Domain Layer**: `FinanceAIRepository` interface and `AskFinanceQuestionUseCase` business logic.
  - **Presentation Layer**: `FinanceAIViewModel` managing loading/error states using `StateFlow`, and `FinanceAIScreen` Compose UI.
