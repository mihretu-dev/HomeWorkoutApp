# 🏋️‍♂️ Training Hub - Home Workout App

A modern, offline-first Android fitness application built with Kotlin and Jetpack Compose. Designed for custom workout tracking, guided exercise execution, live rest timers, and personal record tracking.

Developed by **Mihretu Hizkel** • Contact: mihretu-dev

---

## 🔥 Key Features

- **44+ Exercises Library:** Comprehensive coverage of Bodyweight, Dumbbell, Barbell, Pull-Up Bar, and Dip Station exercises with offline human athlete demonstration GIFs.
- **Smart Filtering & Search:** Search exercises in real time and filter by Target Muscle Group or Equipment with multi-selection support.
- **Custom Routine Management:** Expandable routine cards with step-by-step exercise breakdowns, custom exercise reordering, and full edit capabilities.
- **Guided Active Workouts & Get-Ready Countdown:** Step-by-step workout execution queue with live interactive timers, 3-second get-ready countdown, rest countdowns, and haptic/sound cues.
- **AI Voice Coach:** TextToSpeech spoken countdowns, exercise start guidance, rest announcements, and completion celebrations.
- **Customizable Daily Workout Reminders:** Time Picker, workout day selectors (M..S), and motivational notification styles powered by WorkManager.
- **Form Guides & History:** Detailed exercise instruction screens featuring historical log data (Reps, Weight, Max Hold) and PR tracking.
- **Offline & Private:** 100% local data persistence using Room DB and WorkManager.

---

## 🛠️ Tech Stack

- **UI:** Jetpack Compose, Material 3 (Dark Theme + Electric Lime Accents)
- **Architecture:** MVVM, Clean Architecture
- **Database:** Room DB (Offline First)
- **Image/GIF Loading:** Coil
- **Voice Engine:** Android TextToSpeech (TTS)
- **Background Work:** WorkManager
- **Language:** 100% Kotlin

---

## 🚀 Getting Started

1. Clone the repo:
   ```bash
   git clone https://github.com/mihretu-dev/HomeWorkoutApp.git
   ```
2. Open in Android Studio or VS Code and run:
   ```bash
   ./gradlew installDevelopmentDebug
   ```
