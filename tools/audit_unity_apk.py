#!/usr/bin/env python3
"""Static archive check for Unity Android APK native runtime files.

Usage: python tools/audit_unity_apk.py path/to/app.apk
This checks archive contents only; it does not install or execute the app.
"""
from __future__ import annotations

import hashlib
import sys
import zipfile
from pathlib import Path

EXPECTED_UNITY_LIBS = {"libmain.so", "libunity.so"}
NATIVE_SUFFIX = ".so"


def main() -> int:
    if len(sys.argv) != 2:
        print("Usage: python tools/audit_unity_apk.py path/to/app.apk")
        return 2

    apk = Path(sys.argv[1])
    if not apk.is_file():
        print(f"ERROR: file not found: {apk}")
        return 2

    digest = hashlib.sha256()
    with apk.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)

    try:
        with zipfile.ZipFile(apk) as archive:
            bad_member = archive.testzip()
            names = archive.namelist()
    except (zipfile.BadZipFile, OSError) as exc:
        print(f"ERROR: cannot read APK as ZIP: {exc}")
        return 1

    native = [name for name in names if name.lower().endswith(NATIVE_SUFFIX)]
    unity_expected = sorted(
        name for name in native if Path(name).name in EXPECTED_UNITY_LIBS
    )
    abi_dirs = sorted(
        {"/".join(name.split("/")[:2]) for name in native if name.startswith("lib/")}
    )

    print(f"APK: {apk}")
    print(f"SHA-256: {digest.hexdigest()}")
    print(f"ZIP entries: {len(names)}")
    print(f"ZIP integrity: {'OK' if bad_member is None else 'FAILED at ' + bad_member}")
    print(f"Native .so files: {len(native)}")
    print(f"ABI directories: {', '.join(abi_dirs) if abi_dirs else '(none)'}")
    print(f"Unity player libraries found: {', '.join(unity_expected) if unity_expected else '(none)'}")
    if native:
        print("Native library entries:")
        for name in native:
            print(f"  {name}")

    if bad_member is not None:
        return 1
    if not native:
        print("WARNING: no native libraries are packaged in this APK archive.")
        print("Check whether ABI/configuration split APKs or another package set is required.")
        return 3
    if not unity_expected:
        print("WARNING: native libraries exist, but libmain.so/libunity.so were not found.")
        print("This is a heuristic check; confirm the Unity player/runtime configuration.")
        return 4
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
