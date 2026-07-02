# AI Chat - Multi-Provider Android Application

A production-ready Android app built with Kotlin and Jetpack Compose that enables chatting with 25+ AI providers from a single unified interface.

## Features

- **25+ AI Providers**: OpenAI, Claude, Gemini, DeepSeek, OpenRouter, Groq, Together, Fireworks, Cerebras, Mistral, Cohere, Perplexity, xAI, Hyperbolic, Novita, SiliconFlow, Qwen, Moonshot, SambaNova, Ollama, LM Studio, vLLM, LiteLLM, OpenAI-compatible, Custom
- **Streaming Responses**: Real-time SSE streaming with partial token rendering
- **Markdown Rendering**: Full markdown support with syntax highlighting
- **Provider Switching**: Round-robin carousel with auto-failover
- **File Upload**: Images, PDFs, and documents with provider capability detection
- **Security**: Biometric/PIN lock, encrypted API key storage
- **Conversation Management**: Search, pin, rename, export to JSON/Markdown

## Tech Stack

- Kotlin + Jetpack Compose + Material 3
- Hilt for Dependency Injection
- Ktor Client for HTTP/SSE
- Room Database for persistence
- Coroutines + StateFlow

## Setup

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK 35

### Build Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/ai-chat-android.git
   cd ai-chat-android
   ```

2. **Setup Gradle Wrapper** (if gradle-wrapper.jar is missing)
   ```bash
   # Windows
   setup-wrapper.bat
   
   # macOS/Linux
   chmod +x setup-wrapper.sh
   ./setup-wrapper.sh
   ```

3. **Open in Android Studio**
   - Open Android Studio
   - File → Open → Select the project folder
   - Wait for Gradle sync to complete

4. **Build the APK**
   ```bash
   ./gradlew assembleDebug
   ```

5. **Install on device**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

## Project Structure

```
app/src/main/kotlin/com/aichat/app/
├── data/
│   ├── local/          # Room DB, Security, DataStore
│   ├── remote/         # Ktor client, SSE parsing
│   ├── mapper/         # Entity ↔ Domain mappers
│   └── repository/     # Repository implementations
├── domain/
│   ├── model/          # Data models
│   ├── repository/     # Repository interfaces
│   ├── provider/       # AI provider abstraction
│   │   └── impl/       # 25 provider implementations
│   ├── usecase/        # Business logic
│   └── util/           # Utilities
├── di/                 # Hilt modules
└── ui/
    ├── components/     # Reusable composables
    ├── screens/        # Screen composables + ViewModels
    ├── navigation/     # Navigation graph
    └── theme/          # Material 3 theme
```

## Adding a New Provider

1. Add enum value to `ProviderType.kt`
2. Create `impl/NewProvider.kt` (extend `BaseOpenAiCompatibleProvider`)
3. Add `when` branch in `ProviderFactory.kt`
4. Add capabilities in `ProviderCapabilityDetector.kt`

## License

MIT License
