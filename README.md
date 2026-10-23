# Android accessibility investigation

This repository is tracking work toward making the supplied Android app usable by blind users with TalkBack.

## Honest status
- The supplied APK and a decompiled ZIP were received and inspected at a high level.
- The APK contains Unity-related assets and Android/Java libraries.
- The original Unity project (scene files, prefabs, build settings) has not been supplied.
- No modified APK has been built or signed yet.

## Current work
See [the accessibility plan](ACCESSIBILITY_PLAN.md) and [the Unity navigation prototype](unity/AccessibleUiNavigator.cs).

The prototype is not a finished integration and is not an APK. It must be integrated into the original Unity project, tested with TalkBack, and refined before release.
