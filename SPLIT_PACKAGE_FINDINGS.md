# Split APK Evidence — Package Metadata

## Confirmed by parsing the supplied APK's binary AndroidManifest.xml

- Package ID: `com.plus9.shahrbanoo`
- Version name: `5.80`
- Manifest application attribute: `isSplitRequired=true`
- Metadata `com.android.vending.splits.required=true`
- Metadata `com.android.vending.splits` points to `res/xml/splits0.xml`.
- `res/xml/splits0.xml` contains language configuration entries, including `fa` (Persian), `en`, and many other language codes.
- The supplied APK itself has no `.so` native libraries and no `lib/<abi>/` entries.

## Interpretation

This is stronger evidence than merely observing missing native libraries: the supplied APK declares that split delivery is required. The file appears to be a base APK from a split-enabled distribution, not necessarily a complete standalone APK. The manifest and split metadata alone do not reveal which exact additional APKs were installed on the original device or whether native libraries live in an ABI configuration split.

The original installed app may run normally when delivered with its complete split set. This inspection does not mean the original app is broken.

## Next required recovery step

Obtain the complete installed package set for the same app version, not just the base APK. It may include the base APK and ABI/configuration split APKs; depending on how the publisher packaged assets, additional asset packs or expansion data may also be needed. Preserve the original package and signing identity; do not combine unrelated APKs from different versions.

Once the complete package set is available:
1. inventory each APK's package/version/signature and split name;
2. identify the ABI-specific native libraries and any external Unity data;
3. determine whether a safe rebuild or accessibility overlay/bridge is technically possible;
4. build, sign, install, and test the exact result with TalkBack before publishing it.

## Evidence boundary

This is a static binary-manifest and archive inspection. No installation, runtime execution, APK patching, signing, or TalkBack validation has been performed. No modified APK is available yet.
