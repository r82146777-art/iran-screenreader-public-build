# Unity asset string audit (2026-10-23)

## What was checked
- Examined the supplied `base.apk` and companion `base_source.zip`.
- The APK contains `assets/bin/Data/data.unity3d` (~46 MB), `global-metadata.dat` (~10.4 MB), and `ScriptingAssemblies.json`.
- The APK archive lists no `.so` native libraries. This is unusual for a standalone Unity IL2CPP Android APK and may mean the supplied APK is incomplete, split/modified, or packaged in an atypical way. It must be verified against the original installation before attempting to patch/rebuild.
- The companion ZIP is a decompiled Android project (Smali/Java/resources) rather than the original Unity Editor project; it does not supply scenes, prefabs, scripts, or build settings in editable source form.

## Strings found in the Unity data
Printable strings in `data.unity3d` include likely scene/asset labels such as:
- `Assets/GameSDK/TermsOfService/Scene`
- `SCENE_01_SPLASH`
- `MENU`, `Menu_v2`, `MENU_TOP`
- `Settings`, `HOW_TO_PLAY`, `START`, `Exit`, `Loading`
- `Gameplay_v3`, `GameBoard`, `LetterBlock_Startup`
- `PiggyBank_Menu`, `OfferMenu`, `AntiquesListCanvas2`
- `ButtonClick`, `ButtonRoot`, `Canvas`, `TextMesh`
- `Plus9Games` and `shahrbanoogame@gmail.com`

These are strings extracted from the serialized Unity data, not a verified full scene hierarchy. They help identify likely UI areas, but do not provide enough information to reliably assign accessible labels, actions, or screen-reader focus order.

## Important limitation
No modified APK has been built or tested. A C# script placed in this repository is only source code; it is not injected into the supplied APK. The current prototype that uses speech synthesis and keyboard navigation is not native TalkBack support.

## Next technically valid path
1. Obtain the original Unity project (especially `Assets`, `Packages`, `ProjectSettings`, scenes and prefabs), or obtain a complete, verified APK/AAB including the matching native libraries and app version.
2. Confirm whether this is an IL2CPP build and recover enough metadata/assets to map actual controls and actions. Static string scraping alone cannot do that.
3. Implement native screen-reader semantics in a rebuildable Unity project. Unity documents its Accessibility module/AssistiveSupport APIs for native screen readers; these APIs require an explicitly constructed and synchronized accessibility hierarchy rather than automatically making every Unity control accessible.
4. Build, sign, install and test with TalkBack on a real Android device, covering menus, gameplay, dialogs, state changes and focus order.

## Source reference
Unity's current official mobile accessibility documentation: https://docs.unity.com/en-us/engine/6000.0/manual/platform-specific/cross-platform-features/mobile-accessibility
