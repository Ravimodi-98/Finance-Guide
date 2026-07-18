# Finance Navigator AI Proxy Backend

Secure backend proxy server powered by **Node.js, Express, TypeScript, and Google Gemini**. 

This server acts as a secure intermediary between the Android client and the Gemini API, shielding your private API key from public exposure in Android build products or on GitHub.

---

## Features
- **Secure Gemini Integration**: Calls the official `@google/generative-ai` SDK using a strict personal finance education system prompt.
- **CORS Protection**: Permits cross-origin sharing restricted to safe methods.
- **Helmet**: Adds secure HTTP response headers.
- **Rate Limiting**: Defends endpoints from DDoS or API-key exhaustion (configured to 30 requests per minute per IP).
- **Validation & Sanitization**: Validates parameters and sanitizes text inputs.
- **Transient Retry**: Retries connection to Gemini up to 2 times for transient network faults.
- **Uptime Health Checks**: Exposes a standard endpoint for host health monitoring.

---

## Local Setup

### 1. Requirements
Ensure you have [Node.js](https://nodejs.org/) (v18+) installed.

### 2. Install Dependencies
Navigate to the `backend/` directory and install npm packages:
```bash
npm install
```

### 3. Environment Variables
Create a `.env` file in the `backend/` root directory (you can copy `.env.example` as a template):
```env
PORT=3000
GEMINI_API_KEY=your_actual_gemini_api_key
```
> [!WARNING]
> Do **NOT** commit `.env` to GitHub. The project's `.gitignore` has been updated to automatically exclude this file.

### 4. Running the Server

#### Development Mode (Hot Reload)
```bash
npm run dev
```

#### Production Build & Run
```bash
npm run build
npm start
```

---

## API Documentation

### 1. Health Status
Check if the proxy server is online.
- **Endpoint**: `GET /api/health`
- **Response**:
```json
{
  "status": "healthy",
  "uptime": 12.45,
  "timestamp": "2026-07-18T16:00:00.000Z"
}
```

### 2. Send Chat Prompt
Send a financial question along with the current session's chat history.
- **Endpoint**: `POST /api/chat`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
  "message": "Give me a simple budget rule",
  "history": [
    { "role": "user", "text": "Hi" },
    { "role": "model", "text": "Ahoy! I am the Finance Navigator. How can I guide your ledger today?" }
  ]
}
```
- **Response (200 OK)**:
```json
{
  "response": "A great starting point is the **50/30/20 rule**:\n- **50%** for Needs (food, ship maintenance)\n- **30%** for Wants (cola, banquets)\n- **20%** for Savings (hoarding treasure for the Grand Line)!"
}
```
- **Response (429 Rate Limited)**:
```json
{
  "error": "Too many navigation requests, matey! Please rest a bit before asking the Navigator again."
}
```

### 3. Clear Chat Session
Notify the backend that the client has cleared the session.
- **Endpoint**: `POST /api/chat/reset`
- **Response**:
```json
{
  "status": "success",
  "message": "Logbook cleared. The Navigation chat is reset."
}
```

---

## Production Deployment

### 1. Railway
1. Sign up on [Railway.app](https://railway.app/).
2. Click **New Project** -> **Deploy from GitHub repo**.
3. Choose your repository, select the `backend` subdirectory as the root.
4. Under **Variables**, add `GEMINI_API_KEY` with your token.
5. Deploy. Railway will auto-detect the Node.js application, build, and allocate a public domain name.

### 2. Render
1. Sign up on [Render.com](https://render.com/).
2. Create a new **Web Service** and link your GitHub repo.
3. Configure settings:
   - **Root Directory**: `backend`
   - **Build Command**: `npm install && npm run build`
   - **Start Command**: `npm start`
4. Under **Environment**, add the key: `GEMINI_API_KEY`.
5. Deploy and copy your new service URL (e.g. `https://xxx.onrender.com/`).
