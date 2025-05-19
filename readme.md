# ChatGPT Clone — Jetpack Compose Sample

> Modern Android reference app that showcases a **text + voice ChatGPT client** built with 100 % Jetpack Compose, Kotlin 2.3, and a clean MVVM architecture.

<br/>

| Home (text) | Voice mode | History drawer |
|---|---|---|
| ![](docs/screens/chat_light.png) | ![](docs/screens/voice_dark.png) | ![](docs/screens/history_light.png) |

---

## ✨ Feature Highlights

| Category | Details |
|----------|---------|
| **Chat UI** | • Growing input field (10-line max, scrolls thereafter) <br>• Streaming assistant bubbles with markdown rendered via **compose-richtext** <br>• Shimmer “Thinking…” placeholder while waiting for first token <br>• Copy & Text-to-Speech actions on every assistant reply |
| **Voice chat** | • One-tap mic button <br>• Yarn-ball idle animation → fast pulse while user speaks → waveform equalizer while AI speaks <br>• Android **SpeechRecognizer** for real-time transcription <br>• TTS playback of AI answer |
| **Persistence & offline** | • Room database (conversations + messages) <br>• Drawer with searchable history, auto-titles, works in airplane mode |
| **LLM switching** | • Toggle between **Fake AI** (deterministic demo) and **OpenAI Chat Completions** (streaming) at runtime |
| **Error UX** | • Network monitor; graceful “No internet” Snackbar, messages cached for retry |

---

## 🏗️ Architecture
com.sulav.chatgptclone
├─ data/ # Room + DataStore
├─ repository/ # Single source of truth, streaming logic
├─ ui/ # Compose UI (chat, voice, history, components)
├─ viewmodel/ # Hilt-injected ViewModels (MVVM)
├─ utils/ # Clipboard, TTS, NetworkMonitor, Markdown
└─ di/ # Hilt modules

* Clean MVVM with unidirectional `StateFlow`  
* Repository streams LLM tokens → Room → ViewModel observes → UI renders  
* **ConfigurableAiService** picks **RealAiService** or **FakeAiService** based on DataStore flag

---

## 🛠️ Tech Stack

| Area | Library / Tool |
|------|----------------|
| UI | Jetpack Compose 1.8, Material 3 |
| Animations | Compose animation-core |
| Markdown | `compose-richtext` |
| DI | Hilt 2.56 |
| DB | Room 2.7 (Flow) |
| Network | OkHttp 5, kotlinx-serialization |
| Speech | Android `SpeechRecognizer`, `TextToSpeech` |
| Permissions | Accompanist-permissions |
| Build | AGP 8.4, Kotlin 2.3, Java 17 |

---

## 🚀 Getting Started

1. **Clone**

```bash
git clone https://github.com/<you>/ChatGPTClone.git
```

2. **Secrets**

Create (or append) local.properties:

properties
Copy
Edit
OPENAI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

3. **Run**

bash
Copy
Edit
./gradlew installDebug
Toggle between Fake ↔︎ OpenAI

Open the drawer ➜ switch “Use Live LLM”.

## 🟡 Remaining TODO / Known Limitations

| Area | Item |
|------|------|
| **International-ization** | Several UI strings are still hard-coded in code (e.g., “Ask anything”, icon contentDescriptions). Move them to **`strings.xml`** for full i18n support. |
| **Threading** | Repository network/DB work already runs on `Dispatchers.IO`, but a final audit is needed to ensure **zero** main-thread blocking. |
| **Lifecycle clean-up** | • Release `TextToSpeech` with `shutdown()` in `onCleared()`.<br>• Call `SpeechRecognizer.cancel()` when Voice screen closes to guarantee mic is freed. |
| **Networking** | Add explicit OkHttp `callTimeout()` (e.g., 60 s) to avoid hanging coroutines if OpenAI stalls. |
| **ProGuard / R8** | Insert keep-rules for `android.speech.*` to prevent reflection stripping in release builds. |
| **Accessibility** | Provide `contentDescription` for all interactive icons (send, mic, copy, play). |
| **Crash / Analytics** | No runtime crash reporting (Crashlytics/Sentry) integrated yet. |
| **Amplitude-Driven Equalizer** | Current AI waveform is animated, but uses synthetic amplitude—not real PCM RMS from TTS (Android API limitation). |
| **Offline Sync** | Messages queued offline are stored locally but **not auto-retried** once connection returns. |

## Known Limitations
No automatic retry queue for offline messages yet

TTS amplitude is approximated (Android doesn’t expose live PCM RMS)

Image markdown not rendered

✅ Pre-submission Checklist
./gradlew assembleRelease – release build installs & runs ✔️

detekt / ktlint clean ✔️

API key kept out of VCS ✔️

Replace placeholder screenshots in /docs/screens/ ⬜

📄 License
nginx
Copy
Edit
Apache License 2.0
Built with ❤️ by Sulav Timsina

---

### Last-minute review tips 🕵️‍♂️

1. **Run the Release build** on a physical device; confirm no offline crashes.  
2. Scan **`git status`** for stray debug logs or secrets.  
3. Generate **screenshots** (dark & light) and drop them in `docs/screens/`.  
4. Push a clean commit history (`main` or `submit` branch).  
5. Include this `README.md` at repository root.

You’re ready – good luck with the interview!
