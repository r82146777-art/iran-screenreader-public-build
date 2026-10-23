# Unity Native Runtime Dependency Audit

## Scope
Inspected the supplied `base.apk` and `base_source.zip` directly. This is a static archive inspection, not an installation or runtime test.

## Confirmed findings
- `base.apk` is a structurally readable ZIP archive with 947 entries.
- The APK contains Unity player Java classes and Unity-managed content, including `assets/bin/Data/ScriptingAssemblies.json` and `assets/bin/Data/data.unity3d`.
- The APK contains **zero files ending in `.so`**, and no `lib/` directory entries.
- The decompiled `base_source.zip` also contains no `.so` files. This is expected for a source/decompilation export and does not independently prove the installed app lacks libraries.
- The decompiled `com/unity3d/player/UnityPlayer.java` explicitly attempts to load `libmain.so` and then invokes Unity's `NativeLoader.load(...)`. That makes the absence of native libraries in the supplied APK a material packaging concern.
- The supplied files do not include an `.obb`, `.apks`, or `.aab` package. The APK also has no obvious native-library split APK embedded within it.

## Interpretation (not a runtime diagnosis)
A normal Unity Android player needs native runtime libraries. Android App Bundle delivery can place ABI-specific native libraries in configuration split APKs, and Unity can also distribute game data separately (for APK builds, commonly an OBB). Therefore, the current evidence is consistent with a partial/base package or an APK that was repackaged without native libraries; it does **not** establish which happened. The original app may work when installed from its complete Play-delivered package set.

## Why this blocks an accessible rebuilt APK
The available companion ZIP is decompiled Java/Smali/resources, not the original Unity project. The recovered Unity scene data has no serialized TypeTrees, and the package currently available lacks the native player libraries needed to construct a reliable runnable Unity APK from these files alone. A C# accessibility prototype in this repository is research code only; it has not been injected, compiled, signed, or tested in the supplied app.

## Next technical path
1. Recover the complete installed package set for the exact app/version (base APK plus any ABI/configuration splits and asset packs/OBB, if used).
2. Confirm package identity/version and native ABI files from that complete set.
3. Continue UI/scene accessibility mapping and implement a tested accessibility bridge only after the complete runtime inputs are available.
4. Validate with TalkBack on-device: focus order, labels, button actions, keyboard entry, game-stage transitions, popups, and Persian speech.

## Integrity hashes of supplied inputs
- `base.apk` SHA-256: `57226a7a6e1ddbce15f0205a3641c65306930008c429761ef0d6ed1634f4e418`
- `base_source.zip` SHA-256: `b3d2c52cf60e4c2364eece320cedb6418ba67a90f5a33b78bbec00a9bf1a85ba`

## References
- Android Developers, inspecting native libraries in APKs: https://developer.android.com/games/optimize/64-bit
- Unity, APK expansion files and split application binary: https://docs.unity.cn/2021.1/Documentation/Manual/android-OBBsupport.html
- Unity, introduction to asset splitting: https://docs.unity.cn/Manual/android-asset-splitting.html
