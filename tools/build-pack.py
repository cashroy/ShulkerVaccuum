"""Zips resourcepack/ into ShulkerVaccuum-pack.zip and records its SHA-1 in pom.xml and the README.

Entries are sorted and timestamps fixed, so the SHA-1 only changes when the pack's contents do.
Run from the repo root:  python3 tools/build-pack.py
"""
import hashlib
import os
import re
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
    sha1 = hashlib.sha1(f.read()).hexdigest()

# The plugin sends the pack with this hash (via config.yml); the README shows it for server.properties users.
for path, pattern, replacement in (
    ('pom.xml', r'<pack\.sha1>[0-9a-f]{40}</pack\.sha1>', f'<pack.sha1>{sha1}</pack.sha1>'),
    ('README.md', r'resource-pack-sha1=[0-9a-f]{40}', f'resource-pack-sha1={sha1}'),
):
    with open(path) as f:
        text = f.read()
    with open(path, 'w') as f:
        f.write(re.sub(pattern, replacement, text))

print(f'{OUT}: {len(files)} files, sha1 {sha1}')
