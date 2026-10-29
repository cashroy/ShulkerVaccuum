"""Zips resourcepack/ into ShulkerVaccuum-pack.zip and prints its SHA-1 for server.properties.

Entries are sorted and timestamps fixed, so the SHA-1 only changes when the pack's contents do.
Run from the repo root:  python3 tools/build-pack.py
"""
import hashlib
import os
import zipfile

ROOT = 'resourcepack'
OUT = 'ShulkerVaccuum-pack.zip'

files = sorted(os.path.relpath(os.path.join(d, f), ROOT) for d, _, names in os.walk(ROOT) for f in names)
with zipfile.ZipFile(OUT, 'w', zipfile.ZIP_DEFLATED) as pack:
    for rel in files:
        info = zipfile.ZipInfo(rel.replace(os.sep, '/'), date_time=(2026, 1, 1, 0, 0, 0))
        info.compress_type = zipfile.ZIP_DEFLATED
        with open(os.path.join(ROOT, rel), 'rb') as f:
            pack.writestr(info, f.read())

with open(OUT, 'rb') as f:
    print(f'{OUT}: {len(files)} files, sha1 {hashlib.sha1(f.read()).hexdigest()}')
