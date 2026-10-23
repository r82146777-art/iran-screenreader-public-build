# Game package recovery findings — Shahrbanoo 5.80

## Why this investigation matters

The supplied file `base.apk` identifies as `com.plus9.shahrbanoo`, version `5.80`. Its manifest indicates that split package content is required. The supplied APK itself contains no `lib/` directory or `.so` native libraries, and the companion decompiled ZIP does not supply those native libraries or an editable Unity project.

## Public package listing found

A public APKFab listing for **شهربانو - بازی فکری ماجراجویی** identifies:

- Package: `com.plus9.shahrbanoo`
- Version: `5.80`, version code 90
- Updated: October 3, 2026
- Distribution format: XAPK / APK bundle
- Listed variants: `arm64-v8a` with `config.arm64_v8a`, and `armeabi-v7a` with `config.armeabi_v7a`
- Listing: https://apkfab.com/%D8%B4%D9%87%D8%B1%D8%A8%D8%A7%D9%86%D9%88-%D8%A8%D8%A7%D8%B2%DB%8C-%D9%81%DA%A9%D8%B1%DB%8C-%D9%85%D8%B9%D9%85%D8%A7%DB%8C%DB%8C/com.plus9.shahrbanoo

This is a lead for locating the complete package set, not confirmation that any third-party download has been downloaded, verified, or installed. Do not treat the listing alone as a trusted binary or as permission to redistribute the game.

## Next technical steps

1. Obtain the complete package set for the same version and choose the architecture matching the intended Android device.
2. Verify hashes and inspect the split contents before trusting or installing them.
3. Confirm where the native Unity/IL2CPP libraries are located and whether the package is complete.
4. Continue static UI/scene analysis. A full editable Unity project is still not present in the supplied archive.
5. Only publish a modified game APK after a real integration, clean build, signature check, install test, and TalkBack accessibility test.

## Current status — do not overstate

- The supplied files have been inspected and static scene/control names recovered.
- The repository's existing successful Android build is the separate `ir.iran.screenreader` app, not the game.
- No accessible rebuild of `com.plus9.shahrbanoo` has been produced.
- No external XAPK has been downloaded or verified in this investigation.
- This file records the recovery lead and does not claim that the game has been fixed.


## Additional listing lead

A second public listing at https://barato.ir/shahrbanoo/ describes version 5.80 as a 125 MB direct APK. This listing has not been downloaded or verified either. Treat it as an alternative lead only; verify package ID, version code, signature, archive integrity, and native-library contents before use. A third-party claim that a file is "direct APK" does not establish that it is safe or complete.


## Local file cross-check (2026-10-23)

The supplied `/mnt/data/base.apk` was checked again locally:

- SHA-1: `955d6b40a0e0fa1f4e4b6fd985166ffe92a310d9`
- SHA-256: `57226a7a6e1ddbce15f0205a3641c65306930008c429761ef0d6ed1634f4e418`
- ZIP entries: 947
- Native `.so` files: 0
- Paths under `lib/`: 0
- Editable Unity `.unity`, `.prefab`, or `.cs` files: 0

The APKFab page reports variant SHA-1 values `dd1af8e0738cef249501545d97de58450de78c6d` (arm64-v8a) and `c8239be265a940218527cabf7555da28cfadd7fd` (armeabi-v7a). The supplied base APK SHA-1 matches neither listing value. This is consistent with the listing values referring to complete XAPK variants rather than this one base APK, but that interpretation is not independently verified. It means we cannot claim that the supplied APK is the complete package from that listing.

## Official developer support route

The Google Play listing for the same package is https://play.google.com/store/apps/details?id=com.plus9.shahrbanoo and lists developer support email `shahrbanoogame@gmail.com`. If binary patching proves infeasible, the reliable route is to request an accessible build or permission/source access from the developer. No message has been sent.

