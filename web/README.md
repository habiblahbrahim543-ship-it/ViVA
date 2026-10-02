# VIVA Web & Progressive Web App (PWA)

Production-ready responsive Web/PWA frontend for **VIVA**, unified with the Android application and sharing the exact same Firebase infrastructure.

## 🚀 Key Features
- **Mobile Safari (iOS / iPhone) & Chrome (Android) Optimized:**
  - Full viewport cover with iOS safe-area-inset padding for notches and home indicators.
  - Snap-scroll fullscreen vertical video feed with gesture support (double-tap to like, swipe, tap to pause/play).
  - PWA install prompt customized for iOS Safari ("Add to Home Screen" instructions) and Chromium ("Install PWA" native prompt).
  - Service worker caching for fast offline launching.
- **Unified Cloud Backend (One App, Zero Desync):**
  - Shares Firebase Authentication with Android.
  - Shares Firestore Database (`/users`, `/videos`, `/comments`, `/verificationRequests`, `/adminNotifications`, `/support_conversations`, `/moderation_reports`).
  - Strict username reservation: `@VIVA`, `@admin`, `@owner`, and `@support` are protected across both platforms.
- **Full Feature Parity:**
  - **Feed:** For You & Following feeds with real video playback, sound details, likes, comments, shares, and reports.
  - **Discover:** Search bar, trending hashtags, top creators, sound clips.
  - **Create:** Video upload with captions, tags, sound selection, and publishing flow.
  - **Inbox & Direct Messages:** Real-time chat with creators and direct connection to the **Official VIVA Support Desk**.
  - **Profile:** Creator bio, stats, video grids, Edit Profile, Apply for Verified Badge.
  - **Admin Control Panel (`/admin`):** Protected dashboard for staff/owners with overview metrics, applicant review with one-click verification badge granting, user moderation (suspend/ban), content takedowns, support desk, and audit logs.

---

## 🛠️ Build & Development

### 1. Local Development
```bash
cd web
npm install
npm run dev
```

### 2. Production Build
```bash
cd web
npm run build
```
This generates the optimized production bundle in `web/dist/`.

---

## 🌐 1-Click Deployment Options

### Option A: Firebase Hosting (Recommended for unified backend)
The project includes a root `firebase.json` pre-configured to host `web/dist/`:
```bash
npm --prefix web run build
firebase deploy --only hosting
```

### Option B: Vercel
1. Push your repository to GitHub.
2. In Vercel, click **Add New Project** and select your repository.
3. Set the **Root Directory** to `web`.
4. Vercel will automatically detect `vite` and the included `web/vercel.json`.
5. Click **Deploy**.

### Option C: Netlify
1. Connect your repository to Netlify.
2. The included `web/netlify.toml` automatically configures the build command (`npm run build`), publish directory (`dist`), and single-page app rewrites.
3. Click **Deploy Site**.
