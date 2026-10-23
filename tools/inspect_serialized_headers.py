#!/usr/bin/env python3
"""Inspect basic headers of extracted Unity SerializedFiles (format v22+)."""
from pathlib import Path
import argparse
import struct

def inspect(path: Path):
    data = path.read_bytes()
    if len(data) < 48:
        return {"file": path.name, "bytes": len(data), "error": "shorter than modern header"}
    version = struct.unpack_from(">I", data, 8)[0]
    if version < 22:
        return {"file": path.name, "bytes": len(data), "format": version, "error": "legacy format not handled"}
    metadata_size = struct.unpack_from(">I", data, 20)[0]
    file_size = struct.unpack_from(">Q", data, 24)[0]
    data_offset = struct.unpack_from(">Q", data, 32)[0]
    endian = data[40]
    metadata = data[48:48 + metadata_size]
    nul = metadata.find(b"\0")
    unity_version = metadata[:nul].decode("ascii", "replace") if nul >= 0 else "unknown"
    p = nul + 1 if nul >= 0 else 0
    platform = struct.unpack_from("<i" if endian == 0 else ">i", metadata, p)[0] if p + 4 <= len(metadata) else None
    p += 4
    typetree = bool(metadata[p]) if p < len(metadata) else None
    return {
        "file": path.name, "bytes": len(data), "format": version,
        "unity_version": unity_version, "metadata_size": metadata_size,
        "header_file_size": file_size, "data_offset": data_offset,
        "platform_id": platform, "typetree_enabled": typetree,
    }

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("path", type=Path, help="Extracted SerializedFile or directory")
    args = ap.parse_args()
    paths = sorted(args.path.iterdir()) if args.path.is_dir() else [args.path]
    for path in paths:
        if path.is_file() and not path.name.endswith(".resS"):
            print(inspect(path))

if __name__ == "__main__":
    main()
