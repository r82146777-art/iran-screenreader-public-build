#!/usr/bin/env python3
"""Extract serialized files from a UnityFS bundle embedded in an APK.

Usage:
  python extract_unityfs.py base.apk --output extracted

Dependency: pip install lz4
This extracts UnityFS entries; it does not reconstruct a Unity Editor project
or make the app accessible by itself.
"""
from __future__ import annotations

import argparse
import os
import struct
import zipfile
from pathlib import Path

import lz4.block


def align(value: int, multiple: int = 16) -> int:
    return (value + multiple - 1) // multiple * multiple


def parse_bundle(data: bytes) -> tuple[bytes, list[tuple[int, int, int, str]]]:
    if not data.startswith(b"UnityFS\0"):
        raise ValueError("Input is not a UnityFS bundle")
    p = data.index(b"\0") + 1
    format_version = struct.unpack_from(">I", data, p)[0]
    p += 4
    unity_version_end = data.index(b"\0", p)
    unity_version = data[p:unity_version_end].decode("utf-8", "replace")
    p = unity_version_end + 1
    revision_end = data.index(b"\0", p)
    revision = data[p:revision_end].decode("utf-8", "replace")
    p = revision_end + 1
    bundle_size, compressed_info_size, uncompressed_info_size, flags = struct.unpack_from(">QIII", data, p)
    header_end = p + 20
    if bundle_size != len(data):
        raise ValueError(f"Bundle size mismatch: header={bundle_size}, actual={len(data)}")

    # Flag 0x80 means block info is stored at the end; otherwise it follows
    # the header, aligned to 16 bytes in this build.
    if flags & 0x80:
        info_offset = len(data) - compressed_info_size
        payload_start = align(header_end)
    else:
        info_offset = align(header_end)
        payload_start = align(info_offset + compressed_info_size)

    info_blob = data[info_offset:info_offset + compressed_info_size]
    compression = flags & 0x3F
    if compression == 0:
        info = info_blob
    elif compression in (2, 3):
        info = lz4.block.decompress(info_blob, uncompressed_size=uncompressed_info_size)
    else:
        raise ValueError(f"Unsupported block-info compression mode: {compression}")

    p = 16  # 16-byte bundle hash
    block_count = struct.unpack_from(">I", info, p)[0]
    p += 4
    blocks: list[tuple[int, int, int]] = []
    for _ in range(block_count):
        uncompressed, compressed, block_flags = struct.unpack_from(">IIH", info, p)
        p += 10
        blocks.append((uncompressed, compressed, block_flags))

    node_count = struct.unpack_from(">I", info, p)[0]
    p += 4
    nodes: list[tuple[int, int, int, str]] = []
    for _ in range(node_count):
        offset, size, node_flags = struct.unpack_from(">QQI", info, p)
        p += 20
        end = info.index(b"\0", p)
        name = info[p:end].decode("utf-8", "replace")
        p = end + 1
        nodes.append((offset, size, node_flags, name))

    # In this Unity 2022.3 bundle, block data starts at the 16-byte aligned
    # position after the header and block-info payload.
    if flags & 0x80:
        payload_start = align(header_end)
    raw = bytearray()
    offset = payload_start
    for index, (uncompressed, compressed, block_flags) in enumerate(blocks):
        chunk = data[offset:offset + compressed]
        if len(chunk) != compressed:
            raise ValueError(f"Truncated compressed block {index}")
        mode = block_flags & 0x3F
        if mode == 0:
            decoded = chunk
        elif mode in (2, 3):
            decoded = lz4.block.decompress(chunk, uncompressed_size=uncompressed)
        else:
            raise ValueError(f"Unsupported compression mode {mode} in block {index}")
        if len(decoded) != uncompressed:
            raise ValueError(f"Unexpected size in block {index}: {len(decoded)} != {uncompressed}")
        raw.extend(decoded)
        offset += compressed

    print(f"UnityFS format {format_version}; Unity {unity_version} ({revision})")
    print(f"Decompressed {len(raw):,} bytes; found {len(nodes)} embedded entries")
    return bytes(raw), nodes


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("apk", type=Path, help="Path to APK containing assets/bin/Data/data.unity3d")
    parser.add_argument("--output", type=Path, default=Path("unity_extracted"))
    args = parser.parse_args()

    with zipfile.ZipFile(args.apk) as apk:
        bundle = apk.read("assets/bin/Data/data.unity3d")
    raw, nodes = parse_bundle(bundle)
    args.output.mkdir(parents=True, exist_ok=True)
    for offset, size, _flags, name in nodes:
        destination = args.output / name
        destination.parent.mkdir(parents=True, exist_ok=True)
        destination.write_bytes(raw[offset:offset + size])
        print(f"{name} ({size:,} bytes)")


if __name__ == "__main__":
    main()
