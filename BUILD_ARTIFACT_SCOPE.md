# Build artifact scope clarification

## Finding

The repository's GitHub Actions workflow named `Build APK` runs:

```sh
./gradlew :app:assembleDebug
```

The Gradle app module declares:

- Android namespace: `ir.iran.screenreader`
- application ID: `ir.iran.screenreader`
- version name: `5.0.1`

The supplied Unity game APK declares package ID `com.plus9.shahrbanoo` and version name `5.80`.

These are different Android applications. Therefore, an APK produced by this repository's current `app` module is the standalone Iran Screen Reader app, **not a rebuilt or accessibility-patched copy of the supplied Unity game**. A successful workflow run or an artifact named `iran-screenreader-v5-apk` must not be presented as the modified game APK.

## What the workflow can prove

A successful run proves only that the repository's Android app module compiled and packaged. It does not prove that the Unity game has been modified, that the Unity game's native libraries/splits are complete, or that TalkBack can navigate the game's screens.

## What is still required for the requested game modification

1. Obtain a complete installable package set for the Unity game (the supplied base APK declares split packages are required).
2. Recover or obtain the original Unity project, or establish a safe, technically viable method to modify the built game.
3. Integrate accessibility at the actual game UI layer; a separate screen-reader app or an unintegrated C# prototype does not alter the game.
4. Build, sign, install, and test the modified game with TalkBack, including menus, tutorial, gameplay, dialogs, and buttons.

## Current status

No modified build of package `com.plus9.shahrbanoo` has been produced or verified in this repository.