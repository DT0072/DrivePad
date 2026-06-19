# 🚘 DrivePad 

**DrivePad** is a premium, production-ready Android automotive infotainment system engineered specifically to transform standard tablets—such as the Huawei MatePad 11 (DBY-W09)—into dedicated, in-car dashboard displays. 

Built with modern Android architecture and Jetpack Compose, DrivePad bypasses the need for root access or custom ROMs. It operates as a standard sideloadable APK with dual-intent capabilities, meaning it can run as a normal application or entirely replace the default home screen as a dedicated vehicle launcher.

## ✨ Key Features

### 🎨 Automotive "Glassmorphism" UI
- Custom-built design system optimized for landscape orientation and arm's-length touch interactions.
- Dynamic "GlassCard" components with subtle gradients, blurs, and satisfying press-scale micro-animations.
- Persistent Automotive Status Bar providing real-time system data (Time, Date, Battery Level, WiFi/BT status) and live weather data.

### 📻 FM Radio Simulator (Internet Radio)
Since standard tablets lack physical FM tuners, DrivePad integrates the open **Radio Browser API** to seamlessly stream over 50,000 global radio stations. 
- Features a custom-built, draggable Canvas frequency dial for an authentic "tuning" experience.
- Supports 6 customizable preset buttons and live equalizer animations.

### 📱 Phone Projection Hub
A centralized portal designed to handle external phone projection dongles and software:
- **Android Auto**: Auto-detects and launches Headunit Reloaded or Headunit Revived.
- **Apple CarPlay**: Auto-detects and launches AutoKit (for Carlinkit USB dongles).
- Includes built-in setup guides if the required projection handlers are missing.

### 🎵 Universal Media Center
- A unified media dashboard displaying current playback status with a spinning vinyl animation.
- One-tap intent routing to popular media sources: Spotify, YouTube Music, Huawei Music, and Local Storage.
- Controls active Android media sessions for play/pause, next, previous, seeking, and volume.

### 🗺️ Smart Navigation
- Embeds Google Navigation SDK maps and turn-by-turn guidance directly inside DrivePad when a Google Maps Platform key is configured.
- Retains the built-in OpenStreetMap map and Google Maps handoff when Google Navigation is not configured.
- Supports destination search, route selection, traffic display, and quick actions for Home, Work, and nearby fuel.

### ⚡ Vehicle Automation Settings
- **Power Trigger**: Utilizes `PowerConnectionReceiver` to automatically launch DrivePad the moment the tablet receives power from the vehicle's ignition/USB port, and optionally minimize when power is cut.
- **Boot Trigger**: Automatically starts the infotainment system when the tablet boots up.
- **Theme Modes**: Day, Night, and Auto (system-matched) modes optimized for safe driving visibility.

## 🏗️ Technical Architecture
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture
- **Navigation**: Jetpack Navigation3
- **Local Storage**: Room Database (Destinations, Presets) & DataStore (User Preferences)
- **Networking**: Ktor Client & Kotlinx Serialization
- **APIs**: Open-Meteo (Weather), Radio Browser API (Radio)

## 📦 Installation (No Root Required)
1. Build the APK via Gradle: `./gradlew assembleDebug`
2. Enable **Developer Options** and **USB Debugging** on your Huawei MatePad.
3. Push the APK to the tablet via ADB:
   ```bash
   adb install -r app-debug.apk
   ```
4. **Optional**: To use DrivePad as your persistent dashboard, navigate to `Settings -> Launcher Mode` within the app, then press your tablet's home button and select DrivePad as the default Home app.
5. Open the Media screen and tap **Enable media access** once. Android requires notification-listener access before DrivePad can read and control playback from YouTube Music, Spotify, or Huawei Music.

## Google Navigation setup

Google requires a billing-enabled Cloud project and an API key authorized for **Navigation SDK for Android**. Restrict the key to the `com.drivepad.app` Android application and its release signing certificate, then add it to the ignored `local.properties` file:

```properties
MAPS_API_KEY=your_google_maps_platform_key
```

The key is injected into the APK at build time and is never committed to Git. Builds without the key continue to use DrivePad's OpenStreetMap fallback and open Google Maps externally for live guidance.
