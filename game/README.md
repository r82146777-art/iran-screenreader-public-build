# Shahrbanoo Accessible Native Rebuild

This is an independent Android reconstruction, not the original Unity project or original APK.

## Features
- Persian word-puzzle gameplay with 100 stages, with longer and less familiar words introduced in later groups.
- Native Android controls with TalkBack descriptions.
- The letter keyboard is not rebuilt after each letter tap, helping TalkBack retain focus on the selected keyboard item.
- Bundled offline Persian neural voice (Piper VITS through Sherpa-ONNX); gameplay speech does not call the phone's system TTS.
- Settings for sound effects, a generated electronic melody, high contrast, larger text, and speech rate.
- Hints, answer checking, backspace, clear, restart, and spoken stage clues.
- Synthesized audio cues for taps, menus, stages, hints, correct/wrong answers, and winning.

## Voice and audio limitations
Speech uses the Persian voice data available on the device; the APK does not bundle a natural Persian speech model. If the device has no Persian TTS voice, install a Persian-capable TTS engine/data. The optional melody and sound cues are generated electronically and are not the original game's music, voice actor, or sound assets.

## Important limits
The supplied archive did not contain the full Unity project, original art/audio files, or the original stage database. This app uses application ID `ir.iran.shahrbanoorebuild`, so it installs separately from `com.plus9.shahrbanoo`. The code changes are not a substitute for testing with TalkBack on a physical Android device; focus behavior, speech, and audio should be verified on the target phone.

## Build
From the repository root:

```sh
./gradlew --no-daemon :game:assembleDebug
```

Expected output: `game/build/outputs/apk/debug/game-debug.apk`
