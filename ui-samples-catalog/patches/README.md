# Patches for the vendored foundation samples

The vendored tree under `../src/main/kotlin/upstream/` is upstream's bytes, re-fetched
byte-identically on every import. A fix is a patch here — never an edit in place — with its reason
in its own header, so the next import re-applies it and a patch that stops applying **fails** the
import rather than silently reverting the fix it carried.

Prefer a patch to a quarantine entry wherever the fix is small enough to state: quarantine is
per file, so it takes out every sample in the file, including the ones that render perfectly well.
See `samples/patches/README.md` for how the material3 corpus' patches are written and cut.

This corpus' import passes its own paths:

```
node scripts/import-samples.mjs --manifest ui-samples-catalog/import.json \
  --patches ui-samples-catalog/patches --quarantine ui-samples-catalog/quarantine.json \
  --out ui-samples-catalog/src/main/kotlin/upstream
```

| Patch | Why |
| --- | --- |
| `0001-seed-random-samples.patch` | five samples drew from the unseeded `kotlin.random.Random`, so they rendered differently every run and the preview-diff bot flagged them on every PR |
