# Shahrbanoo Accessible native rebuild

This is an independent Android reconstruction using Kotlin and Java, not the original Unity source.

Current features: 30 sample Persian word-puzzle stages, Android native controls intended for TalkBack, Persian Android TTS when installed, synthesized device tone effects for taps/menu/start/stage/hint/correct/wrong/win, hints, answer checking, backspace, clear and restart.

Voice note: speech uses the Persian voice data available on the device. The APK does not bundle a natural Persian speech model. If the device has no Persian TTS voice, install a Persian-capable TTS engine/data. Effects are synthesized Android tones, not the original game's music or sound assets.

Limits: the supplied archives did not contain the full Unity project, original art/audio files, or original stage database. The app id is `ir.iran.shahrbanoorebuild`, so it installs separately from `com.plus9.shahrbanoo`. This independent sample rebuild has not yet been device-tested with TalkBack.

Build from repo root: `./gradlew :game:assembleDebug`
Expected output: `game/build/outputs/apk/debug/game-debug.apk`
