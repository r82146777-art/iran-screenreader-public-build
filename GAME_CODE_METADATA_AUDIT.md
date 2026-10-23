# Game code metadata audit

Date: 2026-10-23

This audit was performed using only the files already supplied by the user: `base.apk` and `base_source.zip`. No additional source files were assumed to exist.

## Confirmed archive facts
- `base.apk`: 947 ZIP entries, approximately 79 MiB.
- It contains `assets/bin/Data/Managed/Metadata/global-metadata.dat` (10,370,104 bytes), `assets/bin/Data/ScriptingAssemblies.json`, and `assets/bin/Data/data.unity3d`.
- The APK has zero `.so` entries, zero `.unity` scene files, zero `.prefab` files, and zero `.cs` source files.
- `base_source.zip`: 49,298 entries; primarily decompiled `smali*` folders and Android resources, not an editable Unity Editor project. It has no `.so`, `.unity`, `.prefab`, or `.cs` files.
- `ScriptingAssemblies.json` names `Assembly-CSharp.dll`, `UnityEngine.AccessibilityModule.dll`, TextMeshPro, RTLTMPro, and other managed assemblies. This list does not prove that the shipped UI has a TalkBack hierarchy.

## Useful game-specific names recovered from metadata strings
The IL2CPP metadata includes readable names that appear to describe actual game code:
- `WordGameEngine`, `FindWordsFromLetters`, `GenerateRandomLevel`, `LevelData`, `ScrambledLetters`
- `GameBoard`, `BoardInit`, `KeyboardInit`, `KeyboardDisplay`, `StartLevel`
- `GameMenu`, `StartGame`, `GamePuzzle`, `LevelSystem`, `GameStore`, `GameSettings`
- `PauseManager`, `PauseHints`, `FindFirstLetter`, `FindRandomLetter`, `FindEmptyLetters`
- UI/flow labels such as `MENU`, `Settings`, `HOW_TO_PLAY`, `START`, `Exit`, `NEXT_LEVEL`, `MENU_OPEN`, `MENU_CLOSE`, `VICTORY_LEVEL`

These are string/metadata findings, not reconstructed source code. They identify useful targets for a future source recovery effort, but do not reveal method bodies or enough exact scene structure to safely change game behavior.

## Why a reliable APK patch is still blocked
A Unity IL2CPP app normally needs its matching native IL2CPP binary for method-level analysis. The supplied APK has the metadata file but no native `.so` libraries, including no visible `libil2cpp.so` or `libunity.so`. The APK may be incomplete or packaged unusually; do not assume this finding means the original installed app is broken. Without the matching native library and a full project, static string extraction cannot reconstruct the app or insert a working accessibility hierarchy.

## Next step without asking the user for nonexistent source
Continue with the files already provided: investigate whether the asset bundle can be parsed, and determine whether any UI images/labels and Android activity wiring can be recovered. If no matching native libraries or rebuildable scene data can be recovered, report the precise blocker rather than claiming an accessible APK has been built.

## Accessibility requirement
Native TalkBack support requires exposing a deliberate accessibility hierarchy with labels, roles, focus order, and actions. Unity's official documentation explains that this hierarchy is not automatically inferred from the visual scene:
https://docs.unity.com/en-us/engine/6000.0/manual/platform-specific/cross-platform-features/mobile-accessibility
